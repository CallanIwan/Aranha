import math
import threading
import time
from navigator import Bot
from navigator.Ray import Ray
from navigator.SpiderState import SpiderState
from navigator.World import World


class OilStateMachine(object):
    def run(self, startState, networkManager):
        #boot up the state-thread
        state = SpiderState()
        world = World()

        thread = threading.Thread(target=(lambda: state.run(networkManager, world)))
        thread.start()

        #"state" will automagically update in the background since a thread is constantly updating it
        fn = startState(world, state, networkManager)

        #trampoline!
        while callable(fn):
            fn = fn()


#statemachine state example
def startState(world, state, networkManager):
    print "Booting up statemachine!"
    while state.getState() == None:
        time.sleep(0.3)
        #print "stuck in stateloop"

    print "exiting startstate"
    return lambda: analyzeState(world, state, networkManager)


#turn the bot around a bit, analyze world
def analyzeState(world, state, networkManager):
    print "Inside analyzeMode"
    #rotate a large fraction of world space, change rotation to move either left OR right
    networkManager.rotate(networkManager, 120)

    return lambda: exploreState(world, state, networkManager)


#exploration will be thorough, this means it will prioritize fully mapping nearby spots
#over mapping larger areas further away.
#todo: aggressive exploration vs precise?
def exploreState(world, state, networkManager):
    walkingDone = False
    print "inside exploreState"

    state = state.getState()
    spot = world.nearestUnexplored(state.pos, 2)
    while walkingDone == False:
        walkTemp = Bot.walkTo(networkManager, state.pos, spot)

    #return lambda: exploreState(world, state, networkManager)
    return lambda: endState(world, state, networkManager)


def endState(world, state, networkManager):
    print "Shutting down the statemachine!"
    return world





