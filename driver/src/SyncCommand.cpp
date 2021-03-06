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
	std::cout << TERM_RESET << TERM_BOLD << TERM_CYAN << "SyncCommand> " << TERM_RESET << "Waiting for other locks to sync\n";
	lock->WaitForUnlock();
	std::cout << TERM_RESET << TERM_BOLD << TERM_CYAN << "SyncCommand> " << TERM_RESET << "SyncLock solved!\n";
}