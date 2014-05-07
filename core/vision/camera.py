"""
vision/camera.py
Camera object which you can open and close.
"""

import SimpleCV


class Camera:

    camera = None

    def __init__(self, n):
        self.camera = SimpleCV.Camera(0)

    def release(self):
        del self.camera