#include "Globals.h"
#include "SpiController.h"
#include "LegConfig.h"

SpiController::SpiController()
{
}


SpiController::~SpiController()
{
}

void SpiController::update()
{

}

float SpiController::getAngle(int motor, LegConfig modifier, bool sync)
{
	return 0.0;
}

void SpiController::setAngle(int motor, int byteAngle, bool sync)
{

}

bool SpiController::isDone(int motor, bool sync)
{
	return false;
}