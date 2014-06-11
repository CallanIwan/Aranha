#ifndef HEADER_GUARD_SPIDERMOVECOMMAND
#define HEADER_GUARD_SPIDERMOVECOMMAND

#include <queue>

#include "Globals.h"
#include "Vector3.h"
#include "ISpiderCommand.h"

//Define the size of a step, if the distance is smaller then this size, the spider will use microsteps
#define SPIDERMOVECOMMAND_STEPSIZE	40

class SpiderMoveCommand : public ISpiderCommand
{
private:
	std::queue<Vector3> path;
	//Direction to move in, 0 is forward, PI is backward, this scale is clockwise
	float direction;
	//Distance to move by, distance is in millimeters
	float distance;
public:
	SpiderMoveCommand(float direction, float distance);
	~SpiderMoveCommand();
	void Execute(Spider& spider);
};

#endif