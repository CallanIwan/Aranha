import socket
import time
import json
import SimpleCV

from navigator.NetworkManager import NetworkManager
from navigator import OilStateMachine
from navigator.OilStateMachine import OilStateMachine, startState
from vision.CardFinder import CardFinder
from vision.BalloonFinder import BalloonFinder
from vision.camera import Camera



class Client(object):
    cam = None

    def __init__(self, ip=None):
            self.cam = Camera(ip)
            self.networkManager = NetworkManager(ip)



    def run(self):
        #create the 3 "mode object"
        self.balloonFinder = BalloonFinder()
        self.cardFinder = CardFinder()
        self.fsm = OilStateMachine()

        while True:
            mode = self.networkManager.fetchMode()
            frame = self.cam.getImage()

            #switch-statement to select the mode (given by driver @ raspberry)
            if mode == 'card':
                cmd =  self.cardFinder.findColorOrder(frame)
                print json.dumps(cmd) #check validity of the color order & json format
            elif mode == 'balloon':
                cmd = self.balloonFinder.findBalloon(frame, 'blue')
            #the oilfinder state-machine does not return periodically, but handles a main loop itself
            #todo: make the oilFinder interuptable
            elif mode == "oil":
                self.fsm.run(startState, self.networkManager)
            elif mode == 'idle':
                time.sleep(0.3);

#c = Client()
c = Client("10.0.0.2")
c.run()