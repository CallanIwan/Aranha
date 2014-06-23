#ifndef HEADER_GUARD_TURNCOMMAND
#define HEADER_GUARD_TURNCCOMMAND

#include "ISpiderCommand.h"
#include "Globals.h"
#include "Spider.h"
#include "SyncLock.h"
#include "ComplexCommand.h"

class TurnCommand : public ISpiderCommand
{
private:
	ComplexCommand first_half;
	ComplexCommand second_half;
	int state;
	float turn;
	void GenerateTimelines(Spider* spider, float framewidth, float frameheight);
	SyncLock* sync_half_pre;
	SyncLock* sync_half;
	SyncLock* sync_full_pre;
	SyncLock* sync_full;
public:
	/**
	Creates a new turn command that will make the spider turn the given angle clockwise, supports negative for counterclockwise
	Clamping will cause the spider turn to the given angle
	*/
	TurnCommand(Spider* spider, float turnangle);
	~TurnCommand();
	void Execute(Spider* spider);
};

#endif