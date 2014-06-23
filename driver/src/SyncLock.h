#ifndef HEADER_GUARD_SYNCLOCK
#define HEADER_GUARD_SYNCLOCK

#include "Globals.h"
#include <mutex>

class SyncLock
{
private:
	int origionalLockLevel;
	int lockLevel;
	bool completed;
	std::mutex mtx;
	bool IsCompleted();
public:
	SyncLock(int start);
	~SyncLock();
	//Increase the lock by a certain amount
	void Lock();
	//Decrease the lock by 1
	void Unlock();
	//Gets the locklevel of a syncgroup
	int GetLockLevel();
	//Block until the locklevel reaches 0
	void WaitForUnlock();
	//Resets the lock so it can be reused again
	void Reset();
};

#endif