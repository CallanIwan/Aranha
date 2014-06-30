"""
network/wifi_server.py
Starts a TCP server, which is going to connect to the App
and the Commandcenter.
Receives and sends data from/to client.
Android App: receive/send
"""

import SocketServer
import protocol
from threading import Thread
import atexit
import base64

driver = None

class WifiServer:

    def __init__(self, _driver):
        global driver
        driver = _driver
        SocketServer.TCPServer.allow_reuse_address = True
        self.server = SocketServer.TCPServer(("0.0.0.0", 9999), WifiClientHandler)
        atexit.register(self.server.server_close)

    def start(self):
        print("Started TCP Server on 0.0.0.0:9999")
        t = Thread(target=self.server.serve_forever)    # start tcpserver inside thread to prevent locking main thread
        t.daemon = True
        t.start()


class WifiClientHandler(SocketServer.BaseRequestHandler):

    apparatus = None

    def handle(self):
        global driver
        # self.request is the TCP socket connected to the client
        while True:
            try:
                data = base64.b64decode(self.request.recv(1024))
                protocol.handle(self, data, driver)
            except IOError:
                break

    def encode_and_send(self, header, msg):
        if header == protocol.H_VISION_MODUS:
            self.apparatus = True
        #print "strlen msg:", len(msg)
        #self.request.sendall(msg)
        msg = base64.b64encode(msg)
        print msg
        #print "strlen msg:", len(msg)
        if not self.apparatus:
            self.request.sendall(header + "" + str(len(msg)) + chr(0))
        self.request.sendall(msg)
