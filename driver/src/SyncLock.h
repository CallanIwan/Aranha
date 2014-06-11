#ifndef HEADER_GUARD_SYNCLOCK
#define HEADER_GUARD_SYNCLOCK

#include "Globals.h"

#define SYNCLOCK_ERROR		-2
#define SYNCLOCK_NONE		-1
#define SYNCLOCK_CAPACITY	16
#define SYNCLOCK_MAXIMUM	SYNCLOCK_CAPACITY - 1

class SyncLock
{
protected:
	int syncLocks[SYNCLOCK_CAPACITY];
public:
	SyncLock();
	~SyncLock();
	//Increase the lock of a syncgroup by 1
	virtual void lock(int lockID);
	//Decrease the lock of a syncgroup by 1
	virtual void unlock(int lockID);
	//Get the locklevel of a syncgroup
	virtual int getLockLevel(int lockID);
	//Block until the locklevel of a syncgroup 0 is
	virtual void waitForUnlock(int lockID);
};

#endif