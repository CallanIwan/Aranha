"""
control/control_handler.py
Start PS3 and App handler. This class handles all the controls which are given
by the PS3 controller and app.
"""

import ps3_handler as ps3
import app_handler as app


class ControlHandler:

    def __init__(self, driver):
        self.driver = driver
        self.ps3handler = ps3.PS3Handler()
        self.ps3handler.start()
        self.apphandler = app.AppHandler()
        self.apphandler.start()
        self.movementstate = self.ps3handler.movementstate

    def poll_ps3(self):
        #prevstate = self.movementstate
        #self.movementstate = self.ps3handler.getmovementstate()
        #if prevstate != self.movementstate:
        #    print self.movementstate
        print self.ps3handler.cmd_queue.get()

    def poll_app(self):
        for x in range(0, self.apphandler.cmd_queue.qsize()):
            x = self.apphandler.poll()
            if x:
                print x

    def set_ps3(self, flags):
        self.ps3handler.config_flags = flags    # set configuration bitflags for the PS3 controller
