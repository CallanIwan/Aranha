"""
vision/camera.py
Camera object which you can open and close.
"""

import SimpleCV
import numpy as np

#wrapper class for SimpleCV.Camera/cv2.VideoCapture
class Camera:

    isStreaming = False
    camera = None

    def __init__(self, ip=None):
        if ip == None:
            self.camera = SimpleCV.Camera(0)
        else:
            self.isStreaming = True
            self.camera = SimpleCV.cv2.VideoCapture() #
            self.camera.open("http://" + ip + ":8080/?action=stream&dummy=.mjpg")

    # wrapper for grabbing frames from eiher a network object or the local webcam
    def getImage(self):
        if self.isStreaming == False:
           return self.camera.getImage()

        elif self.isStreaming == True:
            ret, frame = self.camera.read( )

            #HACK! SimpleCV.JpegStreamCamera simply does NOT WORK
            #instead use lower-level cv2.VideoCapture, however converting cv2.Mat to SimpleCV.Image is very hacky
            #not applying this hack will result in an image rotated 90deg and BGR interpreted as RGB
            transImg = frame.transpose(1,0,2) # transpose the rows and columns
            transColImg = transImg[:,:,::-1] # change from BGR to RGB
            img = SimpleCV.Image(transColImg, cv2image=True)
            cpy = img.copy()

            return cpy