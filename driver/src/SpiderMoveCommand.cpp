#include "SpiderMoveCommand.h"

#include <vector>
#include <iostream>

#include "Spider.h"
#include "SpiderLeg.h"
#include "Vector3.h"
#include "Matrix.h"
#include "VectorCommand.h"
#include "SyncCommand.h"

SpiderMoveCommand::SpiderMoveCommand(float direction, float distance)
{
	this->direction = direction;
	this->distance = distance;
}

SpiderMoveCommand::~SpiderMoveCommand()
{
}

Vector3 lerp(Vector3 start, Vector3 finish, float progression)
{
	return start + ((finish - start)*progression);
}

#define DIST_HOR	30
#define DIST_VER	40

void GenerateLerp(int legindex, std::vector<ISpiderCommand*>* timeline, Vector3 start, Vector3 end, int steps)
{
	std::cout << "Lerp Start" << std::endl;
	//Lerp to end position
	for (float step = 0; step < steps; step++)
	{
		float progress = 1.0 / steps;
		std::cout << "Progression " << progress << std::endl;
		Vector3 vec = lerp(start, end, step);
		timeline->push_back(new VectorCommand(legindex, vec));
	}
}

bool IsPrimaryGroup(int legnum)
{
	return (legnum == 1 || legnum == 3 || legnum == 4);
}

void SpiderMoveCommand::Execute(Spider* spider)
{
	//Move legs to resting position

	//Generate fullstep command
	ComplexCommand fullstep;

	//For each leg:
	for (int legindex = 0; legindex < GLOBAL_LEG_COUNT; legindex++)
	{
		std::cout << "Generating for leg #" << legindex << std::endl;
		//Get resting pos (R)
		SpiderLeg* leg = spider->GetLeg(legindex);
		Vector3 r = Vector3(100, -100, 0);
		std::cout << "Resting position (local)  ";
		r.Print();
		//Globalize R
		r = leg->Globalize(r);
		std::cout << "Resting position (global) ";
		r.Print();

		//Draw frame around R in globalspace
		Matrix rotation = Matrix::CreateRotationY(direction);
		Vector3 a1 = r + Vector3::Transform((Vector3::Forward() * DIST_HOR), rotation);
		Vector3 a2 = r + Vector3::Transform((Vector3::Forward() * DIST_HOR) + (Vector3::Up() * DIST_VER), rotation);
		Vector3 b1 = r + Vector3::Transform((Vector3::Backward() * DIST_HOR) + (Vector3::Up() * DIST_VER), rotation);
		Vector3 b2 = r + Vector3::Transform(Vector3::Backward() * DIST_HOR, rotation);
		//Localize all the points in the frame
		a1 = leg->Localize(a1);
		a2 = leg->Localize(a2);
		b1 = leg->Localize(b1);
		b2 = leg->Localize(b2);
		std::cout << "Local A1";
		a1.Print();
		std::cout << "Local A2";
		a2.Print();
		std::cout << "Local B1";
		b1.Print();
		std::cout << "Local B2";
		b2.Print();
		//Get timeline
		std::vector<ISpiderCommand*>* timeline = fullstep.GetTimeline(legindex);

		SyncLock sync_all(5);
		std::cout << "Timeline belongs to the ";
		if (IsPrimaryGroup(legindex))
		{
			//Primary group starts at A1
			std::cout << "primary";
			timeline->push_back(new VectorCommand(legindex, a1));
			timeline->push_back(new VectorCommand(legindex, a2));
			timeline->push_back(new SyncCommand(&sync_all));
			timeline->push_back(new VectorCommand(legindex, b1));
			timeline->push_back(new VectorCommand(legindex, b2));
			timeline->push_back(new SyncCommand(&sync_all));
		}
		else
		{
			//Secondary group starts at B1
			std::cout << "secondary";
			timeline->push_back(new VectorCommand(legindex, b1));
			timeline->push_back(new VectorCommand(legindex, b2));
			timeline->push_back(new SyncCommand(&sync_all));
			timeline->push_back(new VectorCommand(legindex, a1));
			timeline->push_back(new VectorCommand(legindex, a2));
			timeline->push_back(new SyncCommand(&sync_all));
		}
		std::cout << " group" << std::endl;
		//Full cycle
		timeline->push_back(new VectorCommand(legindex, r));
	}
	//run command
	fullstep.Execute(spider);
}