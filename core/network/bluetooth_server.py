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

    def __init__(self, apphandler):
        super(BluetoothServer, self).__init__()
        self.daemon = True
        self.apphandler = apphandler
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
            BluetoothClientThread(self.server_sock, client_sock, client_info, self.apphandler).start()
            time.sleep(1)
        # close server socket
        self.server_sock.close()


class BluetoothClientThread(Thread):

    def __init__(self, server_sock, client_sock, client_info, apphandler):
        super(BluetoothClientThread, self).__init__()
        self.daemon = True
        self.server_sock = server_sock
        self.client_sock = client_sock
        self.client_info = client_info
        self.apphandler = apphandler

    def run(self):
        while True:
            try:
                data = base64.b64decode(self.client_sock.recv(1024))
                if len(data) == 0:
                    break
                # begin commands
                if len(data) == 1:
                    print "me gotz header:", ord(data)
                    if ord(data) is ord(protocol.H_IMAGE):
                        with open("/tmp/stream/pic.jpg", "rb") as image_file:
                            image = image_file.read()
                        self.encode_and_send(protocol.H_IMAGE, image)
                        print "Sending image"
                    elif ord(data) is ord(protocol.H_SENSOR):
                        self.encode_and_send(protocol.H_SENSOR, "JSON with sensor shiz")
                        print "Sending sensor data"
                    elif ord(data) is ord(protocol.H_VISION):
                        self.encode_and_send(protocol.H_VISION, "Ballon vinden;Dickpics analyseren")
                        print "Sending vision scripts"
            except IOError:
                break
        # close connection
        print("Closed connection from ", self.client_info)
        self.client_sock.close()
        self.is_alive = False

    def encode_and_send(self, header, msg):
        msg = base64.b64encode(msg)
        print "strlen msg:", len(msg)
        packet_size = chr(self.get_packet_size(len(msg)))
        self.client_sock.send(header + "" + packet_size)
        self.client_sock.send(msg)

    def get_packet_size(self, msglen):
        if msglen < 990:
            return 1
        packet_size = msglen / 990
        if (msglen % 990) > 0:
            packet_size += 1
        print "packet_size:", packet_size
        return packet_size