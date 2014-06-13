#include "SpiderLeg.h"

#include <iostream>
#include <stdio.h>
#include <unistd.h>
#include <math.h>

#include "Spider.h"
#include "Matrix.h"
#include "Vector3.h"
#include "LegConfig.h"

SpiderLeg::SpiderLeg()
{

}

SpiderLeg::SpiderLeg(Spider* master, Matrix modifier, LegConfig config)
{
	//Because we manipulate the origin raltive from the spiders' center,
	//we need the inverse of that manipulation to localize points by, and
	//the origional (double inverted) to globalize it
	this->modifier = modifier.Inverse();
	this->modifier_inv = modifier;//double inverse = current
	this->config = config;
	this->master = master;
}

SpiderLeg::~SpiderLeg()
{

}

void SpiderLeg::SetAngles(float body, float leg, float toe, bool sync)
{
	int bytes[3];
	//Initial calculation
	bytes[0] = body * (200 / PI);
	bytes[1] = leg * (200 / PI);
	bytes[2] = toe * (200 / PI);
	for (int i = 0; i < 3; i++)
	{
		if (bytes[i] < 0)
			bytes[i] = 0;
		if (bytes[i] >= 200)
			bytes[i] = 199;
	}
	//Reverse flags
	if (config.bodyReversed)
		bytes[0] = 199 - bytes[0];
	if (config.legReversed)
		bytes[1] = 199 - bytes[1];
	if (config.toeReversed)
		bytes[2] = 199 - bytes[2];
	//Offsets
	bytes[0] += config.bodyOffset;
	bytes[1] += config.legOffset;
	bytes[2] += config.toeOffset;
	//Sending bytes
	master->GetSpiController()->SetAngle(config.bodyIndex, bytes[0], 1, false);
	master->GetSpiController()->SetAngle(config.legIndex, bytes[1], 1, false);
	master->GetSpiController()->SetAngle(config.toeIndex, bytes[2], 1, sync);
}
void SpiderLeg::SetAngles(Vector3 target, bool sync)
{
	printf(TERM_RESET);
	//Check if vector can be reached, NEEDS IMPROVEMENT
	if (target.Length() > config.bodyLength + config.legLength + config.toeLength)
	{
		printf(TERM_RESET TERM_BOLD TERM_GREEN "SpiderLeg>" TERM_RESET " SetAngles(Vector3,bool) Error: target target is out of range\n");
		return;
	}

	float bodyAngle = atan2(-target.z, target.x);

	float planeDist = sqrt(target.x * target.x + target.z * target.z) - config.bodyLength;
	float directLength = sqrt(planeDist * planeDist + target.y * target.y);
	float outerLegOffset = atan2(target.y, planeDist);
	float legSquared = config.legLength * config.legLength;
	float toeSquared = config.toeLength * config.toeLength;
	float directSquared = directLength * directLength;

	float innerLegAngle = acos(-(toeSquared - directSquared - legSquared) / (2.0 * directLength * config.legLength));
	float innerToeAngle = acos(-(directSquared - legSquared - toeSquared) / (2.0 * config.legLength * config.toeLength));
	float groundAngle = acos(-(legSquared - directSquared - toeSquared) / (2.0 * directLength * config.toeLength));

	float legAngle = innerLegAngle + outerLegOffset;
	float toeAngle = innerToeAngle;

	printf(TERM_BOLD TERM_GREEN "SpiderLeg>" TERM_RESET " SetAngles(Vector3,bool) ");
	if (sync)
	{
		printf(TERM_BOLD TERM_GREEN "Synchronized" TERM_RESET);
	}
	printf("\n");
	printf(TERM_BOLD TERM_GREEN "SpiderLeg>" TERM_RESET " Body angle: %5.4f %5.2f\n", bodyAngle, bodyAngle * (180 / PI));
	printf(TERM_BOLD TERM_GREEN "SpiderLeg>" TERM_RESET " Leg angle : %5.4f %5.2f\n", legAngle, legAngle * (180 / PI));
	printf(TERM_BOLD TERM_GREEN "SpiderLeg>" TERM_RESET " Toe angle : %5.4f %5.2f\n", toeAngle, toeAngle * (180 / PI));

	SetAngles(bodyAngle, legAngle, toeAngle, sync);

	//send angles
	//setAngles(body,leg,toe,sync)
}
void SpiderLeg::Synchronize()
{
	int motors[3];
	motors[0] = config.bodyIndex;
	motors[1] = config.legIndex;
	motors[2] = config.toeIndex;
	while (!master->GetSpiController()->IsCompleted(motors, 3))
	{
		usleep(1000 * 20);
	}
}
Vector3 SpiderLeg::Localize(Vector3 worldVector)
{
	return Vector3::Transform(worldVector, modifier);
}
Vector3 SpiderLeg::Globalize(Vector3 localVector)
{
	return Vector3::Transform(localVector, modifier_inv);
}

void SpiderLeg::Print()
{
	std::cout << "SpiderLeg:\n";
	std::cout << "Localizer:\n";
	modifier.Print();
	std::cout << "Globalizer:\n";
	modifier_inv.Print();
}

/** REVERSE KINEMATICS - www.driehoekberekenen.be/cosinusregel.jsp

printf("Testing inverse kinematics formula:\n");

printf("Enter DirectLength: ");
float directLength;
scanf("%f", &directLength);
float directSquared = directLength * directLength;
printf("Enter ToeLength: ");
float legLength;
scanf("%f", &legLength);
float legSquared = legLength * legLength;
printf("Enter ToeLength: ");
float toeLength;
scanf("%f", &toeLength);
float toeSquared = toeLength * toeLength;

float innerLegAngle = acos(-(toeSquared - directSquared - legSquared) / (2.0 * directLength * legLength)) * (180 / PI);
float innerToeAngle = acos(-(directSquared - legSquared - toeSquared) / (2.0 * legLength * toeLength)) * (180 / PI);
float groundAngle = acos(-(legSquared - directSquared - toeSquared) / (2.0 * directLength * toeLength)) * (180 / PI);

printf("Lengths:\n  Direct: %f\n  Leg: %f\n  Toe: %f\nAngles:\n  Inner Leg: %f\n  Inner Toe: %f\n  Ground: %f\n",
directLength,legLength,toeLength, innerLegAngle, innerToeAngle, groundAngle);

*/