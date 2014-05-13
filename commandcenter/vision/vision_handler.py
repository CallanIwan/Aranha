"""
vision/vision_handler.py
Basically a wrapper to real vision functions.
Handles the commands which have to with Computer Vision (find balloon etc)
"""

from commandcenter.vision.camera import Camera
from commandcenter.vision import balloon


class Handler:

    cam = None

    def __init__(self, n):
        self.cam = Camera(n)

    def __stop(self):
        self.cam.release()

    def find_balloon(self, color):
        balloon.find(self.cam.camera, color)
        self.__stop()