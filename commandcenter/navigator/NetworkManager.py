import json
import socket

#little helper class for deserializing json
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
        newObj = StateClass(s["ultrasone"]["value"], s["rotation"]["value"], s["position"]["value"])

        return newObj

    #check if there is data to be collected, presumed to be state
    def fetchState(self):
        if self.socket != None:
            response = self.socket.recv(1024)
            #print the json we received
            print "Received: {}".format(response)
            if response:
                return self.stateDecoder(json.loads(response))

        return None

    #todo: implement this at driver, hardcoded locally for the time being
    def fetchMode(self):
        #todo implement
        return 'balloon'

    #sends plain strings
    def sendCommand(self, cmd):
        if self.socket != None:
            self.socket.sendall(cmd)
