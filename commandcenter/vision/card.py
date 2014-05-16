import SimpleCV
from SimpleCV.base import *
import numpy as np
import time

from vision import debug as db

def findCard(cam):
    while True:#disp.isNotDone():
        frame = cam.camera.getImage()
        median = frame.medianFilter(13)
        edges = median.edges(30, 30)
        peak = median.huePeaks()

        green = (median.hueDistance(SimpleCV.Color.GREEN, minsaturation=70)*1.5).threshold(70)
        red = (median.hueDistance(SimpleCV.Color.RED, minsaturation=150)   *1.5).threshold(35)
        blue = (median.hueDistance(SimpleCV.Color.BLUE, minsaturation=50)  *1.7).threshold(70)

        a = median+green
        b = median+red
        c = median+blue




