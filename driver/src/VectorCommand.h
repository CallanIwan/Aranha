#pragma once

#include "ISpiderCommand.h"
#include "Globals.h"
#include "Vector3.h"

class VectorCommand : public ISpiderCommand
{
private:
	int legIndex;
	Vector3 target;
public:
	VectorCommand(int legIndex, Vector3 target);
	~VectorCommand();

	void Execute(Spider* spider);
};