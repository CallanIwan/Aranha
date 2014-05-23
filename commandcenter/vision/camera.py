"""
vision/camera.py
Camera object which you can open and close.
"""

import SimpleCV
import numpy as np


class Camera:

    isStreaming = False
    camera = None

    def __init__(self, ip=None):
        if ip == None:
            self.camera = SimpleCV.Camera(0)
        else:
            self.isStreaming = True
            self.camera = SimpleCV.cv2.VideoCapture()
            self.camera.open(ip)


    def getImage(self):
        if self.isStreaming == False:
           return self.camera.getImage()

        elif self.isStreaming == True:
            ret, frame = self.camera.read( )

            transImg = frame.transpose(1,0,2) # transpose the rows and columns
            transColImg = transImg[:,:,::-1] # change from BGR to RGB
            img = SimpleCV.Image(transColImg, cv2image=True)
            cpy = img.copy()

            return cpy