import SimpleCV
import cv2
import numpy as np

width = 640
height = 480
cv2.namedWindow("test")
whichColor = 'red'


class Vision:
    def run(self):
        Camera2().display()
        print ('a')

    def findBalloon(self, color):
        print ('vision works')

    def findBalloonOrder(self):
        print ('a')

class Camera2:
    def display(self):
        cv2.createTrackbar("track1", "test", 1, 255, id)
        cv2.createTrackbar("track2", "test", 1, 255, id)
        cv2.createTrackbar("track3", "test", 1, 255, id)
        camera = SimpleCV.Camera(0)

        while True:
            frame = camera.getImage()
            median = frame.medianFilter(3)
            #redOnly(median).threshold(40).show()
            dir = getDirection(median, whichColor)
            print dir
            median.drawText(dir,x = -10, color=SimpleCV.Color.CRIMSON, fontsize=84)
            median.show()
            #cv2.imshow("test1", greenOnly(median).getNumpyCv2())
            #cv2.imshow("test2", redOnly(median).getNumpyCv2())

            #a = frame.drawText("works 1234, 1234")

            key = cv2.waitKey(20)
            if key == 27: # exit on ESC
                break

	del camera

def greenOnly(value):
    return value.hueDistance(SimpleCV.Color.GREEN, minsaturation=77, minvalue=93).threshold(80).invert()

def redOnly(value):
    return value.hueDistance(SimpleCV.Color.RED, 137, 100).threshold(20).invert()

def blueOnly(img):
    return img.hueDistance(SimpleCV.Color.BLUE, minsaturation=70, minvalue=150).threshold(90).invert()

def getDirection(img, color):
    if color == 'blue':
        filtered = blueOnly(img)
    elif color == 'red':
        filtered = redOnly(img)
    elif color == 'green':
        filtered = greenOnly(img)

    sumLeft = np.sum(filtered.regionSelect(0, 0, width/2, height).getGrayNumpy())
    sumRight = np.sum(filtered.regionSelect(width/2, 0, width, height).getGrayNumpy())
    sumMid = np.sum(filtered.regionSelect(width/5*2, 0, width/5*3, height).getGrayNumpy())

    sumBoth = sumLeft + sumRight
    #print("sumleft: " + `sumLeft` + ". sumMid: " + `sumMid` + ". sumRight: " + `sumRight` + ". sumBoth: " + `sumBoth`)
    if sumBoth > 20000000 or sumMid > 300000:
        return 'forward: ' + `sumBoth`
    elif sumLeft > 300000:
        return 'left: ' + `sumLeft`
    elif sumRight > 300000:
        return 'right: ' + `sumRight`
    else:
        return 'nothing'