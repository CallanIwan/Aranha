#include <stdio.h>
#include <string.h>
#include <bcm2835.h>
#include <math.h>
#include <unistd.h>
#include <vector>

#include "Globals.h"
#include "Spider.h"
#include "Vector3.h"
#include "ComplexCommand.h"
#include "SyncCommand.h"
#include "VectorCommand.h"

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

void SpiderSetup(Spider* spider)
{
	int moff = 1;
	LegConfig frontleft;
	config.SetIndexes(moff + 0, moff + 1, moff + 2);
	config.SetLength(1, 5, 5);
	config.SetOffsets(0, 0, 0);
	config.SetReversed(false, false, false);
	SpiderLeg leg = SpiderLeg(spider, Matrix::CreateTranslation(0, 10, 0) * Matrix::CreateRotationY((PI / 3) * i), config);
	spider->SetLeg(i, leg);
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


}