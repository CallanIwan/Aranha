from threading import Lock
import time

#this object is supposed to be globally accessible somewhere
#and updated in another thread.  clojure agents would handy for that
from navigator.Ray import Ray


class SpiderState(object):
    state = None
    world = None
    lock = Lock()

    #starts an infinite loop, indefinitely fetching state info from the server
    def run(self, networkManager, world):
        self.update(networkManager, world, block=True)

        while(True):
            time.sleep(0.1)
            #self.lock.acquire()
            #print "inside run-lock"
            self.update(networkManager, world, block=True)
            #self.lock.release()
            #print "exit run-lock"


    #for internal use, you probably want to run "run" in a new thread
    def update(self, networkManager, world, block=False):
        newState = networkManager.fetchState(block)
        if newState != None:
            self.state = newState
            world.applyRay(Ray(self.state.pos, self.state.rot, self.state.dist))


    def getState(self):
        #self.lock.acquire()
        #print "inside getState"
        retState = self.state
        #self.lock.release()

        #print "exit getState"
        return retState


