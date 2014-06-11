#include "SpiderLeg.h"


#include "Spider.h"
#include "Matrix.h"
#include "Vector3.h"
#include "LegConfig.h"

SpiderLeg::SpiderLeg()
{

}

SpiderLeg::SpiderLeg(Spider* master, Matrix modifier, LegConfig configuration)
{
	this->modifier = modifier;
	this->configuration = configuration;
	this->master = master;
}

SpiderLeg::~SpiderLeg()
{

}

void SpiderLeg::setAngles(float body, float leg, float toe, bool sync)
{
	int bytes[3];
	for (int i = 0; i < 3; i++)
	{
		bytes[0] = body / 180 * 200;
	}
	master->getSpiController().setAngle(0, bytes[0], sync);
	//void setAngle(int motor, int byteAngle, bool sync);
}
void SpiderLeg::setAngles(Vector3 position, bool sync)
{
	//Check if vector can be reached
	//calculate angles
	//send angles
	//setAngles(body,leg,toe,sync)
}
Vector3 SpiderLeg::localize(Vector3 worldVector)
{
	return Vector3::transform(worldVector, modifier);
}
Vector3 SpiderLeg::globalize(Vector3 localVector)
{
	return Vector3::transform(localVector, modifier * -1);
}