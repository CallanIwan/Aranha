import numpy as np
import cv2
import cv2.cv as cv

#Load the image (img) and convert grayscale to bgr(cimg)
img = cv2.imread("b.png", 0)
cimg = cv2.cvtColor(img,cv2.COLOR_GRAY2BGR)

#Handy dandy display function
def d (img) :
    cv2.imshow('image', img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

def canny (img) :
    cv2.blur(img, )

#Find all circular things in the image. params are tricky
#b.png
circles = cv2.HoughCircles(img, cv.CV_HOUGH_GRADIENT,1,20, param1=50,param2=30,minRadius=10,maxRadius=500)
#c.jpg
#circles = cv2.HoughCircles(img, cv.CV_HOUGH_GRADIENT,1,20, param1=50,param2=30,minRadius=30,maxRadius=50)

#?
circles = np.uint16(np.around(circles))

#draw the circles it found and the their inner centers
for i in circles[0,:]:
    # draw the outer circle
    cv2.circle(cimg,(i[0],i[1]),i[2],(0,255,0),2)
    # draw the center of the circle
    cv2.circle(cimg,(i[0],i[1]),2,(0,0,255),3)

#display the (drawn over^) bgr image
d (img)
d (cimg)
