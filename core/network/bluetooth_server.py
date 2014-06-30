"""
network/bluetooth_server.py
Starts a bluetooth server with max 7 connections.
Receives and sends data from/to paired client.
PS3 controller: receive only
Android App: receive/send
"""
import base64
from threading import Thread
from bluetooth import *
import protocol
import thread
import atexit

PACKET_SIZE = 990


class BluetoothServer(Thread):

    def __init__(self, apphandler, driver):
        super(BluetoothServer, self).__init__()
        self.daemon = True                              # if main is killed, this also gets killed
        self.apphandler = apphandler                    # pass apphandler object so we can pass control commands to it
        self.driver = driver
        self.server_sock = BluetoothSocket(RFCOMM)      # create socket
        self.server_sock.bind(("", PORT_ANY))           # bind to bluetooth adapter
        self.server_sock.listen(1)                      # server listens to accept 1 connection at a time

        self.port = self.server_sock.getsockname()[1]   # socket is bound on this port

        # advertise service
        uuid = "9d7debbc-c85d-11d1-9eb4-006008c3a19a"   # ID of the service
        advertise_service(self.server_sock, "Aranha",   # start service, so the app can connect
                          service_id=uuid,
                          service_classes=[uuid, SERIAL_PORT_CLASS],
                          profiles=[SERIAL_PORT_PROFILE])

        atexit.register(self.server_sock.close)

    # accept clients and create threads for them
    def run(self):
        while True:
            print("Waiting for connection on RFCOMM channel %d" % self.port)
            client_sock, client_info = self.server_sock.accept()    # wait for connection. if new connection, continue
            print("Accepted connection from ", client_info)
            # start client thread
            BluetoothClientThread(self.server_sock, client_sock, client_info, self.apphandler, self.driver).start()


class BluetoothClientThread(Thread):

    def __init__(self, server_sock, client_sock, client_info, apphandler, driver):
        super(BluetoothClientThread, self).__init__()
        self.daemon = True
        self.server_sock = server_sock
        self.client_sock = client_sock
        self.client_info = client_info
        self.apphandler = apphandler
        self.driver = driver
        atexit.register(self.client_sock.close)
        atexit.register(self.server_sock.close)

    def run(self):
        while True:
            try:
                data = base64.b64decode(self.client_sock.recv(1024))
                if len(data) == 0:
                    break
                thread.start_new_thread(protocol.handle, (self, data, self.driver))
            except IOError:
                break
        # close connection
        print("Closed connection from ", self.client_info)
        self.client_sock.close()
        self.is_alive = False

    def encode_and_send(self, header, msg):
        msg = base64.b64encode(msg)
        #print "strlen msg:", len(msg)
        self.client_sock.send(header + "" + str(len(msg)) + chr(0))
        self.client_sock.send(msg)