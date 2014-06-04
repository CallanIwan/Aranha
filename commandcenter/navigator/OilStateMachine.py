import math
import threading
from navigator.Ray import Ray
from navigator.SpiderState import SpiderState
from navigator.World import World


class OilStateMachine(object):
    def run(self, startState, networkManager):
        #boot up the state-thread
        state = SpiderState()
        thread = threading.Thread(target=(lambda: state.run(networkManager)))
        thread.start()

        #"state" will automagically update in the background since a thread is constantly updating it
        fn = startState(World(), state, networkManager)

        #trampoline!
        while fn != None:
            fn = fn()

#statemachine state example
def startState(world, state, networkManager):
    print "Booting up statemachine!"
    return lambda: analyzeState(world, state, networkManager)


#todo: implement this
#draft implementation
#def walkTo(s, current, location):
    #vector = location - current
    #distance = math.sqrt(vector[0]^2 + vector[1]^2)
    #rotation = vector(location).diff_angle(location)

    #Client.sendCommand(s, chr(4) + "{\"move\": [" + rotation + "," + distance + "]}" )


#--------------------

def startState(world, state, networkManager):
    print "Booting up statemachine!"
    return lambda: analyzeState(world, state, networkManager)


#turn the bot around a bit, analyze world
def analyzeState(world, state, networkManager):
    print "Inside analyzeMode"

    #rotate 120 degrees* and keep fetching sensor data
    #chr(3) is a magic number and specifies that the next bytes are movement related.
    networkManager.sendCommand(chr(3) + "{'rotate': -120}")
    for i in range(10):
        newState = state.getState()
        world.applyRay(Ray(newState.pos, newState.rot, newState.dist))

    return lambda: exploreState(world, state, networkManager)


#exploration will be thorough, this means it will prioritize fully mapping nearby spots
#over mapping larger areas further away.
#todo: aggressive exploration vs precise?
def exploreState(world, state, networkManager):
    state = state.getState(state)
    spot = world.nearestUnexplored(currentLocation=state.pos, connected=2)
    #todo implement this helper function
    #walkTo(spot)

    return lambda : exploreState(world, state, networkManager)


def endState(world, state, networkManager):
    print "Shutting down the statemachine!"

    return world





