#include "StabalizeCommand.h"

#include <vector>
#include <iostream>

#include "Spider.h"
#include "SpiderLeg.h"
#include "Vector3.h"
#include "Matrix.h"
#include "VectorCommand.h"
#include "SyncCommand.h"

StabalizeCommand::StabalizeCommand(Vector3 destinations[])
{
	for (int i = 0; i < GLOBAL_LEG_COUNT; i++)
	{
		this->destinations[i] = destinations[i];
	}
}

StabalizeCommand::~StabalizeCommand()
{
}

void PopulateTimeline(Spider* spider, std::vector<ISpiderCommand*>* timeline, int legindex, Vector3 target)
{
	SpiderLeg* leg = spider->GetLeg(legindex);
	Vector3 elevated_start = leg->CurrentPosition + Vector3::Up() * 50;
	Vector3 elevated_destination = target + Vector3::Up() * 50;
	timeline->push_back(new VectorCommand(legindex, elevated_start));
	timeline->push_back(new VectorCommand(legindex, elevated_destination));
	timeline->push_back(new VectorCommand(legindex, target));
}

void StabalizeCommand::Execute(Spider* spider)
{
	//Frequently used
	SpiderLeg* leg;
	//Get lowest height a leg has to move to during the animation
	float lowestY = spider->GetLeg(0)->CurrentPosition.y;
	for (int i = 1; i < GLOBAL_LEG_COUNT; i++)
	{
		if (spider->GetLeg(i)->CurrentPosition.y < lowestY)
		{
			lowestY = spider->GetLeg(i)->CurrentPosition.y;
		}
		if (destinations[i].y < lowestY)
		{
			lowestY = destinations[i].y;
		}
	}
	std::cout << TERM_RESET << TERM_BOLD << TERM_MAGENTA << "StabalizeCommand> " << TERM_RESET << "Lowest Y component: " << lowestY << std::endl;
	//Move all legs to that height simultaniously
	for (int i = 0; i < GLOBAL_LEG_COUNT; i++)
	{
		leg = spider->GetLeg(i);
		Vector3 target = leg->CurrentPosition;
		target.y = lowestY;
		leg->SetAngles(target, false);
	}
	for (int i = 0; i < GLOBAL_LEG_COUNT; i++)
	{
		leg = spider->GetLeg(i);
		leg->SetAngles(leg->CurrentPosition, true);
	}

	//Move the legs to their destinations
	ComplexCommand cc;
	std::vector<ISpiderCommand*>* timelines[6] = {
		cc.GetTimeline(0),
		cc.GetTimeline(1),
		cc.GetTimeline(2),
		cc.GetTimeline(3),
		cc.GetTimeline(4),
		cc.GetTimeline(5),
	};

	SyncLock* groupA = new SyncLock(6);
	SyncLock* groupB = new SyncLock(6);
	SyncLock* groupC = new SyncLock(6);

	//Move leg 0 and 5 to their destinations, making 1 2 3 4 wait
	timelines[1]->push_back(new SyncCommand(groupA));
	timelines[2]->push_back(new SyncCommand(groupA));
	timelines[3]->push_back(new SyncCommand(groupA));
	timelines[4]->push_back(new SyncCommand(groupA));
	PopulateTimeline(spider, timelines[0], 0, destinations[0]);
	PopulateTimeline(spider, timelines[5], 5, destinations[5]);

	timelines[0]->push_back(new SyncCommand(groupA));
	timelines[5]->push_back(new SyncCommand(groupA));

	//Move leg 1 and 4 to their destinations, making 0 2 3 5 wait
	timelines[0]->push_back(new SyncCommand(groupB));
	timelines[2]->push_back(new SyncCommand(groupB));
	timelines[3]->push_back(new SyncCommand(groupB));
	timelines[5]->push_back(new SyncCommand(groupB));
	PopulateTimeline(spider, timelines[1], 1, destinations[1]);
	PopulateTimeline(spider, timelines[4], 4, destinations[4]);

	timelines[1]->push_back(new SyncCommand(groupB));
	timelines[4]->push_back(new SyncCommand(groupB));

	//Move leg 2 and 3 to their destinations making 0 1 5 6 wait
	timelines[0]->push_back(new SyncCommand(groupC));
	timelines[1]->push_back(new SyncCommand(groupC));
	timelines[4]->push_back(new SyncCommand(groupC));
	timelines[5]->push_back(new SyncCommand(groupC));
	PopulateTimeline(spider, timelines[2], 2, destinations[2]);
	PopulateTimeline(spider, timelines[3], 3, destinations[3]);

	timelines[2]->push_back(new SyncCommand(groupC));
	timelines[3]->push_back(new SyncCommand(groupC));

	cc.Execute(spider);

	delete groupA;
	delete groupB;
	delete groupC;
}