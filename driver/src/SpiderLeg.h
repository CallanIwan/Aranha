#ifndef HEADER_GUARD_SPIDERLEG
#define HEADER_GUARD_SPIDERLEG

#include "Globals.h"

#include "Matrix.h"
#include "Vector3.h"
#include "LegConfig.h"

class SpiderLeg
{
private:
	//The matrix that is able to manipulate global vectors into vectors relative to the
	Matrix modifier_translate;
	Matrix modifier_rotate;
	//The matrix that globalizes
	Matrix modifier_inv_translate;
	Matrix modifier_inv_rotate;
	Spider* master;
	int currentBody;
	int currentLeg;
	int currentToe;
public:
	LegConfig config;
	Vector3 CurrentPosition;
	Vector3 NeutralPosition;
	//DO NOT USE THIS CONSTRUCTOR
	SpiderLeg();
	//USE THIS ONE
	SpiderLeg(Spider* master, Vector3 position, float rotation, LegConfig configuration);
	~SpiderLeg();

	/**
	This method sets the angels of the leg to match the specified angles
	When activating synchronized mode, the method will wait until the microcontroller has completed the PWM transition
	*/
	void SetAngles(float body, float leg, float toe, bool sync);
	/**
	Sets the angles of the leg bones so the toe touches the given vector,
	vector is relative to the leg, use SpiderLeg::Localize to transform a
	vector relative to the spiders center to the context of the spiders leg
	*/
	void SetAngles(Vector3 position, bool sync);
	/**
	This method blocks until the leg has reached its destination
	*/
	void Synchronize();
	/**
	Transforms a vector from the context of the spider to the individual leg
	*/
	Vector3 Localize(Vector3 worldVector);
	/**
	Transforms a vector from the context of a spider leg to the (global) context of the spider
	*/
	Vector3 Globalize(Vector3 localVector);
	/**
	Print information about the instance to the console
	*/
	void Print();
};

#endif