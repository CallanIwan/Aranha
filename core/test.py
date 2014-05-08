from SimpleCV import *
from PIL import *
import sys

cam = Camera()
if not cam
    print 'Camera() Failed!'
    sys.exit(-1)

img = cam.getImage()
if not img
    print 'getImage() Failed!'    
    sys.exit(-1)

# Everything succeeded, display image!
img.show()
