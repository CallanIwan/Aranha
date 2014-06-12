#ifndef HEADER_GUARD_LEGSYNCCOMMAND
#define HEADER_GUARD_LEGSYNCCOMAND

#include "ILegCommand.h"
#include "Globals.h"
#include "Spider.h"

class LegSyncCommand :
	public ILegCommand
{
private:
	bool flags[6] = { false };
public:
	LegSyncCommand(int legs[], int count);
	~LegSyncCommand();

	void Execute(Spider& spider);
};

#endif