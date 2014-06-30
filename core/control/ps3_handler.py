"""
control/ps3_handler.py
Receive and sanitize output from PS3 controller
"""

import control_interface as ci
import time
import binascii

# config flags
#: Sixaxis byte.
C_SIXAXIS = 0x01
#: Analog stick byte.
C_ANALOG_AXIS = 0x02
#: Pressure byte.
C_PRESSURE = 0x04
#: Button pressed byte.
C_PRESSED = 0x08
#: Button released byte.
C_RELEASED = 0x10

# movement flags
MOV_UP = "UP"
MOV_RIGHT = "RIGHT"
MOV_DOWN = "DOWN"
MOV_LEFT = "LEFT"
MOV_INIT = "INIT"


class PS3Handler(ci.Control):

    #: The control used for movement
    movementcontrol = "L3_axis"

    #: Sanitized state of the controller
    movementstate = MOV_INIT

    #: Speed
    movementspeed = 0

    # Ranges of x and y
    OUTER_RANGE = 30  # range of sides which is considered a move (deadzone)
    X_LEFT_L = 128
    X_LEFT_R = 255
    X_RIGHT_L = 0
    X_RIGHT_R = 127
    Y_UP_UP = 255
    Y_UP_DOWN = 128
    Y_DOWN_UP = 0
    Y_DOWN_DOWN = 127

    # Boolean flags for checking if INIT state
    x_zero = False
    y_zero = False

    # Enable/disable config flags
    config_flags = C_ANALOG_AXIS | C_PRESSURE | C_PRESSED

    #: Byte 4 (value)
    JS_EVENT_BUTTON_RELEASED = 0x00
    JS_EVENT_BUTTON_PRESSED = 0x01

    # byte 6 (action)
    JS_EVENT_BUTTON = 0x01  # button pressed/released
    JS_EVENT_AXIS = 0x02    # axis moves
    JS_EVENT_INIT = 0x80    # initial state of device

    # byte 7 (button (and axis) 'ids')
    #: List of all the button names.
    button_names = ["select", "L3", "R3", "start", "up", "right", "down",
                    "left", "L2", "R2", "L1", "L2", "triangle", "circle", "cross", "square", "home"]
    #: List of all the axis names.
    axis_names = ["L3_axis_x", "L3_axis_y", "R3_axis_x", "R3_axis_y",
                  "sixaxis_x", "sixaxis_y", "sixaxis_z", "7", "up_pressure", "right_pressure",
                  "down_pressure", "left_pressure", "L2_pressure", "R2_pressure", "L1_pressure",
                  "R1_pressure",  "triangle_pressure", "circle_pressure", "cross_pressure",
                  "square_pressure", "20", "21", "22", "23", "24", "25", "26", "27", "28"]

    E_ACTION = 5
    E_AXIS_VAL = 6
    E_BUTTON_VAL = 4
    E_NAME = 7

    def __init__(self):
        """
        Init
        @return:
        """
        super(PS3Handler, self).__init__()
        self.file = None
        self.isconnected = False
        self.counted = 1

    # read from joystick file and put events into queue
    def run(self):
        """
        Open and read joystick file. Read events from it and sanitize commands and put them in a queue, which are handled by control_handler.
        @return:
        """
        while True:
            if not self.isconnected:
                try:
                    self.file = open("/dev/input/js0", "r")             # open joystick file
                    self.isconnected = True                             # joystick is connected, if file can be opened
                except IOError:
                    pass
                time.sleep(1)                                           # if not connected, wait for it
            else:
                try:
                    event = []
                    data = self.file.read(8)                            # read 8 bytes (1 event) a time
                    for c in data:
                        event += ['%02X' % ord(c)]                      # input to hexadecimal, and add to array
                        if len(event) == 8:                             # if event is completely read
                            if self.counted > 30:
                                cmd = "move"
                                move = int(event[self.E_NAME], 16)
                                if move == 8:
                                    self.cmd_queue.put((cmd, "0"))
                                elif move == 9:
                                    self.cmd_queue.put((cmd, "90"))
                                elif move == 10:
                                    self.cmd_queue.put((cmd, "180"))
                                elif move == 11:
                                    self.cmd_queue.put((cmd, "270"))
                            self.counted += 1
                            continue
                            # convert events to decimal value
                            axisval = int(event[self.E_AXIS_VAL], 16)   # axis value
                            action = int(event[self.E_ACTION], 16)      # action (button press)
                            val = int(event[self.E_BUTTON_VAL], 16)     # value of action (0 or 1 button pressed)
                            name = int(event[self.E_NAME], 16)          # name of button
                            if not self.isinitialstate(action):         # dont read initial state
                                # buttons
                                if self.isbutton(action):
                                    cmd = (self.button_names[name], val)
                                    # add event to queue, if flag is enabled
                                    if self.ispressed(val):
                                        self.cmd_queue.put(cmd)
                                    elif self.isreleased():
                                        self.cmd_queue.put(cmd)
                                # axis
                                if self.isaxis(action):
                                    cmd = (self.axis_names[name], axisval)
                                    # add event to queue, if flag is enabled
                                    if self.ispressure(name):
                                        self.cmd_queue.put(cmd)
                                    elif self.issixaxis(name):
                                        self.cmd_queue.put(cmd)
                                    elif self.isanalogaxis(name):
                                        self.cmd_queue.put(cmd)
                            event = []                                  # reset event array
                    time.sleep(0.001)                                   # save some cpu-time
                except IOError:
                    self.isconnected = False                            # disconnected controller, go in wait loop

    # get queue
    def poll(self):
        if self.cmd_queue.qsize() > 0:
            return self.cmd_queue.get()
        return False

    def close(self):
        """
        close joystick file
        """
        self.file.close()

    def isbutton(self, c):
        """
        Checks if c is a button.
        @param c: object to check.
        @return: boolean
        """
        return (c & self.JS_EVENT_BUTTON) != 0

    def isaxis(self, c):
        """
        Checks if c is a Axis.
        @param c: object to check.
        @return: boolean
        """
        return (c & self.JS_EVENT_AXIS) != 0

    def isinitialstate(self, c):
        """
        Checks if the object is a initial state.
        @param c: object to check.
        @return: boolean
        """
        return (c & self.JS_EVENT_INIT) != 0

    def ispressed(self, c):
        """
        Checks if the object is pressed.
        @param c: object to check.
        @return: boolean
        """
        if not self.config_flags & C_PRESSED:
            return False
        return (c & self.JS_EVENT_BUTTON_PRESSED) != 0

    def isreleased(self):
        """
        Checks if the object is released.
        @param c: object to check.
        @return: boolean
        """
        if not self.config_flags & C_RELEASED:
            return False
        return True

    def ispressure(self, c):
        """
        Checks if the object is pressured.
        @param c: object to check.
        @return: boolean
        """
        if not self.config_flags & C_PRESSURE:
            return False
        return 8 <= c <= 19     # 8 to 19 are pressure events

    def isanalogaxis(self, c):
        """
        Checks if the object is a analog axis.
        @param c: object to check.
        @return: boolean
        """
        if not self.config_flags & C_ANALOG_AXIS:
            return False
        return 0 <= c <= 3      # 0 tot 3 are analog axis events

    def issixaxis(self, c):
        """
        Checks if the object is six axis.
        @param c: object to check.
        @return: boolean
        """
        if not self.config_flags & C_SIXAXIS:
            return False
        return 4 <= c <= 6      # 4 to 6 are sixaxis events (x, y and z)

    # return movementstate, movementstate is calculated in this function
    def getmovementstate(self):
        """

        @return:
        """
        for x in range(0, self.cmd_queue.qsize()):
            action, value = self.poll()
            control_x = self.movementcontrol + "_x"     # the x-axis action
            control_y = self.movementcontrol + "_y"     # the y-axis action
            #print action, " <=> ", value
            #if action[:7] == self.movementcontrol:      # filter on defined movementcontrol output
            #    print action, " <=> ", value            # debug, action <=> value

            """
            control_x = control + "_x"
            control_y = control + "_y"
            #prevstate = self.movementstate
            if action[:7] == self.movementcontrol:
                #print action, " <=> ", value
                if self.x_zero and self.y_zero:
                    self.movementstate = MOV_INIT
                if control_x == action:
                    self.x_zero = True if (value < 128 and 98 > value <= 0) or (225 > value <= 128) else False
                    if 128 >= value <= 158:
                        self.movementstate = MOV_LEFT
                elif control_y == action:
                    self.y_zero = True if (value < 128 and 98 > value <= 0) or (225 > value <= 128) else False
                    if 98 <= value <= 127:
                        self.movementstate = MOV_DOWN
            """
        return self.movementstate