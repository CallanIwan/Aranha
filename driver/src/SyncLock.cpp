#include "SyncLock.h"

#include "unistd.h"

SyncLock::SyncLock(int amount)
{
	lockLevel = amount;
}
SyncLock::~SyncLock()
{
	
}

void SyncLock::Lock()
{
	lockLevel++;
}
void SyncLock::Unlock()
{
	if (lockLevel > 0)
	{
		lockLevel--;
	}
	else
	{
		//In case the locklevel became negative
		lockLevel = 0;
	}
}
int SyncLock::GetLockLevel()
{
	return lockLevel;
}
void SyncLock::WaitForUnlock()
{
	while (GetLockLevel() > 0)
	{
		usleep(1000 * 20);
	};
}