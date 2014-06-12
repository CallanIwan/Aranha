#include "LegSyncCommand.h"

#include <stdio.h>

LegSyncCommand::LegSyncCommand(int legs[], int count)
{
	for (int i = 0; i < count; i++)
	{
		int index = legs[i];
		if (index >= 0 && index < 6)
		{
			flags[index] = true;
		}
	}
}


LegSyncCommand::~LegSyncCommand()
{
}
void LegSyncCommand::Execute(Spider& spider)
{
	int motors[18] = { 0 };
	int mptr = 1;
	for (int i = 0; i < 6;i++)
	{
		if (flags[i])
		{
			SpiderLeg* leg = spider.GetLeg(i);
			motors[mptr + 0] = leg->config.bodyIndex;
			motors[mptr + 1] = leg->config.legIndex;
			motors[mptr + 2] = leg->config.toeIndex;
			mptr += 3;
		}
	}
	printf("Selected motors:");
	for (int i = 0; i < mptr; i++)
	{
		printf(" %i", motors[i]);
	}
	printf("\n");
	spider.GetSpiController()->Synchronize(motors, mptr);
}