"""
control/ps3_handler.py
Basically a mapping for the PS3 controller
"""

from threading import Thread
import time


class PS3Handler(Thread):

    state = None

    def __init__(self):
        super(PS3Handler, self).__init__()
        self.daemon = True

    def run(self):
        while True:
            time.sleep(0.025)
