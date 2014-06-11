#ifndef HEADER_GUARD_SPIDERMOVECOMMAND
#define HEADER_GUARD_SPIDERMOVECOMMAND

#include <queue>

#include "Globals.h"
#include "Vector3.h"
#include "ISpiderCommand.h"

class SpiderMoveCommand : public ISpiderCommand
{
private:
	std::queue<Vector3> path;
public:
	SpiderMoveCommand();
	~SpiderMoveCommand();
	void Execute(Spider& spider);
};

#endif