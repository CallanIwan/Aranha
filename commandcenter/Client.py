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
    cardCheckList = []

    def __init__(self, ip=None, camOff=False):
            self.networkManager = NetworkManager(ip)
            self.cam = Camera(ip) if camOff != True else None



    def run(self):
        #create the 3 "mode object"
        self.balloonFinder = BalloonFinder()
        self.cardFinder = CardFinder()
        self.fsm = OilStateMachine()

        while True:
            mode = self.networkManager.fetchMode()
            frame = self.cam.getImage() if self.cam != None else None

            #switch-statement to select the mode (given by driver @ raspberry)
            if mode == 'card':
                cards =  self.cardFinder.findColorOrder(frame)

                if cards != None:
                    self.cardCheckList.append(cards)

                if len(self.cardCheckList) > 3 and all(x == self.cardCheckList[-1] for x in self.cardCheckList[-3:]):
                    cmd = json.dumps(cards)
                    self.networkManager.sendCommand(self.networkManager.H_CARDS + cmd)
                    print cmd

            elif mode == 'balloon':
                cmd = self.balloonFinder.findBalloon(frame, 'blue')
                if cmd == "left":
                    NetworkManager.rotate(-15)
                if cmd == "right":
                    NetworkManager.rotate(15)
                if cmd == "forward":
                    NetworkManager.forward()
            #the oilfinder state-machine does not return periodically, but handles a main loop itself
            #todo: make the oilFinder interuptable
            elif mode == "oil":
                self.fsm.run(startState, self.networkManager)
            elif mode == 'idle':
                time.sleep(0.3);

#c = Client()
c = Client("10.0.0.2")#, camOff=True)
c.run()