import SimpleCV
import sys

disp = 0
js = 0

if sys.platform != "win32":
    js = SimpleCV.JpegStreamer('0.0.0.0:8080')
else:
    disp = SimpleCV.Display()

def showImg(img):
    if js != 0:
        img.save(js.framebuffer)
    if disp != 0:
        img.show()