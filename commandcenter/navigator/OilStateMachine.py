import Client
from navigator.Ray import Ray
from navigator.World import World



def startState(world, s):
    print "Booting up statemachine!"
    return lambda: analyzeState(world, s)


#turn the bot around a bit, analyze world
def analyzeState(world, s):
    print "AnalyzeMode"

    #rotate 120 degrees* and keep fetching sensor data
    #TODO: actually give the right command
    Client.sendCommand(s, chr(3))

    for i in range(10):
        state = Client.fetchState()
        world.applyRay(Ray(state.pos, state.rot, state.dist))

    return lambda: exploreState(world, s)



def exploreState(world, s):
    #exploration will be thorough, this means it will prioritize fully mapping nearby spots
    #over larger unexplored areas further away.
    #todo: aggressive exploration vs precise?

    #TODO:implement these functions
    #state = getState()
    #spot = world.nearestUnexplored(currentLocation=state.pos, area=2)
    #walkTo(spot)



    return lambda : exploreState(world, s)

def endState(world, s):
    print "Shutting down the statemachine!"
    return world


class OilStateMachine(object):
    def run(self, startState, s):
        fn = startState(World(), s)

        #trampoline!
        while fn != None:
            fn = fn()


