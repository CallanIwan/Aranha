#ifndef HEADER_GUARD_SPIDER
#define HEADER_GUARD_SPIDER

#include <queue>
#include <vector>

#include "Globals.h"
#include "SyncLock.h"
#include "SpiderLeg.h"
#include "ISpiderCommand.h"
#include "SpiController.h"

class Spider : public SyncLock
{
private:
	SpiderLeg legs[GLOBAL_LEG_COUNT];
	std::queue<ISpiderCommand> commandQueue;
	SpiController spiController;
public:
	Spider();
	~Spider();

	//Set a leg of the spider, returns true when succesfull, false when the index is out of range
	bool setLeg(int index, SpiderLeg newLeg);
	//Gets a leg of the spider, returns null when the index is out of range, or the leg is not set
	SpiderLeg* getLeg(int index);
	//Adds a command to the queue of the spider
	void queueCommand(ISpiderCommand command);
	//This method implements the AI of the spider, this should be run in an infinite loop
	void think();

	SpiController getSpiController();
};

#endif