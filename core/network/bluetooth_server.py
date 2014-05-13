"""
vision/bluetooth_server.py
Starts a bluetooth server with max 7 channels.
Receives and sends data from/to paired client.
PS3 controller: receive only
Android App: receive/send
"""

import time
import base64
from threading import Thread
from bluetooth import *
import protocol


class BluetoothServer(Thread):

    def __init__(self):
        super(BluetoothServer, self).__init__()
        self.daemon = True
        self.server_sock = BluetoothSocket(RFCOMM)
        self.server_sock.bind(("", PORT_ANY))
        self.server_sock.listen(1)

        self.port = self.server_sock.getsockname()[1]

        # advertise service
        uuid = "9d7debbc-c85d-11d1-9eb4-006008c3a19a"
        advertise_service(self.server_sock, "Aranha Motherfuckersss",
                          service_id=uuid,
                          service_classes=[uuid, SERIAL_PORT_CLASS],
                          profiles=[SERIAL_PORT_PROFILE])

    def run(self):
        while True:
            print("Waiting for connection on RFCOMM channel %d" % self.port)
            client_sock, client_info = self.server_sock.accept()
            print("Accepted connection from ", client_info)
            BluetoothClientThread(self.server_sock, client_sock, client_info).start()
        # close server socket
        self.server_sock.close()


class BluetoothClientThread(Thread):

    def __init__(self, server_sock, client_sock, client_info):
        super(BluetoothClientThread, self).__init__()
        self.daemon = True
        self.server_sock = server_sock
        self.client_sock = client_sock
        self.client_info = client_info

    def run(self):
        try:
            while True:
                data = base64.b64decode(self.client_sock.recv(1024))
                if len(data) == 0:
                    break
                print data
                self.client_sock.send(base64.b64encode(data))
        except IOError:
            pass

        # close connection
        print("Closed connection from ", self.client_info)
        self.client_sock.close()
        self.is_alive = False