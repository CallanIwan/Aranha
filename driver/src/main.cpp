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
		exit(0);
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
void SetLeg(Spider* spider, int index, int motorbody, int motorleg, int motortoe, float bodyLength, float legLength, float toeLength, float bodyOffset, float legOffset, float toeOffset, bool bodyInv, bool legInv, bool toeInv, Vector3 position, float rotation)
{
	LegConfig config;
	config.SetIndexes(motorbody, motorleg, motortoe);
	config.SetLength(bodyLength, legLength, toeLength);
	config.SetOffsets(bodyOffset, legOffset, toeOffset);
	config.SetReversed(bodyInv, legInv, toeInv);
	SpiderLeg leg = SpiderLeg(spider, position, rotation, config);
	spider->SetLeg(index, leg);
}

void SpiderSetup(Spider* spider)
{
	//Tegen de klok in is een hogere graad
	//Leg: staat het aandrijf punt van de motor aan de linker kant, dan is deze geinverteerd
	float bl = 42.0;
	float ll = 58.2;
	float tl = 123.6;
	/* FRONT LEFT LEG / TESTING LEG */
	SetLeg(spider, 0,
		0, 1, 2,
		bl, ll, tl,
		0, 0, 0,
		false, false, false,
		Vector3(82, 0, 50), 45 * G2R);
	/* FRONT RIGHT LEG */
	SetLeg(spider, 1,
		3, 4, 5,
		bl, ll, tl,
		0, -5, 0,
		false, true, true,
		Vector3(82, 0, -50), -45 * G2R);
	/* MIDDLE LEFT LEG */
	SetLeg(spider, 2,
		6, 7, 15,
		bl, ll, tl,
		0, 0, 0,
		false, false, true,
		Vector3(0, 0, 54), 90 * G2R);
	/* MIDDLE RIGHT LEG */
	SetLeg(spider, 3,
		14, 13, 12,
		bl, ll, tl,
		0, 0, 0,
		false, true, false,
		Vector3(0, 0, -54), -90 * G2R);
	/* BACK LEFT LEG */
	SetLeg(spider, 4,
		11, 10, 9,
		bl, ll, tl,
		0, 0, 0,
		false, false, true,
		Vector3(-82, 0, 50), 135 * G2R);
	/* BACK RIGHT LEG */
	SetLeg(spider, 5,
		8, 19, 18,
		bl, ll, tl,
		0, 1, 0,
		false, true, false,
		Vector3(-82, 0, -50), -135 * G2R);
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

	isRoot();

	printf("Program Entry succesfull\n");
	Spider* spider = new Spider();

	SpiderSetup(spider);

	spider->Print();

	spider->GetSpiController()->Enable();

	/*
	for (int i = 0; i < 20; i++)
	{
	spider->GetSpiController()->SetAngle(i, 120, 1, false);
	}

	for (int i = 0; i < 20; i++)
	{
	printf("Press enter to change motor #%i", i);
	scanf("%*c");
	spider->GetSpiController()->SetAngle(i, 100, 1, true);
	}
	return 0;
	//*/

	printf("Press enter to start stretching the legs");
	scanf("%*c");
	//*
	spider->GetLeg(0)->SetAngles(0, 0, PI, true);
	spider->GetLeg(1)->SetAngles(0, 0, PI, true);
	spider->GetLeg(2)->SetAngles(0, 0, PI, true);
	spider->GetLeg(3)->SetAngles(0, 0, PI, true);
	spider->GetLeg(4)->SetAngles(0, 0, PI, true);
	spider->GetLeg(5)->SetAngles(0, 0, PI, true);
	//*/
	printf("Press enter to curl the toes back");
	scanf("%*c");
	//*
	spider->GetLeg(0)->SetAngles(0, PI / 8, PI / 2, false);
	spider->GetLeg(1)->SetAngles(0, PI / 8, PI / 2, false);
	spider->GetLeg(2)->SetAngles(0, PI / 8, PI / 2, false);
	spider->GetLeg(3)->SetAngles(0, PI / 8, PI / 2, false);
	spider->GetLeg(4)->SetAngles(0, PI / 8, PI / 2, false);
	spider->GetLeg(5)->SetAngles(0, PI / 8, PI / 2, true);
	//*/
	printf("Press enter to put the legs on the ground");
	scanf("%*c");
	//*
	spider->GetLeg(0)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(1)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(2)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(3)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(4)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(5)->SetAngles(0, 0, PI / 2, true);
	//*/

	VectorCommand relax = VectorCommand(0, Vector3(180, 0, 0));//note coordinate, not angles
	relax.Execute(spider);

	printf("Press enter to test move command");
	scanf("%*c");

	SpiderMoveCommand smc = SpiderMoveCommand(90 * G2R, 1);
	smc.Execute(spider);

	for (int i = 0; i < 6; i++)
	{
		printf("Press enter to put leg #%i in the air again", i);
		scanf("%*c");
		spider->GetLeg(i)->SetAngles(Vector3(180, 0, 0), true);
	}
}