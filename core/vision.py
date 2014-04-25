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
            cv2.imshow("gauss", gauss(frame))
            cv2.imshow("redOnly", redOnly(frame))

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

def redOnly(img):
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV, 3)
    draw_histogram(hsv)
    #hue = cv2.extractChannel(hsv, 0)
    #retval, filtered = cv2.threshold(hue, 40, 255, cv2.THRESH_TOZERO)

    filtered = cv2.inRange(hsv, (120, 80, 80), (255, 255, 255))
    #return filtered

    return toColor(filtered)



def draw_histogram(img):
    plt.hist(img.ravel(),256,[0,256])
    plt.show()

def draw_histogram2(img):
    color = ('b','g','r')
    for i,col in enumerate(color):
        histr = cv2.calcHist([img],[i],None,[256],[0,256])
        plt.plot(histr,color = col)
        plt.xlim([0,256])
    plt.show()
