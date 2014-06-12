#include <stdio.h>
#include <string.h>
#include <bcm2835.h>
#include <math.h>
#include <unistd.h>

#include "Globals.h"
#include "Spider.h"
#include "Vector3.h"
#include "LegPathCommand.h"
#include "LegSyncCommand.h"

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
	Spider spider;
	for (int i = 0; i < 6; i++)
	{
		LegConfig config;
		int moff = i * 3;
		config.SetIndexes(moff + 0, moff + 1, moff + 2);
		config.SetLength(1, 5, 5);
		config.SetOffsets(0, 0, 0);
		config.SetReversed(false, false, false);
		SpiderLeg leg = SpiderLeg(&spider, Matrix::CreateTranslation(0, 10, 0) * Matrix::CreateRotationY((PI / 3) * i), config);
		spider.SetLeg(i, leg);
	}
	spider.Print();

	spider.GetSpiController()->Enable();

	printf("Testing matrix modifications\n");

	Vector3 forward = Vector3::Forward();
	printf("Forward vector:\n");
	forward.Print();
	printf("\n");

	Matrix m1 = Matrix::CreateRotationY(PI / 2);
	Vector3 rotated = Vector3::Transform(forward, m1);
	printf("Quarter rotation on Y axis:\n");
	rotated.Print();
	Vector3 temp = Vector3::Left();
	printf("Default left vector:\n");
	temp.Print();
	printf("\n");

	Matrix m2 = Matrix::CreateRotationZ(PI / 2);
	rotated = Vector3::Transform(forward, m2);
	printf("Quarter rotation on Z axis:\n");
	rotated.Print();
	temp = Vector3::Up();
	printf("Default up vector:\n");
	temp.Print();
	printf("\n");
	
	printf("Testing leg path command:\n");

	LegPathCommand pathCommand = LegPathCommand(0);
	pathCommand.AddVector(Vector3(8, -1, 0));
	pathCommand.AddVector(Vector3(3, 0, 10));
	pathCommand.AddVector(Vector3(3, 0, -10));

	pathCommand.Execute(spider);
	for (int i = 0; i < 18; i++)
	{
		spider.GetSpiController()->SetAngle(i, 0, 199, false);
	}
	//Ensure that the motors have time to jump
	usleep(1000);
	//Create command before setting the motors
	int legs[6] = { 0, 1, 2, 3, 4, 5 };
	LegSyncCommand cmd = LegSyncCommand(legs, 6);

	for (int i = 0; i < 18; i++)
	{
		spider.GetSpiController()->SetAngle(i, 199, 1, false);
	}
	printf("All the legs are on their way\n");
	cmd.Execute(spider);
	printf("All legs are now synchronized\n");

	printf("\n");
	
	printf("Now we have done the basics, you can enter a leg number, and a vector to parse\n");
	while (true)
	{
		printf("Choose leg index: ");
		int leg = 0;
		scanf("%i", &leg);
		printf("You selected: %i\n", leg);
		SpiderLeg* userLeg = spider.GetLeg(leg);
		Vector3 vec = GetVector();
		vec.Print();
		Vector3 origin = userLeg->Globalize(Vector3::One());
		printf("Origin of leg: {%4.2f, %4.2f, %4.2f}\n",origin.x ,origin.y, origin.z);
		Vector3 local = userLeg->Localize(vec);
		printf("Localized vector: {%4.2f, %4.2f, %4.2f}\n", local.x, local.y, local.z);
	}
}