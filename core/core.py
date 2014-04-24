from vision import *

class Core:

    def __init__(self):
        Vision().findBalloon(0)

    def run(self):
        Vision().run()

    #1) probeer kleurvolgorde te vinden van kaartje. 2) rape ballonnen
    def setVisionState(self, state):
        print 'a'

    #pak state van spin van /dev/aranha, formaat json
    def fetchState(self):
        print 'a'

core = Core()
core.run()