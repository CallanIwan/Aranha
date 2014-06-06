#include "SyncLock.h"

SyncLock::SyncLock()
{
	for (int i = 0; i < SYNCLOCK_CAPACITY; i++)
	{
		syncLocks[i] = 0;
	}
}
SyncLock::~SyncLock()
{
	
}

void SyncLock::lock(int lockID)
{
	if (lockID < 0 || lockID >= SYNCLOCK_CAPACITY)
		return;
	syncLocks[lockID]++;
}
void SyncLock::unlock(int lockID)
{
	if (lockID < 0 || lockID >= SYNCLOCK_CAPACITY)
		return;
	if (syncLocks>0)
	{
		syncLocks[lockID]--;
	}
	else
	{
		syncLocks[lockID] = 0;
	}

}
int SyncLock::getLockLevel(int lockID)
{
	if (lockID < 0 || lockID >= SYNCLOCK_CAPACITY)
		return SYNCLOCK_ERROR;
	return syncLocks[lockID];

}
void SyncLock::waitForUnlock(int lockID)
{
	if (lockID < 0 || lockID >= SYNCLOCK_CAPACITY)
		return;
	while (syncLocks[lockID] > 0){};
}