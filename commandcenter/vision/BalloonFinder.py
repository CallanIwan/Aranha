"""
vision/balloon.py
Find a balloon <insert more info>
"""

import SimpleCV
import numpy as np
import time
from os import listdir
from os.path import isfile, join

from vision import debug as db


#sorry for the global hack, we want to cache these blobs globally to prevent spamming the HDD every balloonScore() call
balloonFiles = [ f for f in listdir("balloons/") if isfile(join("balloons",f)) ]
baseBalloons = [SimpleCV.Image("balloons/" + x).findBlobs()[-1] for x in balloonFiles]

def blobsToImage(blobs):
    print blobs[-1].image.size()
    img = SimpleCV.Image(blobs[-1].image.size())
    img.addDrawingLayer()

    blobs[-1].draw(color=SimpleCV.Color.MAROON, width=3, layer=img.dl())

    return img


class BalloonFinder(object):
    #the "main" function, applies 2 different blurs to get rid of noise and "normalizes" the colors a bit
    def findBalloon(self, img, color):
        median = img.gaussianBlur((3,3), 3, 5)
        median = median.medianFilter(5)
        dir = self.getDirection(median, color)
        return dir


    #these functions apply (hueDistance . threshold . invert), extracting balloons with specific sweetspot values
    def greenOnly(self, value):
        return (value + (value.hueDistance(SimpleCV.Color.GREEN, minsaturation=70)*1.5).threshold(70)).invert()

    def redOnly(self, value):
        return (value + (value.hueDistance(SimpleCV.Color.RED, minsaturation=120, minvalue=130) *1.5).threshold(45)).invert()

    def blueOnly(self, img):
        return (img + (img.hueDistance(SimpleCV.Color.BLUE, minsaturation=40)  *1.7).threshold(50)).invert()



    #calls 1 of the 3 balloonOnly functions & tries to filter out balloons with hu moments (blob.match())
    #color: string (either 'red', 'green' or 'blue')
    def getDirection(self, img, color):
        width = img.width
        height = img.height

        if color == 'blue':
            filtered = self.blueOnly(img)
        elif color == 'red':
            filtered = self.redOnly(img)
        elif color == 'green':
            filtered = self.greenOnly(img)

        #finds all the blobs and check whether they are balloon-y by checking the hu-moments with known (cached) blobs
        filtered.addDrawingLayer()
        balloons = self.filterBalloons(filtered)
        if balloons:
            #[b.drawOutline(layer=filtered.dl(), color=SimpleCV.Color.CRIMSON, width=3) for b in balloons]
            filtered = blobsToImage(balloons)

        #sum of balloon-pixels in each section
        sumLeft = np.sum(filtered.regionSelect(0, 0, width/2, height).getGrayNumpy())
        sumRight = np.sum(filtered.regionSelect(width/2, 0, width, height).getGrayNumpy())
        sumMid = np.sum(filtered.regionSelect(width/5*2, 0, width/5*3, height).getGrayNumpy())
        sumBoth = sumLeft + sumRight
        #the x factor is to make the sums independent of resolution
        x = width*height

        #the image is split up in 3 sections, each having a threshold sum
        #"forward" is a bit of an exception, since a balloon right in front of us can "fill" multiple sections
        if sumBoth > 40*x or sumMid > 1*x:
            ret = 'forward: ' + `sumBoth/x`
        elif sumLeft > 1*x:
           ret = 'left: ' + `sumLeft/x`
        elif sumRight > 1*x:
           ret = 'right: ' + `sumRight/x`
        else:
           ret =  'idle'

        db.showImg(filtered)

        return ret

    #returns the highest hu-moments score (blob.match()), compares the blob with blobs extracted from balloon?.png
    def balloonScore(self, blob):
        if blob:
            sortedBlobScore = sorted( (blob.match(baseBalloons[i]) for i in range(8)))
            print sortedBlobScore[0]
            return sortedBlobScore[0]


    #iterate over all balloons and filter the balloon-y blobs
    def filterBalloons(self, img):
            blobs = img.erode(2).dilate(5).findBlobs(minsize=1000)
            if blobs:
                blobs = blobs.filter([self.balloonScore(b) <  0.05 for b in blobs])

            return blobs