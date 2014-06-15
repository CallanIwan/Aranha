#ifndef HEADER_GUARD_SYNCCOMMAND
#define HEADER_GUARD_SYNCCOMMAND

#include "ISpiderCommand.h"
#include "Globals.h"
#include "Spider.h"
#include "SyncLock.h"

class SyncCommand : public ISpiderCommand
{
private:
	SyncLock* lock;
public:
	SyncCommand(SyncLock* lock);
	~SyncCommand();
	SyncLock* GetSyncLock();
	void Execute(Spider* spider);
};

#endif