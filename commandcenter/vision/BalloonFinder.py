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


BLOBTOFILE = False

#sorry for the global hack, we want to cache these blobs globally to prevent spamming the HDD every balloonScore() call
balloonFiles = [ f for f in listdir("balloons/") if isfile(join("balloons",f)) ]
baseBalloons = [SimpleCV.Image("balloons/" + x).findBlobs()[-1] for x in balloonFiles]

def blobsToImage(blobs):
    print blobs[-1].image.size()
    img = SimpleCV.Image(blobs[-1].image.size())
    img.addDrawingLayer()

    blobs[-1].draw(color=SimpleCV.Color.MAROON, layer=img.dl())
    img=img.applyLayers()
    return img

def walkToDir(networkManager, direction):
    if direction == "right":
        networkManager.rotateCam(10)
    if direction == "left":
        networkManager.rotateCam(-10)
    if direction == "forward":
        #rotate towards the balloon
        if abs(networkManager.getCamRotation()) > 1:
            networkManager.rotate(networkManager.getCamRotation())
            networkManager.rotateCam(-networkManager.getCamRotation())

        networkManager.forward()

    time.sleep(0.15)


#just an alias
def startState(networkManager, img, color):
    return toBalloonState(networkManager, img, color)


def toBalloonState(networkManager, img, color):
    print "toBalloonState"
    direction = getDirection(img, color)
    time.sleep(0.05)
    if direction == "idle":
        return lambda img, color: exploreSearchState(networkManager, 0, img, color)
    else:
        walkToDir(networkManager, direction)
        return lambda img, color: toBalloonState(networkManager, img, color)


def exploreSearchState(networkManager, tries, img, color):
    print "exploreSearchState" + `tries`
    direction = getDirection(img, color)

    if direction != "idle":
        return lambda img, color: toBalloonState(networkManager, img, color)
    else:
        time.sleep(0.15)
        if tries < 4:
            neg = tries % 2
            _deg = tries * 30 if tries != 0 else 15
            deg = -_deg if neg == 0 else _deg

            networkManager.rotateCam(deg)

            return (lambda img2, color2: exploreSearchState(networkManager, tries+1, img2, color2))
        else:
            return (lambda img, color: panicSearchState(networkManager, img, color))


def panicSearchState(networkManager, img, color):
    print "panicSearchState"
    #networkManager.rotate(180)
    return lambda img, color: exploreSearchState(networkManager, 0, img, color)

GREEN = (63, 57, 89)
t1 = 160; t2 = 90; t3 = 208
db.setTrackbars([t1, t2, t3])
#these functions apply (hueDistance . threshold . invert), extracting balloons with specific sweetspot values
def greenOnly(value):
    return (value.hueDistance(GREEN, minsaturation=db.t(0), minvalue=db.t(1))).threshold(db.t(2)).invert()

def redOnly(value):
    return (value + (value.hueDistance(SimpleCV.Color.RED, minsaturation=120, minvalue=130) *1.5).threshold(45)).invert()

def blueOnly(img):
    return ((img.hueDistance(SimpleCV.Color.BLUE, minsaturation=150, minvalue=5)).threshold(200)).invert()



#calls 1 of the 3 balloonOnly functions & tries to filter out balloons with hu moments (blob.match())
#color: string (either 'red', 'green' or 'blue')
def getDirection(img, color):
    #applies 2 different blurs to get rid of noise and "normalizes" the colors a bit
    img = img.copy()
    blurred = img.gaussianBlur((5, 5), 3, 3)
    blurred = blurred.medianFilter(11)

    width = img.width
    height = img.height

    if color == 'blue':
        filtered = blueOnly(blurred)
    elif color == 'red':
        filtered = redOnly(blurred)
    elif color == 'green':
        filtered = greenOnly(blurred)


    db.showImg(filtered)

    #finds all the blobs and check whether they are balloon-y by checking the hu-moments with known (cached) blobs
    balloons = filterBalloons(filtered)
    if balloons:
        #[b.drawOutline(layer=filtered.dl(), color=SimpleCV.Color.CRIMSON, width=3) for b in balloons]
        filtered = blobsToImage(balloons)
    else:
        return "idle"

    #sum of balloon-pixels in each section
    sumLeft = np.sum(filtered.regionSelect(0, 0, width/2, height).getGrayNumpy())
    sumRight = np.sum(filtered.regionSelect(width/2, 0, width, height).getGrayNumpy())
    sumMid = np.sum(filtered.regionSelect(width/5*2, 0, width/5*3, height).getGrayNumpy())
    sumBoth = sumLeft + sumRight
    #the x factor is to make the sums independent of resolution
    x = width*height

    #the image is split up in 3 sections, each having a threshold sum
    #"forward" is a bit of an exception, since a balloon right in front of us can "fill" multiple sections
    if sumBoth > 40*x or sumMid > 0.7*x:
        ret = "forward"#: " + `sumBoth/x`
    elif sumLeft > 0.7*x:
       ret = "left"#: " + `sumLeft/x`
    elif sumRight > 0.7*x:
       ret = "right"#: " + `sumRight/x`
    else:
       ret =  "idle"

    db.showImg(filtered)

    return ret


#returns the highest hu-moments score (blob.match()), compares the blob with blobs extracted from balloon?.png
def balloonScore(blob):
    if blob:
        sortedBlobScore = sorted( (blob.match(baseBalloons[i]) for i in range(len(baseBalloons))))
        print sortedBlobScore[0]
        if BLOBTOFILE == True:
            blob.hullImage().save("b.png")

        return sortedBlobScore[0]


#iterate over all balloons and filter the balloon-y blobs
def filterBalloons(img):
    blobs = img.erode(3).dilate(3).findBlobs(minsize=100)
    if blobs:
        asd = blobs[-1].area()

        blobs = blobs.filter([balloonScore(b) <  (0.05 if b.area() < 3000 else 0.3) for b in blobs])

    return blobs