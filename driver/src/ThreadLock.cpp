#include "ThreadLock.h"

#include "unistd.h"

#define SLEEPTIME	1000 * 5

ThreadLock::ThreadLock()
{
	//Prevent the value from being not zero when initiating the class, otherwise random values may be inside the memory
	level = 0;
}


ThreadLock::~ThreadLock()
{
}


void ThreadLock::GetLock()
{
	if (owner != std::this_thread::get_id())
	{
		while (level > 0)
		{
			usleep(SLEEPTIME);
		}
		owner = std::this_thread::get_id();
	}
	level++;
}
void ThreadLock::FreeLock()
{
	level--;
}