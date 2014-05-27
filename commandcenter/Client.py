import socket
import time
import json
import SimpleCV


import PIL
from navigator.OilStateMachine import *
from vision.CardFinder import CardFinder
from vision.BalloonFinder import BalloonFinder
from vision.camera import Camera


class StateClass(object):
    pos = None
    rot = None
    dist = None
    def __init__(self, pos, rot, dist):
        self.pos = pos
        self.rot = rot
        self.dist = dist

def stateDecoder(obj):
    s = obj["sensors"]
    newObj = StateClass(s["ultrasone"]["value"], s["rotation"]["value"], s["position"]["value"])

    return newObj

def fetchState(s):
    response = s.recv(1024)
    print "Received: {}".format(response)
    if response:
        return stateDecoder(json.loads(response))
    else:
        return None

def fetchMode(s):
    return 'oil'

def sendCommand(s, cmd):
    s.sendall(cmd)


class Client(object):
    cam = None

    def __init__(self, ip=None):
            self.cam = Camera(ip)
            self.socket = self.connectToStream(ip, 9999)


    def connectToStream(self, ip, port):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((ip, port))
        return s

    def run(self):

        self.balloonFinder = BalloonFinder()
        self.cardFinder = CardFinder()

        while True:
            if self.socket != None:
                sendCommand(self.socket, chr(2))
                fetchState(self.socket)

            mode = fetchMode(self.socket)
            frame = self.cam.getImage()

            if mode == 'card':
                cmd =  self.cardFinder.findColorOrder(frame)
                print json.dumps(cmd)
            elif mode == 'balloon':
                cmd = self.balloonFinder.findBalloon(frame, 'red')
                #print json.dumps(cmd)
                #self.sendCommand(cmd)
                #print cmd
            elif mode == "oil":
                fsm = OilStateMachine()
                fsm.run(startState, self.socket)
            elif mode == 'idle':
                time.sleep(0.3);

c = Client("10.0.0.2")#"http://10.0.0.2:8080/?action=stream&dummy=.mjpg")
c.run()
