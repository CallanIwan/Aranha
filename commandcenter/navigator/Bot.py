#helper functions for movement go here since the basic commands are pretty elementary
import math
import time


#since we're in a pretty messy (network) IO situation... NONBLOCKING, KEEP CALLING THIS
#returns True if arrived
#returns False when not finished yet, keep calling me
import numpy
from util import pair, angle, angleToPoint


def walkTo(nw, current, location):
    current = pair(current)
    location = pair(location)
    vec = (location[0] - current[0], location[1] - current[1])
    distance = math.sqrt(vec[0]^2 + vec[1]^2)
    #normalizedVec = (vec[0]/distance, vec[1]/distance)


    rotation = angleToPoint(vec)

    #these "care" values are just safety checks, don't want the bot to go crazy and spam tiny rotations like 0.00012
    if rotation < 1.0 and not distance < 0.5:
        nw.sendCommand(nw.H_MOV + ";" + `rotation`)
        return False

    #far enough to care
    if distance > 0.5:
        nw.sendCommand(nw.H_MOV + ";" + 0)

