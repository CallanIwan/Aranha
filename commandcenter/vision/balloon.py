"""
vision/balloon.py
Find a balloon <insert more info>
"""

import SimpleCV
import numpy as np
import time

from vision import debug as db


def find(cam, color):

    #cv2.createTrackbar("track1", "test", 1, 255, id)
    #cv2.createTrackbar("track2", "test", 1, 255, id)
    #cv2.createTrackbar("track3", "test", 1, 255, id)

    while True:#disp.isNotDone():
        frame = cam.getImage()

        median = frame.medianFilter(17)
        dir = getDirection(median, color)

        time.sleep(0.05)

        #median.drawText(dir, x=-10, color=SimpleCV.Color.CRIMSON, fontsize=84)

        #db.showImg(median)  # show() does not work correct

def greenOnly(value):
    #return value.hueDistance(SimpleCV.Color.GREEN, minsaturation=77, minvalue=93).threshold(80).invert()
    return (value + (value.hueDistance(SimpleCV.Color.GREEN, minsaturation=70)*1.5).threshold(70)).invert()

def redOnly(value):
    #return value.hueDistance(SimpleCV.Color.RED, minsaturation=137, minvalue=100).threshold(25).invert()
    return (value + (value.hueDistance(SimpleCV.Color.RED, minsaturation=150)   *1.5).threshold(35)).invert()

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

    db.showImg(filtered)

    sumLeft = np.sum(filtered.regionSelect(0, 0, width/2, height).getGrayNumpy())
    sumRight = np.sum(filtered.regionSelect(width/2, 0, width, height).getGrayNumpy())
    sumMid = np.sum(filtered.regionSelect(width/5*2, 0, width/5*3, height).getGrayNumpy())
    sumBoth = sumLeft + sumRight
    x = width*height

    #print("sumleft: " + `sumLeft` + ". sumMid: " + `sumMid` + ". sumRight: " + `sumRight` + ". sumBoth: " + `sumBoth`)
    if sumBoth > 40*x or sumMid > 1*x:
        return 'forward: ' + `sumBoth/x`
    elif sumLeft > 1*x:
        return 'left: ' + `sumLeft/x`
    elif sumRight > 1*x:
        return 'right: ' + `sumRight/x`
    else:
        return 'nothing'