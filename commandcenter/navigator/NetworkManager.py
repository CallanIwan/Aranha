import json
import socket

#little helper class for deserializing json
import time


class StateClass(object):
    pos = None
    rot = None
    dist = None
    def __init__(self, pos, rot, dist):
        self.pos = pos
        self.rot = rot
        self.dist = dist

#needed its own class so that it can be passed around at the oilfinder statemachine
class NetworkManager(object):
    # header bytes, intended for the app to know what to receive
    H_IMAGE = "" + chr(1)
    H_SENSOR = "" + chr(2)
    H_VISION = "" + chr(3)
    H_MOV = "" + chr(4)
    H_MODE = "" + chr(5)

    H_CARDS = "" + chr(10)

    def __init__(self, ip):
        if ip != None:
            self.socket = self.connectToStream(ip, 9999)
        else:
            self.socket = None

    #returns a new socket object, just use the constructor instead
    def connectToStream(self, ip, port):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((ip, port))
        return s

    #internal use, converts the deserialized hashmap to actual object
    def stateDecoder(self, obj):
        s = obj["sensors"]
        newObj = StateClass( s["position"]["value"], s["rotation"]["value"], s["ultrasone"]["value"])

        return newObj

    #check if there is data to be collected, presumed to be state
    def fetchState(self, block=False):
        if self.socket != None:
            response = None

            self.sendCommand(self.H_SENSOR)

            #todo make more robust
            if block == True:
                while response == None:
                    time.sleep(0.2)
                    response = self.socket.recv(1024)
            else:
                response = self.socket.recv(1024)

            #print the json we received
            print "Received: {}".format(response)
            if response:
                try:
                    return self.stateDecoder(json.loads(response))
                except ValueError:
                    return None

    #todo: implement this at driver, hardcoded locally for the time being
    def fetchMode(self):
        self.sendCommand(self.H_MODE)
        #return 'card'

    #sends plain strings, probably want to use the command implementations, eg, forward/rotate
    def sendCommand(self, cmd):
        if self.socket != None:
            self.socket.sendall(cmd)


    #constantly spam these commands to emulate a controller
    def forward(self):
        self.sendCommand(self.H_MOV + ";0")

    def rotate(self, deg):
        self.sendCommand(self.H_MOV + ";" + `deg`)
