#ifndef HEADER_GUARD_LEGCONFIG
#define HEADER_GUARD_LEGCONFIG

#include "Globals.h"

class LegConfig
{
public:
	LegConfig();
	~LegConfig();
	//Allow fine tuning by code:
	//angle = calculatedAngle + offset;
	float bodyOffset;
	float legOffset;
	float toeOffset;
	//Whether the leg angle has to be reversed after calculating the byte
	bool bodyReversed;
	bool legReversed;
	bool toeReversed;
	//The indexes of the leg motors for the leg
	char bodyIndex;
	char legIndex;
	char toeIndex;
};

#endif