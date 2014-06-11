#ifndef HEADER_GUARD_LEGPATHCOMMAND
#define HEADER_GUARD_LEGPATHCOMMAND

#include <queue>

#include "Globals.h"
#include "ILegCommand.h"
#include "Vector3.h"

class LegPathCommand : public ILegCommand
{
private:
	int legIndex;
	std::queue<Vector3> path;
public:
	LegPathCommand(int legIndex, std::queue<Vector3> path);
	LegPathCommand(int legIndex);
	~LegPathCommand();
	/**
	Adds a new vector to the queue.
	Warning: cannot be removed from queue outside of this class.
	Only add vectors when these vectors are final
	*/
	void AddVector(Vector3 item);

	void Execute(Spider& spider);
};

#endif