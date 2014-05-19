from threading import Thread
from control import ps3_handler as ps3, app_handler as app
from types import *
import time


class ControlHandler(Thread):

    def __init__(self):
        super(ControlHandler, self).__init__()
        self.daemon = True
        self.ps3handler = ps3.PS3Handler()
        self.ps3handler.start()

    def run(self):
        while True:
            self.poll_ps3()
            self.poll_app()
            time.sleep(0.0005)

    def poll_ps3(self):
        x = self.ps3handler.poll()
        if x:
            print x

    def poll_app(self):
        pass

    def set_ps3(self, name, val):
        if name is "sixaxis" and type(val) is BooleanType:
            self.ps3handler.sixaxis = val