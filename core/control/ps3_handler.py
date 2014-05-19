"""
control/ps3_handler.py
Basically a mapping for the PS3 controller
"""

import control_interface as ci
import time
import os
import struct


class PS3Handler(ci.Control):

    JS_EVENT_BUTTON = 0x01  # button pressed/released
    JS_EVENT_AXIS = 0x02    # joystick moved
    JS_EVENT_INIT = 0x80    # initial state of device

    button_names = ""

    state = None

    def __init__(self):
        super(PS3Handler, self).__init__()
        self.fd = os.open("/dev/input/js0", os.O_RDONLY)

    def run(self):
        while True:
            self.sample(os.read(self.fd, 7*64))
            time.sleep(0.05)

    def sample(self, event):
        _bytes = []
        for c in event:
            _bytes.append(ord(c))
            if len(_bytes) == 8:
                if _bytes[4] == 1:
                    print "pressed", _bytes[7]
                _bytes = []

    def close(self):
        self.fd.close()

    def move(self, mov):
        print mov

    def isbutton(self, c):
        return (c & self.JS_EVENT_BUTTON) != 0

    def isaxis(self, c):
        return (c & self.JS_EVENT_AXIS) != 0

    def isinitialstate(self, c):
        return (c & self.JS_EVENT_INIT) != 0