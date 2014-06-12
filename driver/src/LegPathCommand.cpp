#include "LegPathCommand.h"
#include "Spider.h"

#include <stdio.h>

LegPathCommand::LegPathCommand(int index)
{
	this->legIndex = index;
}
LegPathCommand::LegPathCommand(int index, std::queue<Vector3> path)
{
	this->legIndex = index;
	this->path = path;
}


LegPathCommand::~LegPathCommand()
{
}

void LegPathCommand::AddVector(Vector3 item)
{
	path.push(item);
}
void LegPathCommand::Execute(Spider& spider)
{
	//Get the leg we operate on
	SpiderLeg* leg = spider.GetLeg(legIndex);
	printf(TERM_RESET TERM_BOLD TERM_GREEN "LegPathCommand>" TERM_RESET " Starting path:\n");
	//While there are elements in the path
	while (!path.empty())
	{
		//Pop element from queue
		Vector3 target = path.front();
		path.pop();
		printf(TERM_BOLD TERM_GREEN "LegPathCommand>" TERM_RESET " New target: ");
		target.Print();
		//Move leg to elements position
		//Movement is synchronized
		leg->SetAngles(target, false);// path.empty());
		printf(TERM_BOLD TERM_GREEN "LegPathCommand>" TERM_RESET " Synchronizing...\n");
		leg->Synchronize();
		printf(TERM_BOLD TERM_GREEN "LegPathCommand>" TERM_RESET " Synchronization Completed\n");
	}
	printf(TERM_BOLD TERM_GREEN "LegPathCommand>" TERM_RESET " End of path\n");
	//return when queue is empty
}