#pragma once

#include "Globals.h"
#include <thread>

class ThreadLock
{
private:
	std::thread::id owner;
	int level;
public:
	ThreadLock();
	~ThreadLock();
	void GetLock();
	void FreeLock();
};

