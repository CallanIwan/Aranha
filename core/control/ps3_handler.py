"""
control/ps3_handler.py
Basically a mapping for the PS3 controller
"""

import control_interface as ci
import Queue


class PS3Handler(ci.Control):

    # enable/disable sixaxis, default = False
    sixaxis = False

    # queue of commands
    cmd_queue = Queue.Queue()

    # byte 4 (value)
    JS_EVENT_BUTTON_RELEASED = 0x00
    JS_EVENT_BUTTON_PRESSED = 0x01

    # byte 6 (action)
    JS_EVENT_BUTTON = 0x01  # button pressed/released
    JS_EVENT_AXIS = 0x02    # axis moves
    JS_EVENT_INIT = 0x80    # initial state of device

    # byte 7 (button (and axis) 'ids')
    button_names = ["select", "L3", "R3", "start", "up", "right", "down",
                    "left", "L2", "R2", "L1", "L2", "triangle", "circle", "cross", "square", "home"]

    axis_names = ["L3_axis_x", "L3_axis_y", "R3_axis_x", "R3_axis_y",
                  "sixaxis_x", "sixaxis_y", "sixaxis_z", "7", "up_pressure", "right_pressure",
                  "down_pressure", "left_pressure", "L2_pressure", "R2_pressure", "L1_pressure",
                  "R1_pressure",  "triangle_pressure", "circle_pressure", "cross_pressure",
                  "square_pressure", "20", "21", "22", "23", "24", "25", "26", "27", "28"]

    state = None

    def __init__(self):
        super(PS3Handler, self).__init__()
        self.file = open("/dev/input/js2", "r")

    def run(self):
        event = []
        while True:
            for c in self.file.read(1):
                event += ['%02X' % ord(c)]
                if len(event) == 8:
                    action = int(event[6], 16)
                    val = int(event[4], 16)
                    name = int(event[7], 16)
                    if not self.isinitialstate(action):
                        if self.isbutton(action):
                            self.cmd_queue.put((self.button_names[name], val))
                        if self.isaxis(action) and not self.issixaxis(name):
                            self.cmd_queue.put((self.axis_names[name], val))
                    event = []

    def poll(self):
        if self.cmd_queue.qsize() > 0:
            return self.cmd_queue.get()
        return False

    def close(self):
        self.file.close()

    def isbutton(self, c):
        return (c & self.JS_EVENT_BUTTON) != 0

    def isaxis(self, c):
        return (c & self.JS_EVENT_AXIS) != 0

    def isinitialstate(self, c):
        return (c & self.JS_EVENT_INIT) != 0

    def ispressed(self, c):
        return (c & self.JS_EVENT_BUTTON_PRESSED) != 0

    def issixaxis(self, c):
        if self.sixaxis:
            return False
        return 4 <= c <= 6