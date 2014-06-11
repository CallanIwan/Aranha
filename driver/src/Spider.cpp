#include "Spider.h"

#include <queue>
#include <vector>

#include "SpiderLeg.h"
#include "ISpiderCommand.h"
#include "SpiController.h"

Spider::Spider()
{
	commandQueue = std::queue<ISpiderCommand>();
	//Initialize SPI
	spiController = SpiController();
}


Spider::~Spider()
{

}

bool Spider::setLeg(int index, SpiderLeg newLeg)
{
	if (index < 0 || index >= GLOBAL_LEG_COUNT)
	{
		return false;
	}
	legs[index] = newLeg;
	return true;
}

SpiderLeg* Spider::getLeg(int index)
{
	if (index < 0 || index >= GLOBAL_LEG_COUNT)
	{
		return 0;
	}
	return &(legs[index]);
}

void Spider::think()
{
	//Update SPI targets
	//Update SPI progress
	//Run activespider command
	//if command is done, then load next command
}

void Spider::queueCommand(ISpiderCommand command)
{
	commandQueue.push(command);
}

SpiController Spider::getSpiController()
{
	return spiController;
}