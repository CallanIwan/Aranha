"""
vision/balloon.py
Find a balloon <insert more info>
"""

import SimpleCV
import numpy as np
import time

from vision import debug as db

class BalloonFinder(object):

    def findBalloon(self, img, color):
        median = img.gaussianBlur((3,3), 3, 5)
        median = median.medianFilter(5)
        dir = getDirection(median, color)
        return dir



def greenOnly(value):
    #return value.hueDistance(SimpleCV.Color.GREEN, minsaturation=77, minvalue=93).threshold(80).invert()
    return (value + (value.hueDistance(SimpleCV.Color.GREEN, minsaturation=70)*1.5).threshold(70)).invert()

def redOnly(value):
    #return value.hueDistance(SimpleCV.Color.RED, minsaturation=137, minvalue=100).threshold(25).invert()
    return (value + (value.hueDistance(SimpleCV.Color.RED, minsaturation=120, minvalue=130)   *1.5).threshold(45)).invert()

def blueOnly(img):
    #return img.hueDistance(SimpleCV.Color.BLUE, minsaturation=70, minvalue=150).threshold(90).invert()
    return (img + (img.hueDistance(SimpleCV.Color.BLUE, minsaturation=50)  *1.7).threshold(70)).invert()

def getDirection(img, color):
    width = img.width
    height = img.height

    if color == 'blue':
        filtered = blueOnly(img)
    elif color == 'red':
        filtered = redOnly(img)
    elif color == 'green':
        filtered = greenOnly(img)

    filtered.addDrawingLayer()
    balloons = filterBalloons(filtered)
    if balloons:
        [b.drawOutline(layer=filtered.dl(), color=SimpleCV.Color.CRIMSON, width=3) for b in balloons]

    sumLeft = np.sum(filtered.regionSelect(0, 0, width/2, height).getGrayNumpy())
    sumRight = np.sum(filtered.regionSelect(width/2, 0, width, height).getGrayNumpy())
    sumMid = np.sum(filtered.regionSelect(width/5*2, 0, width/5*3, height).getGrayNumpy())
    sumBoth = sumLeft + sumRight
    x = width*height

    #print("sumleft: " + `sumLeft` + ". sumMid: " + `sumMid` + ". sumRight: " + `sumRight` + ". sumBoth: " + `sumBoth`)
    if sumBoth > 40*x or sumMid > 1*x:
        ret = 'forward: ' + `sumBoth/x`
    elif sumLeft > 1*x:
        ret = 'left: ' + `sumLeft/x`
    elif sumRight > 1*x:
        ret = 'right: ' + `sumRight/x`
    else:
        ret =  'idle'

    filtered.drawText(ret, x=-5, color=SimpleCV.Color.CRIMSON, fontsize=84)
    db.showImg(filtered)
    return ret

baseBalloons = [SimpleCV.Image("balloon" + `x` + ".png").findBlobs()[-1] for x in (range(8))]


def balloonScore(blob):
    if blob:
        sortedBlobScore = sorted( (blob.match(baseBalloons[i]) for i in range(8)))
        print sortedBlobScore[0]
        return  sortedBlobScore[0]


def filterBalloons(img):
        blobs = img.erode(2).dilate(5).findBlobs(minsize=1000)
        if blobs:
            blobs = blobs.filter([balloonScore(b) <  0.05 for b in blobs])

        return blobs
