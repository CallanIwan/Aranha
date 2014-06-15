import numpy as np

class Grid(object):

    UNKNOWN = 0
    PASSABLE = 1
    IMPASSABLE = 2
    VISITED = 4

    grid = None

    def __init__(self, size):
        self.grid = np.ndarray(shape=(size, size), dtype=int)

    def getTile(self, point):
        return self.grid[point[0]][point[1]]

    def setTile(self, point, bitFlag):
        self.grid[point[0]][point[1]] = bitFlag

    def setTiles(self, points, bitFlag):
        for p in points:
            self.grid[p[0]][p[1]] = bitFlag



#expands the search area around the location in an "efficient" manner (its still python, sigh)
                #######
        #####   #     #
  ###   #   #   #     #
  #.#   # . #   #  .  #
  ###   #   #   #     #
        #####   #     #
                #######
#when a tile has been found it will get the #n connected tiles
    def nearestUnexplored(self, pos, connected=1):
        for tryCount in xrange(1, 15):
            startx = pos[0] - tryCount
            starty = pos[1] - tryCount
            endx = pos[0] + tryCount
            endy = pos[1] + tryCount
            found = None

            for x in xrange(starty, endy):
                if self.grid[x][starty] & self.VISITED == False:
                    found = (x, starty)
                if self.grid[x][endy] & self.VISITED == False:
                    found = (x, endy)

            for y in xrange(startx, endx):
                if self.grid[startx][y] & self.VISITED == False:
                    found = (startx, y)
                if self.grid[endx][y] & self.VISITED == False:
                    found = (endx, y)

            validFound = self.isConnectedCountNot(found, self.VISITED) >= connected

            if validFound == True:
                return found

        return None

    #todo: fully test if this grabs all 8 bounding tiles
    def isConnectedCountNot(self, pos, flag):
        return  9 - self.isConnectedCount(pos, flag)

    def _and(self, x, flag):
        val = x & flag
        return 1 if val != 0 else 0

    def isConnectedCount(self, pos, flag):
        (x, y) = pos
        count = \
           self._and(self.grid[x][y]    , flag) + \
           self._and(self.grid[x+1][y+1], flag) + \
           self._and(self.grid[x-1][y-1], flag) + \
           self._and(self.grid[x-1][y+1], flag) + \
           self._and(self.grid[x+1][y-1], flag) + \
           self._and(self.grid[x][y+1]  , flag) + \
           self._and(self.grid[x][y-1]  , flag) + \
           self._and(self.grid[x-1][y]  , flag) + \
           self._and(self.grid[x+1][y]  , flag)

        return count
