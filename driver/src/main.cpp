#include <stdio.h>
#include <string.h>
#include <bcm2835.h>
#include <math.h>
#include <unistd.h>
#include <vector>
#include <iostream>
#include <termios.h>

#include "Globals.h"
#include "Spider.h"
#include "SpiderLeg.h"
#include "Vector3.h"
#include "ComplexCommand.h"
#include "SyncCommand.h"
#include "VectorCommand.h"
#include "MoveCommand.h"
#include "TurnCommand.h"
#include "StabalizeCommand.h"

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
void SetLeg(Spider* spider, int index, int motorbody, int motorleg, int motortoe, float bodyLength, float legLength, float toeLength, float bodyOffset, float legOffset, float toeOffset, bool bodyInv, bool legInv, bool toeInv, Vector3 position, float rotation, Vector3 neutralPosition)
{
	LegConfig config;
	config.SetIndexes(motorbody, motorleg, motortoe);
	config.SetLength(bodyLength, legLength, toeLength);
	config.SetOffsets(bodyOffset, legOffset, toeOffset);
	config.SetReversed(bodyInv, legInv, toeInv);
	SpiderLeg leg = SpiderLeg(spider, position, rotation, config);
	leg.NeutralPosition = neutralPosition;
	spider->SetLeg(index, leg);
}

int get_raw_char(void)
{
	int ch;
	struct termios oldt, newt;
	//Put terminal into raw mode
	tcgetattr(STDIN_FILENO, &oldt);
	newt = oldt;
	newt.c_lflag &= ~(ICANON | ECHO);
	tcsetattr(STDIN_FILENO, TCSANOW, &newt);
	//Get raw character
	ch = getchar();
	//Return terminal to normal
	tcsetattr(STDIN_FILENO, TCSANOW, &oldt);
	return ch;
}

