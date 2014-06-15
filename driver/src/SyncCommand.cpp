#include "SyncCommand.h"

#include <iostream>
#include <unistd.h>

SyncCommand::SyncCommand(SyncLock* synclock)
{
	this->lock = synclock;
}

SyncCommand::~SyncCommand()
{
}

SyncLock* SyncCommand::GetSyncLock()
{
	return lock;
}

void SyncCommand::Execute(Spider* spider)
{
	lock->Unlock();
	std::cout << TERM_RESET << TERM_BOLD << TERM_CYAN << "SyncCommand> " << TERM_RESET << "Waiting for other locks to sync\n";
	while (lock->GetLockLevel() > 0)
	{
		//Sleep so other threads can do some work(20ms)
		usleep(1000 * 20);
	}
	std::cout << TERM_RESET << TERM_BOLD << TERM_CYAN << "SyncCommand> " << TERM_RESET << "SyncLock solved!\n";
}