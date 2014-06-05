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
import base64


class WifiServer:

    def __init__(self):
        self.server = SocketServer.TCPServer(("0.0.0.0", 9999), WifiClientHandler)

    def start(self):
        print("Started TCP Server on 0.0.0.0:9999")
        t = Thread(target=self.server.serve_forever)    # start tcpserver inside thread to prevent locking main thread
        t.daemon = True
        t.start()


class WifiClientHandler(SocketServer.BaseRequestHandler):

    def handle(self):
        # self.request is the TCP socket connected to the client
        data = self.request.recv(1024)
        print "{} wrote:".format(self.client_address[0])
        print data
        print len(data)
        protocol.handle(self, data)

    def encode_and_send(self, header, msg):
        msg = base64.b64encode(msg)
        print "strlen msg:", len(msg)
        self.request.sendall(header + "" + str(len(msg)) + chr(0))
        self.request.sendall(msg)
