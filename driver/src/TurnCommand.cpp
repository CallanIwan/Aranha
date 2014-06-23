#include "TurnCommand.h"

#include <iostream>
#include <unistd.h>

#include "Spider.h"
#include "SpiderLeg.h"
#include "Vector3.h"
#include "Matrix.h"
#include "VectorCommand.h"
#include "SyncCommand.h"
#include "StabalizeCommand.h"

TurnCommand::TurnCommand(float radianTurn, bool clamp)
{
	//Clamps the rotation between -PI and PI
	if (clamp)
	{
		while (radianTurn > PI)
		{
			radianTurn -= 2 * PI;
		}
		while (radianTurn < -PI)
		{
			radianTurn += 2 * PI;
		}
	}
	this->turn = radianTurn;
	sync_half_pre = new SyncLock(GLOBAL_LEG_COUNT);
	sync_half = new SyncLock(GLOBAL_LEG_COUNT);
	sync_full_pre = new SyncLock(GLOBAL_LEG_COUNT);
	sync_full = new SyncLock(GLOBAL_LEG_COUNT);
}

TurnCommand::~TurnCommand()
{
	delete sync_half;
	delete sync_half_pre;
	delete sync_full;
	delete sync_full_pre;
}

void TurnCommand::GenerateTimelines(Spider* spider, float framerotation, float frameheight)
{
	//For each leg:
	for (int legindex = 0; legindex < GLOBAL_LEG_COUNT; legindex++)
	{
		//Get resting pos (R)
		SpiderLeg* leg = spider->GetLeg(legindex);
		Vector3 r = Vector3(150, -70, 0);

		//Draw frame around R in globalspace
		//r = leg->Globalize(r);
		Vector3 a1 = Vector3::Transform(r, Matrix::CreateRotationY(framerotation * (PI / 180)));
		Vector3 a2 = a1 + (Vector3::Up() * frameheight);
		Vector3 b2 = Vector3::Transform(r, Matrix::CreateRotationY(-framerotation * (PI / 180)));
		Vector3 b1 = b2 + (Vector3::Up() * frameheight);

		std::cout << "Calculated vectors:" << std::endl;
		std::cout << "A1: ";
		a1.Print();
		std::cout << "A2: ";
		a2.Print();
		std::cout << "B1: ";
		b1.Print();
		std::cout << "B2: ";
		b2.Print();

		//Set timelines
		std::vector<ISpiderCommand*>* tl1 = first_half.GetTimeline(legindex);
		std::vector<ISpiderCommand*>* tl2 = second_half.GetTimeline(legindex);

		if (spider->IsPrimaryGroup(legindex))
		{
			std::cout << "Primary" << std::endl;
			//Primary group starts at A1
			tl1->push_back(new VectorCommand(legindex, a1));//low
			tl1->push_back(new SyncCommand(sync_half_pre));//Wait for other legs to touch down
			tl1->push_back(new VectorCommand(legindex, a2));//high
			tl1->push_back(new SyncCommand(sync_half));//Full sync

			//Second half of the animation
			tl2->push_back(new VectorCommand(legindex, b1));//high
			tl2->push_back(new VectorCommand(legindex, b2));//low
			tl2->push_back(new SyncCommand(sync_full_pre));
			tl2->push_back(new SyncCommand(sync_full));//Wait for other legs to be lifted
		}
		else
		{
			std::cout << "Secondary" << std::endl;
			//Secondary group starts at B1
			tl1->push_back(new VectorCommand(legindex, b1));//high
			tl1->push_back(new VectorCommand(legindex, b2));//low
			tl1->push_back(new SyncCommand(sync_half_pre));
			tl1->push_back(new SyncCommand(sync_half));//Wait for other legs to be lifted

			//Second half of the animation
			tl2->push_back(new VectorCommand(legindex, a1));//low
			tl2->push_back(new SyncCommand(sync_full_pre));//Wait for other legs to touch down
			tl2->push_back(new VectorCommand(legindex, a2));//high
			tl2->push_back(new SyncCommand(sync_full));//Full sync
		}
	}
}

void TurnCommand::Execute(Spider* spider)
{
	std::cout << TERM_RESET << TERM_BOLD << TERM_MAGENTA << "TurnCommand> " << TERM_RESET << "Generating timelines" << std::endl;
	GenerateTimelines(spider, 20, 40);
	while (true)
	{
		printf("Press enter to start first half of turn command");
		scanf("%*c");
		std::cout << TERM_RESET << TERM_BOLD << TERM_MAGENTA << "TurnCommand> " << TERM_RESET << "Half Step" << std::endl;
		first_half.Execute(spider);
		sync_half->Reset();
		sync_half_pre->Reset();
		printf("Press enter to start second half of turn command");
		scanf("%*c");
		std::cout << TERM_RESET << TERM_BOLD << TERM_MAGENTA << "TurnCommand> " << TERM_RESET << "Full Step" << std::endl;
		second_half.Execute(spider);
		sync_full->Reset();
		sync_full_pre->Reset();
	}
	std::cout << TERM_RESET << TERM_BOLD << TERM_MAGENTA << "TurnCommand> " << TERM_RESET << "Executing timelines" << std::endl;
}