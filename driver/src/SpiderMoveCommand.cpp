#include "SpiderMoveCommand.h"


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
void SpiderMoveCommand::Execute(Spider& spider)
{
	int state = 0;
	//Check if feet are at starting positions(fullstep) (tolerance: 10 units total)
		//If so, continue with normal walk cycle
	//Else check if the feet are at half step positions (tolerance: 10 units total)
		//If so, continue with normal walk cycle, but start halfway the first cycle
	//Else correct feet to beginning positions of fullstep

	//Keep walking until 
	while (distance >= SPIDERMOVECOMMAND_STEPSIZE)
	{
		//Get current position in path (beginning / halfstep)
		//Determine best movement path (optimization, do later)
		//Execute half step

		if (state == STATE_HALFSTEP)
		{
			state = STATE_FULLSTEP;
		}
		else
		{
			state = STATE_HALFSTEP;
		}

		distance -= SPIDERMOVECOMMAND_STEPSIZE;
	}
	if (distance > 1)
	{
		//Use micro steps
	}
}