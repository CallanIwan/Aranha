from math import *


class Ray(object):

    def __init__(self, pos, rot, dist):
        self.pos = pos
        self.rot = rot
        self.dist = dist


    def getEndTile(self, tileSize, plusOne=True):
        rad = radians(self.rot)
        dist = self.dist + tileSize if plusOne else self.dist
        (x, y) = self.pos

        x += sin(rad) * dist
        y += cos(rad) * dist

        #we have the location the ray ends, somewhere in the tile. now we have to change
        #these coordinates to actual index values.
        x = floor(x)
        y = floor(y)

        return (x,y)


    def getPassableTiles(self, tileSize):
        "Bresenham's line algorithm"

        (x0, y0) = self.pos
        (x1, y1) = self.getEndTile(tileSize, plusOne=False)
        x1+=1;  y1+=1
        tileList = []

        dx = abs(x1 - x0)
        dy = abs(y1 - y0)
        x, y = x0, y0
        sx = -1 if x0 > x1 else 1
        sy = -1 if y0 > y1 else 1
        if dx > dy:
            err = dx / 2.0
            while x != x1:
                tileList.append((x, y))
                err -= dy
                if err < 0:
                    y += sy
                    err += dx
                x += sx
        else:
            err = dy / 2.0
            while y != y1:
                tileList.append((x, y))
                err -= dx
                if err < 0:
                    x += sx
                    err += dy
                y += sy
        tileList.append((x, y))
        return tileList


ray = Ray((5, 5), 15, 5)
ray.getEndTile(1)
ray.getPassableTiles(1)