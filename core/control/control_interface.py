"""
control/control_interface.py
These functions are the simplest definition of a control handler.
run(): poll data
poll(): poll queue (queue contains the polled data)
"""

from threading import Thread
import Queue


class Control(Thread):

    # queue of commands
    cmd_queue = Queue.Queue()

    def __init__(self):
        super(Control, self).__init__()
        self.daemon = True

    def run(self):
        raise NotImplementedError("Should have implemented this")

    def poll(self):
        raise NotImplementedError("Should have implemented this")