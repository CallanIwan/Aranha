"""
aranha.py
Start control & network handler.
Controls get polled here, TODO: movements are given to the embedded software.
"""

from threading import Thread
import time
from network import network_handler
from control import control_handler
import driver


class Aranha(Thread):

    def __init__(self):
        super(Aranha, self).__init__()
        self.daemon = True
        self.driver = driver.Driver()
        self.driver.start()
        self.ch = control_handler.ControlHandler(self.driver)
        self.nh = network_handler.NetworkHandler(self.ch.apphandler, self.driver)

    def run(self):
        while True:
            # movement
            self.ch.poll_ps3()
            self.ch.poll_app()
            time.sleep(0.001)