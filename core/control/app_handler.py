"""
control/app_handler.py
Receive control commands from app
"""

import control_interface as ci
import time


class AppHandler(ci.Control):

    def __init__(self):
        super(AppHandler, self).__init__()
        self.daemon = True

    def run(self):
        while True:
            time.sleep(1)

    def poll(self):
        pass