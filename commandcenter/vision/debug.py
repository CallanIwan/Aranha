import SimpleCV
import cv2
import sys

disp = 0
js = 0
cv2.namedWindow("main")
cv2.namedWindow("tracks")

if sys.platform != "win32":
    js = SimpleCV.JpegStreamer('0.0.0.0:8080')
else:
    disp = SimpleCV.Display()

def nothing(x):
    pass

def t(x):
    ret = cv2.getTrackbarPos("t"+`x+1`, "tracks")
    return ret

#CALL ONLY ONCE
def setTrackbars(vals, max=255):
    if vals != None:
        for i in range(len(vals)):
            cv2.createTrackbar("t"+`i+1`, "tracks", vals[i], max, nothing)


#streams the image over the jpegStream if we are on an RPI
#if on windows (main dev-env) display the image in a window
def showImg(img):
    if js != 0:
        img.save(js.framebuffer)
    if disp != 0:
        cv2.imshow("main", img.getNumpyCv2())
        cv2.waitKey(300)
