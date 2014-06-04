from Canvas import Line
from navigator.Grid import Grid
from navigator.Ray import Ray


class World(object):
    grid = None
    rays = []
    tileSize = 1

    def __init__(self, tileSize = 1):
        self.tileSize = tileSize
        self.grid = Grid(100)

    def applyRay(self, ray):
        passableTiles = ray.getPassableTiles(self.tileSize)
        endTile = ray.getEndTile(self.tileSize)

        self.rays.append(ray)
        self.grid.setTiles(passableTiles, self.grid.PASSABLE)
        self.grid.setTile(endTile, self.grid.IMPASSABLE)


