#ifndef HEADER_GUARD_MOVECOMMAND
#define HEADER_GUARD_MOVECOMMAND

#include <queue>

#include "Globals.h"
#include "Vector3.h"
#include "ISpiderCommand.h"
#include "ComplexCommand.h"

#define MOVECOMMAND_STATE_NEUTRAL	1
#define MOVECOMMAND_STATE_START		2
#define MOVECOMMAND_STATE_HALFWAY	3
/**
This class makes the spider move in a specific direction
*/
class MoveCommand : public ISpiderCommand
{
private:
	ComplexCommand first_half;
	ComplexCommand second_half;
	int state;
	int steps;
	//Direction to move in, 0 is forward, PI is backward, this scale is clockwise
	float direction;
	//Distance to move by, distance is in millimeters
	float distance;
	Vector3 origins[6];
	void GenerateTimelines(Spider* spider);
	SyncLock* sync_half_pre;
	SyncLock* sync_half;
	SyncLock* sync_full_pre;
	SyncLock* sync_full;
public:
	//Creates a new move command that moves in a given direction
	MoveCommand(Spider* spider, float direction, Vector3 origins[6], int steps);
	~MoveCommand();
	void Execute(Spider* spider);
};

#endif