import numpy as np
import cv2
import cv2.cv as cv

#Load the image (img) and convert grayscale to bgr(cimg)
img = cv2.imread("c.jpg", 1)
camera = cv2.VideoCapture(0)

#Handy dandy display function
def d (img) :
    cv2.imshow('image', img)
    cv2.waitKey()
    cv2.destroyAllWindows()



def canny (img) :
    t = cv2.GaussianBlur(img, (11, 9), 4)
    return cv2.Canny(t, 20, 60)

#draw the circles it found and the their inner centers
def hough_circles (img) :
    #Find all circular things in the image. params are tricky
    #b.png
    #circles = cv2.HoughCircles(img, cv.CV_HOUGH_GRADIENT,1,20, param1=50,param2=30,minRadius=10,maxRadius=500)
    #c.jpg
    circles = cv2.HoughCircles(img, cv.CV_HOUGH_GRADIENT,0,40, param1=50,param2=30,minRadius=50,maxRadius=200)
    #?
    circles = np.uint16(np.around(circles))
    return circles

def d_circles (img, circles) :
    copy = img.copy()
    #copy = cv2.cvtColor(img,cv2.COLOR_GRAY2BGR)
    for i in circles[0,:]:
        # draw the outer circle
        cv2.circle(copy,(i[0],i[1]),i[2],(0,255,0),2)
        # draw the center of the circle
        cv2.circle(copy,(i[0],i[1]),2,(0,0,255),3)
    return copy

def c (img) :
    return d_circles (img, hough_circles(img))

#d (img)
#d (d_circles (img, hough_circles(img)))
#d (d_circles (img, hough_circles (canny(img))))
#d(canny(img))


def get_frame () :
    if camera.isOpened() == True:
        retval, img = camera.read()
        return img
    return False


def main () :
    camera.open(0)
    if camera.isOpened(): # try to get the first frame
        rval, frame = camera.read()

    while rval:
        cv2.imshow("preview", c (canny(frame)))
        rval, frame = camera.read()
        key = cv2.waitKey(20)
        if key == 27: # exit on ESC
            break
    cv2.destroyWindow("preview")
    camera.release

main()


d (c (img))







