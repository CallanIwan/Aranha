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

#define DIST_HOR	40
#define DIST_VER	50
void SpiderMoveCommand::Execute(Spider* spider)
{
	//Move legs to resting position

	//Generate fullstep command
	ComplexCommand fullstep;

	//For each leg:
	//Get resting pos (R)
	SpiderLeg* leg = spider->GetLeg(0);

	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " Creating frame\n";
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " Resting vector: ";
	Vector3 r = Vector3(100, -100, 0);
	r.Print();
	//Globalize R
	r = leg->Globalize(r);
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " Globalized: ";
	r.Print();
	//Draw frame around R in globalspace
	Matrix rotation = Matrix::CreateRotationY(direction);

	Vector3 a1 = r + Vector3::Transform((Vector3::Forward() * DIST_HOR), rotation);
	Vector3 a2 = r + Vector3::Transform((Vector3::Forward() * DIST_HOR) + (Vector3::Up() * DIST_VER), rotation);
	Vector3 b1 = r + Vector3::Transform((Vector3::Backward() * DIST_HOR) + (Vector3::Up() * DIST_VER), rotation);
	Vector3 b2 = r + Vector3::Transform(Vector3::Backward() * DIST_HOR, rotation);

	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " Rotation: " << direction * (180 / PI) << "\n";
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " A1: ";
	a1.Print();
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " A2: ";
	a2.Print();
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " B1: ";
	b1.Print();
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " B2: ";
	b2.Print();
	//Localize all the points in the frame
	a1 = leg->Localize(a1);
	a2 = leg->Localize(a2);
	b1 = leg->Localize(b1);
	b2 = leg->Localize(b2);
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " Localized:\n";
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " A1: ";
	a1.Print();
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " A2: ";
	a2.Print();
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " B1: ";
	b1.Print();
	std::cout << TERM_RESET << TERM_BOLD << TERM_GREEN << "SpiderMoveCommand>" << TERM_RESET << " B2: ";
	b2.Print();

	//Generate Sync
	std::vector<ISpiderCommand*>* groupA = fullstep.GetTimeline(0);
	//Full cycle
	groupA->push_back(new VectorCommand(0, r));
	//R to A1
	for (float i = 0; i < 1; i += 0.3)
	{
		printf("Progression Vector: ");
		Vector3 vec = lerp(r, a1, i);
		vec.Print();
		groupA->push_back(new VectorCommand(0, vec));
	}
	float inc = 1;
	for (int j = 0; j < 10; j++)
	{
		//A1 to A2
		for (float i = 0; i < 1; i += inc)
		{
			Vector3 vec = lerp(a1, a2, i);
			groupA->push_back(new VectorCommand(0, vec));
		}
		//A2 to B1
		for (float i = 0; i < 1; i += inc)
		{
			Vector3 vec = lerp(a2, b1, i);
			groupA->push_back(new VectorCommand(0, vec));
		}
		//B1 to B2
		for (float i = 0; i < 1; i += inc)
		{
			Vector3 vec = lerp(b1, b2, i);
			groupA->push_back(new VectorCommand(0, vec));
		}
		//B2 to A2
		for (float i = 0; i < 1; i += inc)
		{
			Vector3 vec = lerp(b2, a1, i);
			groupA->push_back(new VectorCommand(0, vec));
		}
	}

	scanf("%*s");
	//run command
	fullstep.Execute(spider);
}