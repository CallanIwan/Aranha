from threading import Lock
import time

#this object is supposed to be globally accessible somewhere
#and updated in another thread.  clojure agents would handy for that
class SpiderState(object):
    state = None
    lock = Lock()

    #starts an infinite loop, indefinitely fetching state info from the server
    def run(self, networkManager):
        while(True):
            self.lock.acquire()
            self.update(networkManager)
            self.lock.release()
            time.sleep(0.1)


    #for internal use, you probably want to run "run" in a new thread
    def update(self, networkManager):
        self.state = networkManager.fetchState()

    def getState(self):
        self.lock.acquire()
        retState = self.state
        self.lock.release

        return retState


