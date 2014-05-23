"""
control/ext_control_handler.py
Basically a wrapper to real external controller functions
Handles the commands which have to do with receiving commands from clients
connected to the servers.
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