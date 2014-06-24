#include "MoveCommand.h"

#include <vector>
#include <iostream>

#include "Spider.h"
#include "SpiderLeg.h"
#include "Vector3.h"
#include "Matrix.h"
#include "VectorCommand.h"
#include "SyncCommand.h"
#include "StabalizeCommand.h"

MoveCommand::MoveCommand(Spider* spider, float direction, Vector3 origins[], int stepsize, int steps)
{
	this->direction = direction;
	std::cout << TERM_RESET << TERM_BOLD << TERM_MAGENTA << "MoveCommand> " << TERM_RESET "Origins:" << std::endl;
	for (int i = 0; i < 6; i++)
	{
		this->origins[i] = origins[i];
		origins[i].Print();
	}
	this->stepsize = stepsize;
	this->steps = steps;
	//Generate walking pattern
	sync_half_pre = new SyncLock(GLOBAL_LEG_COUNT);
	sync_half = new SyncLock(GLOBAL_LEG_COUNT);
	sync_full_pre = new SyncLock(GLOBAL_LEG_COUNT);
	sync_full = new SyncLock(GLOBAL_LEG_COUNT);
	GenerateTimelines(spider);
}

MoveCommand::~MoveCommand()
{
	delete sync_half;
	delete sync_half_pre;
	delete sync_full;
	delete sync_full_pre;
}
#define DIST_VER	100

void MoveCommand::GenerateTimelines(Spider* spider)
{
	Matrix rotation = Matrix::CreateRotationY(direction);
	//For each leg:
	for (int legindex = 0; legindex < GLOBAL_LEG_COUNT; legindex++)
	{
		//Get resting pos (R)
		SpiderLeg* leg = spider->GetLeg(legindex);
		Vector3 r = origins[legindex];

		//Draw frame around R in globalspace
		r = leg->Globalize(r);
		Vector3 a1 = r + Vector3::Transform((Vector3::Forward() * stepsize), rotation);
		Vector3 a2 = r + Vector3::Transform((Vector3::Forward() * stepsize) + (Vector3::Up() * DIST_VER), rotation);
		Vector3 b1 = r + Vector3::Transform((Vector3::Backward() * stepsize) + (Vector3::Up() * DIST_VER), rotation);
		Vector3 b2 = r + Vector3::Transform(Vector3::Backward() * stepsize, rotation);

		//Localize all the points in the frame
		a1 = leg->Localize(a1);
		a2 = leg->Localize(a2);
		b1 = leg->Localize(b1);
		b2 = leg->Localize(b2);

		//Set timelines
		std::vector<ISpiderCommand*>* tl1 = first_half.GetTimeline(legindex);
		std::vector<ISpiderCommand*>* tl2 = second_half.GetTimeline(legindex);

		if (spider->IsPrimaryGroup(legindex))
		{
			//Primary group starts at A1
			tl1->push_back(new VectorCommand(legindex, a1));//low
			tl1->push_back(new SyncCommand(sync_half_pre));//Wait for other legs to touch down
			tl1->push_back(new VectorCommand(legindex, a2));//high
			tl1->push_back(new SyncCommand(sync_half));

			//Second half of the animation
			tl2->push_back(new VectorCommand(legindex, b1));//high
			tl2->push_back(new VectorCommand(legindex, b2));//low
			tl2->push_back(new SyncCommand(sync_full_pre));
			tl2->push_back(new SyncCommand(sync_full));//Wait for other legs to be lifted
		}
		else
		{
			//Secondary group starts at B1
			tl1->push_back(new VectorCommand(legindex, b1));//high
			tl1->push_back(new VectorCommand(legindex, b2));//low
			tl1->push_back(new SyncCommand(sync_half_pre));
			tl1->push_back(new SyncCommand(sync_half));//Wait for other legs to be lifted

			//Second half of the animation
			tl2->push_back(new VectorCommand(legindex, a1));//low
			tl2->push_back(new SyncCommand(sync_full_pre));//Wait for other legs to touch down
			tl2->push_back(new VectorCommand(legindex, a2));//high
			tl2->push_back(new SyncCommand(sync_full));
		}
	}
}

void MoveCommand::Execute(Spider* spider)
{
	//Perform fullstep untill no more steps can be taken
	for (int i = 0; i < steps; i++)
	{
		first_half.Execute(spider);
		sync_half->Reset();
		sync_half_pre->Reset();
		i++;
		if (i < steps)
		{
			second_half.Execute(spider);
			sync_full->Reset();
			sync_full_pre->Reset();
		}
		else
		{
			break;
		}
	}
	//Put legs on origin points
}