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

void SyncLock::Lock(int lockID)
{
	if (lockID < 0 || lockID >= SYNCLOCK_CAPACITY)
		return;
	syncLocks[lockID]++;
}
void SyncLock::Unlock(int lockID)
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
int SyncLock::GetLockLevel(int lockID)
{
	if (lockID < 0 || lockID >= SYNCLOCK_CAPACITY)
		return SYNCLOCK_ERROR;
	return syncLocks[lockID];

}
void SyncLock::WaitForUnlock(int lockID)
{
	if (lockID < 0 || lockID >= SYNCLOCK_CAPACITY)
		return;
	while (syncLocks[lockID] > 0){};
}