import SimpleCV
from SimpleCV.base import *
import numpy as np
import time

from vision import debug as db

def findCard(cam):
    while True:#disp.isNotDone():
        frame = cam.camera.getImage()
        median = frame.gaussianBlur((5,5), 5, 7)
        median = median.medianFilter(7)

        #todo adaptive thresholding (binarize). perhaps to create a mask for more precise thresholding later on
        red = (median.hueDistance(SimpleCV.Color.RED, minsaturation=150, minvalue=50)   *2).threshold(35).invert()
        green = (median.hueDistance(SimpleCV.Color.GREEN, minsaturation=100, minvalue=50)*1).threshold(80).invert()
        blue = (median.hueDistance(SimpleCV.Color.BLUE, minsaturation=70, minvalue= 30)  *1.5).threshold(70).invert()

        redBlobs = red.erode(10).dilate(15).findBlobs(minsize=2000)
        greenBlobs = green.erode(10).dilate(15).binarize().findBlobs(minsize=2000)
        blueBlobs = blue.erode(10).dilate(15).binarize().findBlobs(minsize=2000)
        median.addDrawingLayer(red.dl())

        if redBlobs and greenBlobs and blueBlobs:
            redsquares = (redBlobs).filter([b.isSquare(2.51) for b in redBlobs])
            greensquares = (greenBlobs).filter([b.isSquare(2.51) for b in greenBlobs])
            bluesquares = (blueBlobs).filter([b.isSquare(2.51) for b in blueBlobs])





            if redsquares and greensquares and bluesquares:
                redsquares[-1].draw(layer=median.dl())
                greensquares[-1].draw(layer=median.dl())
                bluesquares[-1].draw(layer=median.dl())
                greensquares[-1].drawOutline()
                median.show()

                colors = [('red', redsquares), ('green', greensquares), ('blue', bluesquares)]
                ordersTuple = sorted(colors, key=lambda color: (color[1][-1].centroid())[0])

                orders = [x[0] for x in ordersTuple]
                print orders
            else:
                blue.show()




        #a = median + green
        #b = median + red
        #c = median + blue

        #median.applyLayers()
        #db.showImg(median)
        #time.sleep(0.1)
        #db.showImg(b)
        #time.sleep(0.1)
        #db.showImg(c)
        #time.sleep(0.1)
