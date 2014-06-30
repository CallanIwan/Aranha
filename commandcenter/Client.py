import socket
import time
import json
import SimpleCV

from navigator.NetworkManager import NetworkManager
from navigator import OilStateMachine
from navigator.OilStateMachine import OilStateMachine, startState
from vision import BalloonFinder
from vision.CardFinder import CardFinder
from vision.BalloonFinder import startState
from vision.camera import Camera

CARD = chr(10) + ""
BALLOON = chr(12) + ""
OIL = chr(13) + ""

class Client(object):
    cam = None
    cardCheckList = []
    fn = None
    staticImage = None
    pause = False

    def __init__(self, ip=None, camOff=False, staticImage=None):
            self.networkManager = NetworkManager(ip)
            self.cam = Camera(ip) if camOff != True and staticImage == None else None
            self.staticImage = SimpleCV.Image(staticImage) if staticImage != None else None



    def run(self):
        #create the 3 "mode object"
        #self.balloonFinder = BalloonFinder()
        self.cardFinder = CardFinder()
        self.fsm = OilStateMachine()


        while True:
            mode = self.networkManager.fetchMode()
            if self.staticImage == None:
                frame = self.cam.getImage() if self.cam != None else None
            else:
                frame = self.staticImage

            #switch-statement to select the mode (given by driver @ raspberry)
            if mode == 'idle' or self.pause == True:
                time.sleep(0.3);


            elif mode == CARD:
                cards =  self.cardFinder.findColorOrder(frame)

                if cards != None:
                    self.cardCheckList.append(cards)

                if len(self.cardCheckList) > 1 and all(x == self.cardCheckList[-1] for x in self.cardCheckList[-3:]):
                    cmd = json.dumps(cards)
                    self.networkManager.sendCommand(self.networkManager.H_CARDS + cmd)
                    print cmd




            elif mode == BALLOON:
                #initial state, only set when it has not been set yet (therefor, initial!)
                self.fn = BalloonFinder.startState(self.networkManager, frame, "blue") if self.fn == None else self.fn

                #trampoline!
                if callable(self.fn):
                    self.fn = self.fn(frame, "red")




            #the oilfinder state-machine does not return periodically, but handles a main loop itself
            #todo: make the oilFinder interuptable
            elif mode == OIL:
                self.fsm.run(startState, self.networkManager)





#c = Client("10.0.0.2", staticImage="C:\Users\Remco\Dropbox\Beliebers\snapshots\\licht\\image3.jpg")
c = Client("10.0.0.2")#), staticImage="C:\Users\Remco\Dropbox\Beliebers\snapshots\\licht\\image3.jpg")
c.run()



