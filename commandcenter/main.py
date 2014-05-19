from vision.balloon import find
from vision.camera import Camera
import vision.card as card


cam = Camera()
#find(cam.camera, 'blue')
card.findCard(cam)

#main loop
