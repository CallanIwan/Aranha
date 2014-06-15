#ifndef HEADER_GUARD_COMPLEXCOMMAND
#define HEADER_GUARD_COMPLEXCOMMAND

#include "ISpiderCommand.h"

#include <map>
#include <vector>

#include "Globals.h"
#include "SyncLock.h"

class ComplexCommand : public ISpiderCommand
{
private:
	std::map<int, std::vector<ISpiderCommand*>> timelines;
	void ExecuteTimeline(Spider* spider, std::vector<ISpiderCommand*> timeline, int syncgroup);
public:
	ComplexCommand();
	~ComplexCommand();

	std::vector<ISpiderCommand*>* GetTimeline(int group);

	void Execute(Spider* spider);
	void Print();
};

#endif