void SpiderSetup(Spider* spider)
{
	//Tegen de klok in is een hogere graad
	//Leg: staat het aandrijf punt van de motor aan de linker kant, dan is deze geinverteerd
	float bl = 42.0;
	float ll = 58.2;
	float tl = 123.6;
	Vector3 neutral_c = Vector3(150, -70, 0);
	Vector3 neutral_m = Vector3(130, -70, 0);
	/* FRONT LEFT LEG / TESTING LEG */
	SetLeg(spider, 0,
		0, 1, 2,
		bl, ll, tl,
		-1, 0, -15,
		true, false, false,
		Vector3(82, 0, 50), 45 * G2R,
		neutral_c);
	/* FRONT RIGHT LEG */
	SetLeg(spider, 1,
		3, 4, 5,
		bl, ll, tl,
		-6, 0, -2,
		true, true, true,
		Vector3(82, 0, -50), -45 * G2R,
		neutral_c);
	/* MIDDLE LEFT LEG */
	SetLeg(spider, 2,
		6, 7, 15,
		bl, ll, tl,
		-2, 20, 0,
		true, false, true,
		Vector3(0, 0, 54), 90 * G2R,
		neutral_m);
	/* MIDDLE RIGHT LEG */
	SetLeg(spider, 3,
		14, 13, 12,
		bl, ll, tl,
		0, 5, -10,
		true, true, false,
		Vector3(0, 0, -54), -90 * G2R,
		neutral_m);
	/* BACK LEFT LEG */
	SetLeg(spider, 4,
		11, 10, 9,
		bl, ll, tl,
		0, 0, 10,
		true, false, true,
		Vector3(-82, 0, 50), 135 * G2R,
		neutral_c);
	/* BACK RIGHT LEG */
	SetLeg(spider, 5,
		8, 19, 18,
		bl, ll, tl,
		2, -5, -2,
		true, true, false,
		Vector3(-82, 0, -50), -135 * G2R,
		neutral_c);
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

	int motors[20] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };

	/*
	printf("Press enter to start stretching the legs");
	scanf("%*c");
	spider->GetLeg(0)->SetAngles(0, 0, PI, false);
	spider->GetLeg(1)->SetAngles(0, 0, PI, false);
	spider->GetLeg(2)->SetAngles(0, 0, PI, false);
	spider->GetLeg(3)->SetAngles(0, 0, PI, false);
	spider->GetLeg(4)->SetAngles(0, 0, PI, false);
	spider->GetLeg(5)->SetAngles(0, 0, PI, false);
	spider->GetSpiController()->Synchronize(motors, 20);
	//*/
	/*
	printf("Press enter to curl the toes back");
	scanf("%*c");
	spider->GetLeg(0)->SetAngles(0, PI / 2, PI / 6, false);
	spider->GetLeg(1)->SetAngles(0, PI / 2, PI / 6, false);
	spider->GetLeg(2)->SetAngles(0, PI / 2, PI / 6, false);
	spider->GetLeg(3)->SetAngles(0, PI / 2, PI / 6, false);
	spider->GetLeg(4)->SetAngles(0, PI / 2, PI / 6, false);
	spider->GetLeg(5)->SetAngles(0, PI / 2, PI / 6, false);
	spider->GetSpiController()->Synchronize(motors, 20);
	//*/
	/*
	printf("Press enter to put the legs on the ground");
	scanf("%*c");
	spider->GetLeg(0)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(1)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(2)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(3)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(4)->SetAngles(0, 0, PI / 2, false);
	spider->GetLeg(5)->SetAngles(0, 0, PI / 2, false);
	spider->GetSpiController()->Synchronize(motors, 20);
	//*/

	//*
	printf("Press enter to neutralize the legs");
	//scanf("%*c");
	spider->GetLeg(0)->SetAngles(spider->GetLeg(0)->NeutralPosition, false);
	spider->GetLeg(1)->SetAngles(spider->GetLeg(1)->NeutralPosition, false);
	spider->GetLeg(2)->SetAngles(spider->GetLeg(2)->NeutralPosition, false);
	spider->GetLeg(3)->SetAngles(spider->GetLeg(3)->NeutralPosition, false);
	spider->GetLeg(4)->SetAngles(spider->GetLeg(4)->NeutralPosition, false);
	spider->GetLeg(5)->SetAngles(spider->GetLeg(5)->NeutralPosition, false);
	spider->GetSpiController()->Synchronize(motors, 20);
	//*/

	/*
	VectorCommand relax = VectorCommand(0, Vector3(180, 0, 0));//note coordinate, not angles
	relax.Execute(spider);
	//*/

	/*
	bool point = false;
	Vector3 start = Vector3(160, -50, 40);
	Vector3 end = Vector3(180, 50, -40);
	while (true)
	{
	spider->GetLeg(3)->SetAngles((point) ? start : end, true);
	point = !point;
	}
	//*/
	/*
	printf("Press enter to execute stabalize command");
	scanf("%*c");

	spider->GetLeg(0)->SetAngles(spider->GetLeg(0)->CurrentPosition + (Vector3::Down() * 20), true);

	Vector3 destinations[6];
	for (int i = 0; i < 6; i++)
	{
	SpiderLeg* leg = spider->GetLeg(i);
	destinations[i] = leg->NeutralPosition + (Vector3::Forward() * 30);
	}
	StabalizeCommand sc = StabalizeCommand(destinations);
	sc.Execute(spider);

	//*/
	//*

	std::cout << "Press enter to start";
	scanf("%*c");

	//Create destinations for all the legs, where they lay outwards from their starting positions
	float distance = 160.0;
	Vector3 move_origins[6];
	for (int i = 0; i < 6; i++)
	{
		SpiderLeg* leg = spider->GetLeg(i);
		Vector3 origin = leg->Globalize(Vector3::Zero());
		if (i % 2 == 0)//0 2 4 = LEFT
		{
			origin += Vector3::Left() * distance;
		}
		else
		{
			origin += Vector3::Right() * distance;
		}
		origin += Vector3::Down() * 70;
		origin = leg->Localize(origin);
		std::cout << "Location of #" << i;
		origin.Print();
		move_origins[i] = origin;
	}

	Vector3 turn_origins[6];
	for (int i = 0; i < 6; i++)
	{
		turn_origins[i] = Vector3(150, -70, 0);
	}

	StabalizeCommand stabalize_turn = StabalizeCommand(turn_origins, false);
	StabalizeCommand stabalize_move = StabalizeCommand(move_origins, true);
	TurnCommand turn_clockwise = TurnCommand(spider, 15);
	TurnCommand turn_counterclockwise = TurnCommand(spider, -15);
	MoveCommand move_left = MoveCommand(spider, 90 * G2R, move_origins, 40, 2);
	MoveCommand move_right = MoveCommand(spider, -90 * G2R, move_origins, 40, 2);
	/*
	stabalize_move.Execute(spider);
	move_left.Execute(spider);
	move_left.Execute(spider);
	move_left.Execute(spider);
	stabalize_turn.Execute(spider);
	turn_clockwise.Execute(spider);
	turn_clockwise.Execute(spider);
	turn_clockwise.Execute(spider);
	move_left.Execute(spider);
	move_left.Execute(spider);
	move_left.Execute(spider);
	*/
	printf("Use the arrow keys to control the spider, pressing space will exit the program");
	int mode = 0;
	while (true)
	{
		//Read key
		int input = -1;
		//char inputchar = getchar();
		input = get_raw_char();
		//If key is space, exit
		if (input == 32)
		{
			return 0;
		}
		//If key is 0-9 proceed
		input -= 48;
		if (input == 8)
		{
			//Forwards
			if (mode != 1)
			{
				stabalize_move.Execute(spider);
			}
			move_left.Execute(spider);
			mode = 1;
		}
		if (input == 2)
		{
			//Backwards
			if (mode != 1)
			{
				stabalize_move.Execute(spider);
			}
			move_right.Execute(spider);
			mode = 1;
		}
		if (input == 4)
		{
			//Counter-clockwise
			if (mode != 2)
			{
				stabalize_turn.Execute(spider);
			}
			turn_counterclockwise.Execute(spider);
			mode = 2;
		}
		if (input == 6)
		{
			//Clockwise
			if (mode != 2)
			{
				stabalize_turn.Execute(spider);
			}
			turn_clockwise.Execute(spider);
			mode = 2;
		}
	}

	//*/
}