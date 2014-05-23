import SimpleCV
from SimpleCV.base import *
import numpy as np
import time

from vision import debug as db

class CardFinder(object):

    def findColorOrder(self, img):
            median = img.gaussianBlur((5,5), 5, 7)
            median = median.medianFilter(5)

            #todo adaptive thresholding (binarize). perhaps to create a mask for more precise thresholding later on
            red = (median.hueDistance(SimpleCV.Color.RED, minsaturation=150, minvalue=50)   *2).threshold(35).invert()
            green = (median.hueDistance(SimpleCV.Color.GREEN, minsaturation=90, minvalue=40)*1).threshold(70).invert()
            blue = (median.hueDistance(SimpleCV.Color.BLUE, minsaturation=70, minvalue= 30)  *1.5).threshold(70).invert()

            redBlobs = red.erode(10).dilate(15).findBlobs(minsize=1000)
            greenBlobs = green.erode(1).dilate(10).findBlobs(minsize=1000)
            blueBlobs = blue.erode(10).dilate(15).findBlobs(minsize=1000)
            median.addDrawingLayer(red.dl())

            if redBlobs and greenBlobs and blueBlobs:
                redsquares = (redBlobs).filter([b.isSquare(5.51) for b in redBlobs])
                greensquares = (greenBlobs).filter([b.isSquare(1.51) for b in greenBlobs])
                bluesquares = (blueBlobs).filter([b.isSquare(1.51) for b in blueBlobs])

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
                    green.show()
