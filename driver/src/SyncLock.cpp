#include "SyncLock.h"

#include <iostream>
#include <unistd.h>

SyncLock::SyncLock(int amount)
{
	lockLevel = amount;
	origionalLockLevel = amount;
	completed = false;
}
SyncLock::~SyncLock()
{
}

void SyncLock::Lock()
{
	mtx.lock();
	lockLevel++;
	mtx.unlock();
}
void SyncLock::Unlock()
{
	mtx.lock();
	if (lockLevel > 1)
	{
		lockLevel--;
	}
	else
	{
		//In case the locklevel became negative
		lockLevel = origionalLockLevel;
		completed = true;
	}
	std::cout << TERM_RESET << TERM_BOLD << TERM_CYAN << "SyncLock> " << TERM_RESET << "Unlocked, current lock is " << lockLevel << std::endl;
	mtx.unlock();
}
int SyncLock::GetLockLevel()
{
	mtx.lock();
	return lockLevel;
	mtx.unlock();
}

bool SyncLock::IsCompleted()
{
	mtx.lock();
	bool result = completed;
	mtx.unlock();
	return result;
}

void SyncLock::WaitForUnlock()
{
	Unlock();
	while (!IsCompleted())
	{
		usleep(1000 * 20);
	}
}

void SyncLock::Reset()
{
	mtx.lock();
	lockLevel = origionalLockLevel;
	completed = false;
	mtx.unlock();
}