from threading import Thread
import time
from network import network_handler
from control import control_handler


class Aranha(Thread):

    def __init__(self):
        super(Aranha, self).__init__()
        self.daemon = True
        self.ch = control_handler.ControlHandler()
        self.nh = network_handler.NetworkHandler(self.ch.apphandler)

    def run(self):
        while True:
            # movement
            self.ch.poll_ps3()
            self.ch.poll_app()
            time.sleep(0.001)