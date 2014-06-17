#include "SyncLock.h"

#include <iostream>
#include <unistd.h>

SyncLock::SyncLock(int amount)
{
	lockLevel = amount;
	origionalLockLevel = amount;
	completed = false;
	unlocks = 0;
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
	if (lockLevel > 0)
	{
		lockLevel--;
	}
	else
	{
		//In case the locklevel became negative
		lockLevel = 0;
		completed = true;
	}
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
	while (!IsCompleted())
	{
		usleep(1000 * 20);
	}
	std::cout << "SyncLock Solved" << std::endl;
	//Every thread that is being unlocked at this point will increase unlocks
	mtx.lock();
	unlocks++;
	//When the last thread increases unlocks,
	if (unlocks >= origionalLockLevel)
	{
		std::cout << "SyncLock Reset" << std::endl;
		completed = false;
		lockLevel = origionalLockLevel;
	}
	mtx.unlock();
}