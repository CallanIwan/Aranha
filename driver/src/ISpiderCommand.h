#ifndef HEADER_GUARD_ISPIDERCOMMAND
#define HEADER_GUARD_ISPIDERCOMMAND

#include "Globals.h"

/**
Base class that defines the functions a spider command has to have to allow the spider to use the command object

*/
class ISpiderCommand
{
protected:
	ISpiderCommand();
public:
	/**
	Method that has to be implemented by subclasses
	*/
	~ISpiderCommand();
	virtual void execute(Spider& spider);
};

#endif