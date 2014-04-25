import cv2
import cv2.cv as cv
import numpy as np
from matplotlib import pyplot as plt


class Vision:
    def run(self):
        Camera().display()
        print ('a')

    def findBalloon(self, color):
        print ('vision works')

    def findBalloonOrder(self):
        print ('a')


class Camera:
    camera = cv2.VideoCapture(0)

    #def __init__(self):
    #    camera = cv2.VideoCapture(0)

    def display(self):
        self.camera.open(0)
        if self.camera.isOpened(): # try to get the first frame
            rval, frame = self.camera.read()

        while rval:
            #cv2.imshow("preview", canny(frame))
            c = canny(frame)
            #cv2.imshow("preview", d_circles(toColor(c), hough_circles(c)))
            cv2.imshow("vanilla", frame)
            #cv2.imshow("redOnly", redOnly(frame))
            cv2.imshow("greenOnly", greenOnly(frame))
            rval, frame = self.camera.read()
            key = cv2.waitKey(20)
            if key == 27: # exit on ESC
                break
        cv2.destroyWindow("preview")
        self.camera.release()


def canny (img) :
    t = cv2.GaussianBlur(img, (11, 7), 2)
    return cv2.Canny(t, 40, 100)

def hough_circles (img) :
    circles = cv2.HoughCircles(img, cv.CV_HOUGH_GRADIENT,1,40, param1=30,param2=12,minRadius=50,maxRadius=70)
    circles = np.uint16(np.around(circles))
    return circles

def d_circles (img, circles) :
    copy = img.copy()
    #copy = cv2.cvtColor(cv2.cvtColor(copy, cv2.COLOR_RGB2GRAY), cv2.COLOR_GRAY2RGB, 3)
    for i in circles[0,:]:
        cv2.circle(copy,(i[0],i[1]),i[2],(0,255,0),2)
        cv2.circle(copy,(i[0],i[1]),2,(0,0,255),3)
    return copy

def toColor(img):
    return cv2.cvtColor(img, cv2.COLOR_GRAY2RGB, 3)

def toGray(img):
    return cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)

def gauss(img):
    return cv2.GaussianBlur(img, (9, 9), 4);

def greenOnly(img):
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV, 3)
    filtered = cv2.inRange(hsv, (0, 100, 100), (18, 255, 255))
    return toColor(filtered)

def redOnly(img):
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV, 3)
    filtered = cv2.inRange(hsv, (0, 100, 100), (18, 255, 255))
    filtered2 = cv2.inRange(hsv, (170, 80, 80), (255, 255, 255))
    t = cv2.add(filtered, filtered2)

    draw_histogram(hsv)
    return toColor(t)




def draw_histogram(img):
    hsv_map = np.zeros((180, 256, 3), np.uint8)
    h, s = np.indices(hsv_map.shape[:2])
    hsv_map[:,:,0] = h
    hsv_map[:,:,1] = s
    hsv_map[:,:,2] = 255
    hsv_map = cv2.cvtColor(hsv_map, cv2.COLOR_HSV2BGR)

    hsv = img#cv2.cvtColor(img, cv2.CV_BGR2HSV );
    h = cv2.calcHist( [hsv], [0, 1], None, [180, 256], [0, 180, 0, 256] )
    hist_scale = 10

    h = np.clip(h*0.005*hist_scale, 0, 1)
    vis = hsv_map*h[:,:,np.newaxis] / 255.0
    cv2.imshow('hist', vis)
