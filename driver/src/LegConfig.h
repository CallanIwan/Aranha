#ifndef HEADER_GUARD_LEGCONFIG
#define HEADER_GUARD_LEGCONFIG

#include "Globals.h"

class LegConfig
{
public:
	LegConfig();
	~LegConfig();
	//Allow fine tuning by code:
	//The length of the individual bones of the leg
	float bodyLength;
	float legLength;
	float toeLength;
	void SetLength(float body, float leg, float toe);
	//angle = calculatedAngle + offset;
	float bodyOffset;
	float legOffset;
	float toeOffset;
	void SetOffsets(float body, float leg, float toe);
	//Whether the leg angle has to be reversed after calculating the byte
	bool bodyReversed;
	bool legReversed;
	bool toeReversed;
	void SetReversed(bool body, bool leg, bool toe);
	//The indexes of the leg motors for the leg
	int bodyIndex;
	int legIndex;
	int toeIndex;
	void SetIndexes(int body, int leg, int toe);
};

#endif