import SimpleCV
from SimpleCV.base import *
import numpy as np
import time

from vision import debug as db

#debug trackbar magic
#thresholds
#t1 = 5; t2 = 5; t3 = 200
#t4 = 100; t5 = 40; t6 = 100
#t7 = 40; t8 = 100; t9 = 100
#db.setTrackbars([t1, t2, t3])#, t4, t5, t6, t7, t8, t9])
#squar-y ratio
s1 = 150
s2 = 70

COMPENSATEYELLOW = False


#extracts red, green and blue blobs and tries to filter the possibilities by checking the "square-score
#the order of colors is based on the y-axis of the blobs. simply a sort() suffices
#uses for thresholding
class CardFinder(object):
    def findColorOrder(self, img):
        if COMPENSATEYELLOW == True:
            t1 = 100; t2 = 130; t3 = 35
            t4 = 100; t5 = 40; t6 = 100
            t7 = 5; t8 = 5; t9 = 180
            t4 = db.t(0)
            t5 = db.t(1)
            t6 = db.t(2)
        else:
            t1 = 100; t2 = 130; t3 = 35
            t4 = 30; t5 = 30; t6 = 60
            t7 = 150; t8 = 5; t9 = 200

        #basic blurs to remove noise and even out the colors
        img = img.copy()
        median = img.gaussianBlur(3, 3)
        median = median.medianFilter(7)

        #RED = (213, 78, 29) if COMPENSATEYELLOW else (132, 168, 44)
        GREEN = (213, 78, 29) if COMPENSATEYELLOW else (63, 57, 89)
        #BLUE = (213, 78, 29) if COMPENSATEYELLOW else (132, 168, 44)

        #apply (hueDistance . treshold . invert), multiplies by values which somehow improve detection..
        #todo adaptive thresholding (binarize). perhaps to create a mask for more precise thresholding later on?
        red = (median.hueDistance(SimpleCV.Color.RED, minsaturation=t1, minvalue=t2)   *2).threshold(t3).invert()
        green = (median.hueDistance(SimpleCV.Color.GREEN, minsaturation=t4, minvalue=t5)*1).threshold(t6).invert()
        blue = (median.hueDistance(SimpleCV.Color.BLUE, minsaturation=t7, minvalue=t8) ).threshold(t9).invert()

        #blue = blue.invert() if COMPENSATEYELLOW == False else blue

        #(erode . dilate) to remove "messy" blobs and make the card-blob more square-like
        redBlobs = red.erode(10).dilate(15).findBlobs(minsize=1000)
        greenBlobs = green.erode(10).dilate(10).findBlobs(minsize=1000)
        blueBlobs = blue.erode(10).dilate(10).findBlobs(minsize=1000)
        median.addDrawingLayer(red.dl())

        if redBlobs and greenBlobs and blueBlobs:
            #filter all blobs with a square-y ratio
            #s1 = float(db.t(0))/100
            #s2 = float(db.t(1))/100
            s1 = 1.50
            s2 = 0.20
            redsquares = (redBlobs).filter([b.isSquare(s1, s2) for b in redBlobs])
            greensquares = (greenBlobs).filter([b.isSquare(s1, s2) for b in greenBlobs])
            bluesquares = (blueBlobs).filter([b.isSquare(s1, s2) for b in blueBlobs])

            if redsquares and greensquares and bluesquares:
                #draw the blobs to our initial blurred image, for debug and spider-o-vision!
                redsquares[-1].draw(layer=median.dl())
                greensquares[-1].draw(layer=median.dl())
                bluesquares[-1].draw(layer=median.dl())
                greensquares[-1].drawOutline()
                db.showImg(median)

                #find the 3 blobs, order by height, extract the ordered color strings
                colors = [('red', redsquares), ('green', greensquares), ('blue', bluesquares)]
                ordersTuple = sorted(colors, key=lambda color: (color[1][-1].y))
                orders = [x[0] for x in ordersTuple]

                return orders

        #display failing blob-image here.
        db.showImg(red)
        db.showImg(green)
        db.showImg(blue)

        #didn't find all 3 RGB squares
        return None
