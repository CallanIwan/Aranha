#ifndef HEADER_GUARD_ILEGCOMMAND
#define HEADER_GUARD_ILEGCOMMAND

#include "Globals.h"
/**
Defines an interface for commands that control individual legs, 
these are used by ISpiderCommand classes to control individual legs, or clusters of them
*/


class ILegCommand
{
protected:
	ILegCommand();
public:
	~ILegCommand();
	virtual void execute(Spider& spider);
};

#endif