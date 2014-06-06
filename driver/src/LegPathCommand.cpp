#include "LegPathCommand.h"


LegPathCommand::LegPathCommand(int index, std::queue<Vector3> path)
{
	this->path = path;
}


LegPathCommand::~LegPathCommand()
{
}

void LegPathCommand::execute(Spider& spider)
{
	//while there are elements in the path
	//pop element from queue
	//move leg to elements position
	//return when queue is empty
}