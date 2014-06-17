#include "Spider.h"

#include <queue>
#include <vector>
#include <stdio.h>

#include "SpiderLeg.h"
#include "ISpiderCommand.h"
#include "SpiController.h"
#include "Vector3.h"

Spider::Spider()
{
	commandQueue = std::queue<ISpiderCommand>();
}

Spider::~Spider()
{
}
bool Spider::SetLeg(int index, SpiderLeg newLeg)
{
	if (index < 0 || index >= GLOBAL_LEG_COUNT)
	{
		return false;
	}
	legs[index] = newLeg;
	return true;
}

SpiderLeg* Spider::GetLeg(int index)
{
	if (index < 0 || index >= GLOBAL_LEG_COUNT)
	{
		return 0;
	}
	return &(legs[index]);
}

void Spider::Think()
{
	//Update SPI targets
	//Update SPI progress
	//Run activespider command
	//if command is done, then load next command
}

void Spider::Print()
{
	printf("Spider object debug report\n");
	printf("Memory size: %i bytes\n", sizeof(*this));
	for (int i = 0; i < GLOBAL_LEG_COUNT; i++)
	{
		SpiderLeg* leg = GetLeg(i);
		printf("Leg: %i [%p]\n", i, leg);
		printf("    Memory size: %i bytes\n", sizeof(*leg));
		Vector3 local = leg->Localize(Vector3::One());
		printf("    Localize  (Matrix::One)       {%8.2f,%8.2f,%8.2f}\n", local.x, local.y, local.z);
		Vector3 global = leg->Globalize(Vector3::One());
		printf("    Globalize (Matrix::One)       {%8.2f,%8.2f,%8.2f}\n", global.x, global.y, global.z);
		Vector3 forward = leg->Globalize(Vector3(100, 0, 0));
		printf("    Globalize (100 units forward) {%8.2f,%8.2f,%8.2f}\n", forward.x, forward.y, forward.z);
	}
}

void Spider::QueueCommand(ISpiderCommand command)
{
	commandQueue.push(command);
}

SpiController* Spider::GetSpiController()
{
	return &spiController;
}