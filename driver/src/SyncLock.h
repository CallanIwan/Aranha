#ifndef HEADER_GUARD_SYNCLOCK
#define HEADER_GUARD_SYNCLOCK

#include "Globals.h"

class SyncLock
{
private:
	int lockLevel;
public:
	SyncLock(int start);
	~SyncLock();
	//Increase the lock by a certain amount
	virtual void Lock();
	//Decrease the lock by 1
	virtual void Unlock();
	//Gets the locklevel of a syncgroup
	virtual int GetLockLevel();
	//Block until the locklevel reaches 0
	virtual void WaitForUnlock();
};

#endif