"""
vision/camera.py
Camera object which you can open and close.
"""

import SimpleCV


class Camera:

    camera = None

    def __init__(self):
        self.camera = SimpleCV.Camera(0)
        #self.camera = SimpleCV.JpegStreamCamera("141.252.231.82:8080/?action=stream&dummy=.mjpg")


    def release(self):
        del self.camera