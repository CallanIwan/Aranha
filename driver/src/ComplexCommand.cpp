#include "ComplexCommand.h"

#include <iostream>
#include <iomanip>
#include <thread>
#include <queue>
#include <typeinfo>

ComplexCommand::ComplexCommand()
{

}


ComplexCommand::~ComplexCommand()
{
	for (auto& x : timelines)
	{
		for (int i = 0; i < x.second.size(); i++)
		{
			delete x.second[i];
		}
		x.second.clear();
	}
}

std::vector<ISpiderCommand*>* ComplexCommand::GetTimeline(int group)
{
	if (timelines.count(group) == 0)
	{
		timelines[group] = std::vector<ISpiderCommand*>();
	}
	return &timelines.at(group);
}

void ComplexCommand::ExecuteTimeline(Spider* spider, std::vector<ISpiderCommand*> timeline, int syncgroup)
{
	printf(TERM_RESET TERM_BOLD TERM_GREEN "ComplexCommand>" TERM_RESET " WorkerThread created: (%i)\n",syncgroup);
	for (int i = 0; i < timeline.size(); i++)
	{
		printf("(%i)Executing command ",syncgroup);
		timeline.at(i)->Execute(spider);
	}
}
void ComplexCommand::Execute(Spider* spider)
{
	printf(TERM_RESET TERM_BOLD TERM_GREEN "ComplexCommand>" TERM_RESET " Creating WorkerThreads\n");
	std::queue<std::thread> threads;
	int count = 0;
	for (auto rit = timelines.crbegin(); rit != timelines.crend(); ++rit)
	{
		threads.push(std::thread(&ComplexCommand::ExecuteTimeline, this, spider,rit->second, rit->first));
		count++;
	}
	printf(TERM_RESET TERM_BOLD TERM_GREEN "ComplexCommand>" TERM_RESET " Created %i Threads\n",count);
	//Remove all threads
	while (!threads.empty())
	{
		//Get thread and join it
		threads.front().join();
		count--;
		printf(TERM_RESET TERM_BOLD TERM_GREEN "ComplexCommand>" TERM_RESET " Remaining Threads: %i\n", count);
		//Thread is now killed, and we can remove it from the collection
		threads.pop();
	}
	printf(TERM_RESET TERM_BOLD TERM_GREEN "ComplexCommand>" TERM_RESET " All timelines have finished\n");
}

void ComplexCommand::Print()
{
	std::cout << "ComplexCommand Timelines: " << timelines.size() << "\n";
	for (auto rit = timelines.crbegin(); rit != timelines.crend(); ++rit)
	{
		std::cout << "Timeline: " << rit->first << " Commands: " << rit->second.size() << "\n";
	}
	std::cout << "End of Timeline Table\n" << std::flush;
}
