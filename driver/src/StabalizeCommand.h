#ifndef HEADER_GUARD_STABALIZECOMMAND
#define HEADER_GUARD_STABALIZECOMMAND

#include <queue>

#include "Globals.h"
#include "Vector3.h"
#include "ISpiderCommand.h"
#include "ComplexCommand.h"

/**
This class puts the legs of the spider at specific points in space, safely
*/
class StabalizeCommand : public ISpiderCommand
{
private:
	Vector3 destinations[GLOBAL_LEG_COUNT];
	bool middleFirst;
public:
	//Creates a new stabalize command that puts the legs at the given coordinates
	StabalizeCommand(Vector3 destinations[GLOBAL_LEG_COUNT], bool middleFirst);
	~StabalizeCommand();
	void Execute(Spider* spider);
};

#endif