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

    max_threads = None
    server_threads = []

    def __init__(self, max_threads):
        super(BluetoothServer, self).__init__()
        self.daemon = True
        self.max_threads = max_threads
        for x in range(1, self.max_threads + 1):
            self.server_threads.append(BluetoothServerThread(x))
            self.server_threads[x - 1].start()

    def run(self):
        while True:
            time.sleep(1)
            for x in range(1, self.max_threads + 1):
                if not self.server_threads[x - 1].is_alive:
                    self.server_threads[x - 1] = BluetoothServerThread(x)
                    self.server_threads[x - 1].start()


class BluetoothServerThread(Thread):

    port = None
    server_sock = None
    client_sock = None

    def __init__(self, port):
        super(BluetoothServerThread, self).__init__()
        self.daemon = True
        self.port = port

    def run(self):
        self.server_sock = BluetoothSocket(RFCOMM)
        self.server_sock.bind(("00:15:83:6a:31:b7", self.port))
        self.server_sock.listen(self.port)

        # should advertise service once, so do it by port 1
        if self.port == 1:
            uuid = "9d7debbc-c85d-11d1-9eb4-006008c3a19a"
            advertise_service(self.server_sock, "Aranha Motherfuckersss",
                              service_id=uuid,
                              service_classes=[uuid, SERIAL_PORT_CLASS],
                              profiles=[SERIAL_PORT_PROFILE])

        print("Waiting for connection on RFCOMM channel %d" % self.port)
        self.client_sock, client_info = self.server_sock.accept()
        print("Accepted connection from ", client_info)

        try:
            while True:
                '''
                testing strings with base64 encoding/decoding
                most likely we are gonna use bits/bytes for raw commands
                and base64 for json encoding/decoding
                '''
                data = base64.b64decode(self.client_sock.recv(1024))
                self.client_sock.send(base64.b64encode(data))
                print data
                if len(data) == 0:
                    break
        except IOError:
            pass

        print("Closed connection from ", client_info)

        self.client_sock.close()
        self.server_sock.close()

        # hax0rz me th1nkz
        self.is_alive = False