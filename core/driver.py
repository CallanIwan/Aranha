"""
kernel.py
A wrapper which wraps calls to the kernel module
"""

from threading import Thread
import time
import main
import atexit


class Driver(Thread):

    def __init__(self):
        super(Driver, self).__init__()
        self.daemon = True
        main.init()
        atexit.register(self.dispose)

    def run(self):
        while True:
            time.sleep(1)

    def move(self, direction):
        #print "spider.move(" + str(direction) + ")"
        main.move(direction)

    def turn(self, direction):
        #print "spider.turn(" + str(direction) + ")"
        main.turn(direction)

    def stabilize(self):
        main.stabilize()

    def dispose(self):
        #print "spider.dispose()"
        main.dispose()
