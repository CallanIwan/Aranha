import socket
import time
import json
import SimpleCV


import PIL
from vision.CardFinder import CardFinder
from vision.BalloonFinder import BalloonFinder
from vision.camera import Camera


class Client(object):
    cam = None
    socket = None

    def __init__(self, ip=None):
            self.cam = Camera(ip)

    def fetchState(self):
        response = self.socket.recv(1024)
        print "Received: {}".format(response)

    def fetchMode(self):
        return 'balloon'

    def sendCommand(self, cmd):
        self.socket.sendall(cmd)




    def connectToStream(self, ip, port):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((ip, port))
        return s

    def run(self):

        balloonFinder = BalloonFinder()
        cardFinder = CardFinder()

        while True:
            if self.socket != None:
                self.sendCommand(chr(3) + "forward")
                self.fetchState()

            mode = self.fetchMode()
            frame = self.cam.getImage()

            if mode == 'card':
                cmd =  cardFinder.findColorOrder(frame)
                print json.dumps(cmd)
            elif mode == 'balloon':
                cmd = balloonFinder.findBalloon(frame, 'red')
                #print json.dumps(cmd)
                #self.sendCommand(cmd)
                #print cmd
            elif mode == 'idle':
                time.sleep(0.3);

c = Client()#"http://10.0.0.2:8080/?action=stream&dummy=.mjpg")
c.run()
