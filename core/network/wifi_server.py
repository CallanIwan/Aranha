"""
vision/wifi_server.py
Blueprint of TCP/UDP server
"""

import SocketServer


class WifiServer:

    def __init__(self):
        self.server = SocketServer.TCPServer(("localhost", 9999), WifiClientHandler)

    def start(self):
        print("Started TCP Server on localhost:9999")
        self.server.serve_forever()


class WifiClientHandler(SocketServer.BaseRequestHandler):
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    data = None

    def handle(self):
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(1024).strip()
        print "{} wrote:".format(self.client_address[0])
        print self.data
        # just send back the same data, but upper-cased
        self.request.sendall(self.data.upper())
