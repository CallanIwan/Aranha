#include "SpiderLeg.h"

#include <iostream>
#include <unistd.h>
#include <math.h>

#include "Spider.h"
#include "Matrix.h"
#include "Vector3.h"
#include "LegConfig.h"

SpiderLeg::SpiderLeg()
{
}

SpiderLeg::SpiderLeg(Spider* master, Vector3 position, float rotation, LegConfig configuration)
{
	//Because we manipulate the origin raltive from the spiders' center,
	//we need the inverse of that manipulation to localize points by, and
	//the origional (double inverted) to globalize it
	this->modifier_translate = Matrix::CreateTranslation(position * -1);
	this->modifier_rotate = Matrix::CreateRotationY(rotation);
	this->modifier_inv_translate = Matrix::CreateTranslation(position);
	this->modifier_inv_rotate = Matrix::CreateRotationY(-rotation);
	this->config = configuration;
	this->master = master;
	this->currentBody = 100;
	this->currentLeg = 100;
	this->currentToe = 100;
}

SpiderLeg::~SpiderLeg()
{
}

#define SPEED_BODY 4
#define SPEED_LEG 2
#define SPEED_TOE 4
void SpiderLeg::SetAngles(float body, float leg, float toe, bool sync)
{
	int bytes[3];
	//Initial calculation
	bytes[0] = 100 + (body * (200 / PI));
	bytes[1] = 100 + (leg * (200 / PI));
	bytes[2] = (toe * (200 / PI));
	//Offsets
	bytes[0] += config.bodyOffset;
	bytes[1] += config.legOffset;
	bytes[2] += config.toeOffset;
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
	//Comparing last send bytes to calculate proper speeds
	float bodyDelta = bytes[0] - currentBody;
	bodyDelta = (bodyDelta < 0) ? -bodyDelta : bodyDelta;
	float legDelta = bytes[1] - currentLeg;
	legDelta = ((legDelta < 0) ? -legDelta : legDelta) * 2;
	float toeDelta = bytes[2] - currentToe;
	toeDelta = (toeDelta < 0) ? -toeDelta : toeDelta;
	//Calcuclate relative speeds
	float bodyFact = 1;
	float legFact = 1;
	float toeFact = 1;
	if (bodyDelta >= legDelta && bodyDelta >= toeDelta && bodyDelta != 0)
	{
		//std::cout << TERM_GREEN << "SpiderLeg>" << TERM_RESET << " Dominant body" << std::endl;
		bodyFact = 1.0;
		legFact = legDelta / bodyDelta;
		toeFact = toeDelta / bodyDelta;
	}
	else if (legDelta >= bodyDelta && legDelta >= toeDelta && legDelta != 0)
	{
		//std::cout << TERM_GREEN << "SpiderLeg>" << TERM_RESET << " Dominant leg" << std::endl;
		bodyFact = bodyDelta / legDelta;
		legFact = 1.0;
		toeFact = toeDelta / legDelta;
	}
	else if (toeDelta >= bodyDelta && toeDelta >= legDelta && toeDelta != 0)
	{
		//std::cout << TERM_GREEN << "SpiderLeg>" << TERM_RESET << " Dominant toe" << std::endl;
		bodyFact = bodyDelta / toeDelta;
		legFact = legDelta / toeDelta;
		toeFact = 1.0;
	}

	bodyFact *= SPEED_BODY;
	legFact *= SPEED_LEG;
	toeFact *= SPEED_TOE;
	//
	if (bodyFact < 1)
	{
		bodyFact = 1;
	}
	else if (bodyFact > 10)
	{
		bodyFact = 10;
	}
	//
	if (legFact < 1)
	{
		legFact = 1;
	}
	else if (legFact > 10)
	{
		legFact = 10;
	}
	//
	if (toeFact < 1)
	{
		toeFact = 1;
	}
	else if (toeFact > 10)
	{
		toeFact = 10;
	}

	//Sending bytes
	master->GetSpiController()->SetAngle(config.bodyIndex, bytes[0], (int)bodyFact, false);
	master->GetSpiController()->SetAngle(config.toeIndex, bytes[2], (int)toeFact, false);
	//Prioritize toe before leg
	master->GetSpiController()->SetAngle(config.legIndex, bytes[1], (int)legFact, false);
	currentBody = bytes[0];
	currentLeg = bytes[1];
	currentToe = bytes[2];
	if (sync)
	{
		int motors[3] = { config.bodyIndex, config.legIndex, config.toeIndex };
		master->GetSpiController()->Synchronize(motors, 3);
	}
}

bool CanReach(Vector3 target, float body, float leg, float toe)
{
	//CAN BE OPTIMIZED, SHOULD BE IMPROVED
	if (target.Length() > body + leg + toe)
	{
		return false;
	}
	if (target.x < 0)
	{
		return false;
	}
	else
	{
	}
	return true;
}

void SpiderLeg::SetAngles(Vector3 target, bool sync)
{
	printf(TERM_RESET);
	//Check if vector can be reached, NEEDS IMPROVEMENT
	if (target.Length() > config.bodyLength + config.legLength + config.toeLength)
	{
		std::cout << TERM_BOLD TERM_RED "SpiderLeg>" TERM_RESET " SetAngles(Vector3,bool) Error: target is out of range " << target.Length() << std::endl;
		return;
	}
	if (!CanReach(target, config.bodyLength, config.legLength, config.toeLength))
	{
		std::cout << TERM_BOLD TERM_RED "SpiderLeg>" TERM_RESET " SetAngles(Vector3,bool) Error: target cant be reached " << target.Length() << std::endl;
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

	if (isnan(innerLegAngle) || isnan(innerToeAngle) || isnan(groundAngle))
	{
		std::cout << TERM_RESET << TERM_BOLD << TERM_RED << "SpiderLeg>" << TERM_RESET << TERM_RED << " Warning: one of the angles turned NaN\n" << TERM_RESET;
		std::cout << "Leg Indexes: " << this->config.bodyIndex << " " << this->config.legIndex << " " << this->config.toeIndex << std::endl;
		std::cout << "Target location: " << std::endl;
		target.Print();
		std::cout << "Distance from origin:     " << target.Length() << std::endl;
		std::cout << "Distance from first bone: " << directLength << std::endl;
		exit(-1);
	}

	float legAngle = innerLegAngle + outerLegOffset;
	float toeAngle = innerToeAngle;

	SetAngles(bodyAngle, legAngle, toeAngle, sync);
	CurrentPosition = target;
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
	//First translate the point closer to the local space, then rotate it in the right direction
	worldVector = Vector3::Transform(worldVector, modifier_translate);
	return Vector3::Transform(worldVector, modifier_rotate);
}
Vector3 SpiderLeg::Globalize(Vector3 localVector)
{
	//First rotate the vector in the right direction, then shift it into place
	localVector = Vector3::Transform(localVector, modifier_inv_rotate);
	return Vector3::Transform(localVector, modifier_inv_translate);
}

void SpiderLeg::Print()
{
	printf("Rewrite this method\n");
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