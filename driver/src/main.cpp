#include <stdio.h>
#include <string.h>
#include <bcm2835.h>
#include <math.h>
#include <unistd.h>
#include <vector>
#include <iostream>

#include "Globals.h"
#include "Spider.h"
#include "Vector3.h"
#include "ComplexCommand.h"
#include "SyncCommand.h"
#include "VectorCommand.h"
#include "SpiderMoveCommand.h"

//Spider spider;

void isRoot()
{
	if (geteuid() != 0)
	{
		printf("This program requires root privileges\n");
	}
}

Vector3 GetVector()
{
	printf("Enter a vector, which requires 3 floating point numbers, seperated by a space\n");
	float x, y, z;
	scanf("%f %f %f", &x, &y, &z);
	Vector3 vec(x, y, z);
	return vec;
}
#define G2R	(PI / 180)
void SetLeg(Spider* spider, int index, int motorstart, float bodyLength, float legLength, float toeLength, float bodyOffset, float legOffset, float toeOffset, bool bodyInv, bool legInv, bool toeInv, Vector3 position, float rotation)
{
	LegConfig config;
	config.SetIndexes(motorstart + 0, motorstart + 1, motorstart + 2);
	config.SetLength(bodyLength, legLength, toeLength);
	config.SetOffsets(bodyOffset, legOffset, toeOffset);
	config.SetReversed(bodyInv, legInv, toeInv);
	SpiderLeg leg = SpiderLeg(spider, position, rotation, config);
	spider->SetLeg(index, leg);
}

void SpiderSetup(Spider* spider)
{
	float bl = 42.0;
	float ll = 58.2;
	float tl = 123.6;
	/* FRONT LEFT LEG / TESTING LEG */
	SetLeg(spider, 0, 0,
		bl, ll, tl,
		20, -80, 20,
		true, true, false,
		Vector3(0, 0, 0), 0 * G2R);
	/* FRONT RIGHT LEG */
	SetLeg(spider, 1, 3,
		bl, ll, tl,
		0, -40, 0,
		true, true, false,
		Vector3(100, 0, 0), 90 * G2R);
	/* MIDDLE LEFT LEG */
	SetLeg(spider, 2, 6,
		bl, ll, tl,
		0, -40, 0,
		true, true, false,
		Vector3(100, 0, 0), 90 * G2R);
	/* MIDDLE RIGHT LEG */
	SetLeg(spider, 3, 9,
		bl, ll, tl,
		0, -40, 0,
		true, true, false,
		Vector3(100, 0, 0), 90 * G2R);
	/* BACK RIGHT LEG */
	SetLeg(spider, 4, 12,
		bl, ll, tl,
		0, -40, 0,
		true, true, false,
		Vector3(100, 0, 0), 90 * G2R);
	/* BACK RIGHT LEG */
	SetLeg(spider, 5, 15,
		bl, ll, tl,
		0, -40, 0,
		true, true, false,
		Vector3(100, 0, 0), 90 * G2R);
}

int main(int argc, const char* argv[])
{
	printf(TERM_BOLD TERM_RED "red ");
	printf(TERM_GREEN "green ");
	printf(TERM_YELLOW "yellow ");
	printf(TERM_BLUE "blue ");
	printf(TERM_MAGENTA "magenta ");
	printf(TERM_CYAN "cyan ");
	printf(TERM_WHITE "white ");
	printf(TERM_NORMAL "normal\n" TERM_RESET);

	printf("Program Entry succesfull\n");
	Spider* spider = new Spider();

	SpiderSetup(spider);

	spider->Print();

	spider->GetSpiController()->Enable();
	spider->GetSpiController()->SetAngle(0, 100, 100, true);
	return 0;
	/*
	printf("\nTesting SyncCommand\n");

	//Set all legs to 0 (instantly)
	for (int i = 0; i < 18; i++)
	{
	spider->GetSpiController()->SetAngle(i, 0, 199, false);
	}
	usleep(1000 * 40);
	//Create command before setting the motors
	int legs[6] = { 0, 1, 2, 3, 4, 5 };
	//SyncCommand cmd = SyncCommand(legs, 6);

	for (int i = 0; i < 18; i++)
	{
	spider->GetSpiController()->SetAngle(i, 199, 1, false);
	}
	printf("All the legs are on their way\n");
	//cmd.Execute(spider);
	printf("All legs are now synchronized\n");

	//*/

	/*
	printf("\nTesting ComplexCommand\n");

	ComplexCommand complex = ComplexCommand();

	std::vector<ISpiderCommand*>* timeline0 = complex.GetTimeline(0);
	std::vector<ISpiderCommand*>* timeline1 = complex.GetTimeline(1);
	SyncLock* sync1 = new SyncLock(2);
	SyncLock* sync2 = new SyncLock(2);

	timeline0->push_back(new VectorCommand(0, Vector3::Forward() * 50));
	timeline0->push_back(new VectorCommand(0, Vector3::Forward() * 150));
	timeline0->push_back(new VectorCommand(0, Vector3::Forward() * 100));
	timeline0->push_back(new SyncCommand(sync1));
	timeline0->push_back(new VectorCommand(0, Vector3::Forward() * 200));
	timeline0->push_back(new SyncCommand(sync2));

	timeline1->push_back(new VectorCommand(0, Vector3::Forward() * 50));
	timeline1->push_back(new SyncCommand(sync1));
	timeline1->push_back(new VectorCommand(0, Vector3::Forward() * 150));
	timeline1->push_back(new VectorCommand(0, Vector3::Forward() * 100));
	timeline1->push_back(new VectorCommand(0, Vector3::Forward() * 200));
	timeline1->push_back(new SyncCommand(sync2));

	complex.Print();

	printf("\nStarting timeline now!\n");
	complex.Execute(spider);
	//*/

	spider->GetLeg(0)->SetAngles(0, 0, PI / 2, true);

	usleep(1000 * 1000 * 2);

	VectorCommand relax = VectorCommand(0, Vector3(180, 0, 0));
	relax.Execute(spider);

	SpiderMoveCommand smc = SpiderMoveCommand(90 * G2R, 1);

	smc.Execute(spider);
}