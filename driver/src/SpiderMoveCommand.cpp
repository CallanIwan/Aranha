#include "SpiderMoveCommand.h"

#include <list>

SpiderMoveCommand::SpiderMoveCommand(float direction, float distance)
{
	this->direction = direction;
	this->distance = distance;
}


SpiderMoveCommand::~SpiderMoveCommand()
{
}

//Legs have not yet moved to starting position
#define STATE_NONE		0
//Legs are at beginning of the fullstep cycle
#define STATE_FULLSTEP	1
//Legs are at midway point of the fullstep cycle
#define STATE_HALFSTEP	2
void SpiderMoveCommand::Execute(Spider* spider)
{
	int state = 0;
	//Move legs to resting position

	//Generate fullstep command
	ComplexCommand fullstep;
	std::list<ISpiderCommand*>* groupA = fullstep.GetTimeline(0);
	std::list<ISpiderCommand*>* groupB = fullstep.GetTimeline(1);
	//run command
}