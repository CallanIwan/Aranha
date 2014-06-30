#include "LegConfig.h"
LegConfig::LegConfig()
{
	bodyOffset = 0;
	bodyReversed = false;
	legOffset = 0;
	legReversed = false;
	toeOffset = 0;
	toeReversed = false;
}
LegConfig::~LegConfig()
{
}
void LegConfig::SetLength(float body, float leg, float toe)
{
	bodyLength = body;
	legLength = leg;
	toeLength = toe;
}
void LegConfig::SetOffsets(float body, float leg, float toe)
{
	bodyOffset = body;
	legOffset = leg;
	toeOffset = toe;
}
void LegConfig::SetReversed(bool body, bool leg, bool toe)
{
	bodyReversed = body;
	legReversed = leg;
	toeReversed = toe;
}
void LegConfig::SetIndexes(int body, int leg, int toe)
{
	bodyIndex = body;
	legIndex = leg;
	toeIndex = toe;
}