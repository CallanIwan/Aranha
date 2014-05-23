"""
vision/wifi_server.py
Blueprint of TCP/UDP server
"""

import SocketServer
import protocol
from threading import Thread


class WifiServer:

    def __init__(self):
        self.server = SocketServer.TCPServer(("0.0.0.0", 9999), WifiClientHandler)

    def start(self):
        print("Started TCP Server on 0.0.0.0:9999")
        t = Thread(target=self.server.serve_forever)
        t.daemon = True
        t.start()


class WifiClientHandler(SocketServer.BaseRequestHandler):

    def handle(self):
        # self.request is the TCP socket connected to the client
        data = self.request.recv(1024)
        print "{} wrote:".format(self.client_address[0])
        print data
        if ord(data) is ord(protocol.H_SENSOR):
            # just send back the same data, but upper-cased
            self.request.sendall("tnx Remco 4 sensor stats brah")
        if ord(data) is ord(protocol.H_MOV_RECV):
            print "Remco sent: ", data
            self.request.sendall("wow Remco, stuur je me zomaar move shizzle :)")
