#ifndef HEADER_GUARD_LEGPATHCOMMAND
#define HEADER_GUARD_LEGPATHCOMMAND

#include <queue>

#include "Globals.h"
#include "ILegCommand.h"
#include "Vector3.h"

class LegPathCommand : public ILegCommand
{
private:
	std::queue<Vector3> path;
public:
	LegPathCommand(int legIndex, std::queue<Vector3> path);
	~LegPathCommand();

	void execute(Spider& spider);
};

#endif