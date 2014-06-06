#ifndef HEADER_GUARD_SPIDERLEG
#define HEADER_GUARD_SPIDERLEG

#include "Globals.h"

#include "Matrix.h"
#include "LegConfig.h"

class SpiderLeg
{
private:
	//The matrix that specifies the position of the origin of the leg in the world, and its rotation(usually only Yaxis rotation)
	Matrix modifier;
	LegConfig configuration;
	Spider* master;
public:
	//DO NOT USE THIS CONSTRUCTOR
	SpiderLeg();
	//USE THIS ONE
	SpiderLeg(Spider* master, Matrix modifier, LegConfig configuration);
	~SpiderLeg();

	/**
	This method sets the angels of the leg to match the specified angles
	When activating synchronized mode, the method will wait until the microcontroller has completed the PWM transition
	*/
	void setAngles(float body, float leg, float toe, bool sync);
	/**
	Sets the angles of the leg bones so the toe touches the given vector,
	vector is relative to the leg, use SpiderLeg::Localize to transform a
	vector relative to the spiders center to the context of the spiders leg
	*/
	void setAngles(Vector3 position, bool sync);
	/**
	Transforms a vector from the context of the spider to the individual leg
	*/
	Vector3 localize(Vector3 worldVector);
	/**
	Transforms a vector from the context of a spider leg to the (global) context of the spider
	*/
	Vector3 globalize(Vector3 localVector);
};

#endif