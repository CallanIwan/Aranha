"""
control/ps3_handler.py
Receive and sanitize output from PS3 controller
"""

import control_interface as ci
import time

# config flags
C_SIXAXIS = 0x01
C_ANALOG_AXIS = 0x02
C_PRESSURE = 0x04
C_PRESSED = 0x08
C_RELEASED = 0x10

# movement flags
MOV_UP = "UP"
MOV_RIGHT = "RIGHT"
MOV_DOWN = "DOWN"
MOV_LEFT = "LEFT"
MOV_INIT = "INIT"


class PS3Handler(ci.Control):

    # the control used for movement
    movementcontrol = "L3_axis"

    # sanitized state of the controller
    movementstate = MOV_INIT

    # speed
    movementspeed = 0

    # enable/disable config flags
    config_flags = C_ANALOG_AXIS | C_PRESSURE | C_PRESSED

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
        self.file = None
        self.isconnected = False

    def run(self):
        event = []
        while True:
            if not self.isconnected:
                try:
                    self.file = open("/dev/input/js0", "r")
                    self.isconnected = True
                except IOError:
                    pass
                time.sleep(1)
            else:
                try:
                    data = self.file.read(8)
                    for c in data:
                        event += ['%02X' % ord(c)]
                        if len(event) == 8:
                            axisval = int(event[5], 16)
                            action = int(event[6], 16)
                            val = int(event[4], 16)
                            name = int(event[7], 16)
                            if not self.isinitialstate(action):
                                # buttons
                                if self.isbutton(action):
                                    cmd = (self.button_names[name], val)
                                    if self.ispressed(val):
                                        self.cmd_queue.put(cmd)
                                    elif self.isreleased(val):
                                        self.cmd_queue.put(cmd)
                                # axis
                                if self.isaxis(action):
                                    cmd = (self.axis_names[name], axisval)
                                    if self.ispressure(name):
                                        self.cmd_queue.put(cmd)
                                    elif self.issixaxis(name):
                                        self.cmd_queue.put(cmd)
                                    elif self.isanalogaxis(name):
                                        self.cmd_queue.put(cmd)
                            event = []
                    time.sleep(0.001)
                except IOError:
                    self.isconnected = False

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
        if not self.config_flags & C_PRESSED:
            return False
        return (c & self.JS_EVENT_BUTTON_PRESSED) != 0

    def isreleased(self, c):
        if not self.config_flags & C_RELEASED:
            return False
        return True

    def ispressure(self, c):
        if not self.config_flags & C_PRESSURE:
            return False
        return 8 <= c <= 19

    def isanalogaxis(self, c):
        if not self.config_flags & C_ANALOG_AXIS:
            return False
        return 0 <= c <= 3

    def issixaxis(self, c):
        if not self.config_flags & C_SIXAXIS:
            return False
        return 4 <= c <= 6

    def getmovementstate(self, action):
        pass