import ps3_handler as ps3
import app_handler as app


class ControlHandler:

    def __init__(self):
        self.ps3handler = ps3.PS3Handler()
        self.ps3handler.start()
        self.apphandler = app.AppHandler()
        self.apphandler.start()

    def poll_ps3(self):
        for x in range(0, self.ps3handler.cmd_queue.qsize()):
            action, value = self.ps3handler.poll()
            control = self.ps3handler.movementcontrol
            x_zero = False
            y_zero = False
            if action[:7] == self.ps3handler.movementcontrol:
                print action, " <=> ", value
                if control + "_x" == action:
                    x_zero = True if value == 0 else False
                    if value == 0 and y_zero:
                        self.ps3handler.movementstate = ps3.MOV_INIT
                        self.ps3handler.movementspeed = value
                    elif 0 > value <= 127:
                        pass
                elif control + "_y" == action:
                    y_zero = True if value == 0 else False
                    if value == 0 and x_zero:
                        self.ps3handler.movementstate = ps3.MOV_INIT
                        self.ps3handler.movementspeed = value
                    elif value <= 127:
                        self.ps3handler.movementstate = ps3.MOV_DOWN
                        self.ps3handler.movementspeed = value
            print "State: ", self.ps3handler.movementstate
            print "Speed: ", self.ps3handler.movementspeed

    def poll_app(self):
        for x in range(0, self.apphandler.cmd_queue.qsize()):
            x = self.apphandler.poll()
            if x:
                print x

    def set_ps3(self, flags):
        self.ps3handler.config_flags = flags
