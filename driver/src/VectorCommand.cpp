#include "VectorCommand.h"

#include <iostream>

#include "Spider.h"
#include "SpiderLeg.h"



/**
Command for moving one leg to one specific vector (local to that leg)
This command blocks the thread until the leg has reached the target
*/

VectorCommand::VectorCommand(int legIndex, Vector3 target)
{
	this->legIndex = legIndex;
	this->target = target;
}


VectorCommand::~VectorCommand()
{
}

void VectorCommand::Execute(Spider* spider)
{
	//Get leg
	SpiderLeg* leg = spider->GetLeg(legIndex);
	//Command the leg to move to target
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "VectorCommand> " << TERM_RESET << "Moving leg " << legIndex << "to ";
	target.Print();
	leg->SetAngles(target, true);
	//Return when done
}