#ifndef HEADER_GUARD_SPICONTROLLER
#define HEADER_GUARD_SPICONTROLLER

#include "Globals.h"
#include <thread>

#include "ThreadLock.h"

class SpiController : public ThreadLock
{
private:
	int lock;
	std::thread::id owner;
public:
	SpiController();
	~SpiController();
	//Enables the SPI
	void Enable();
	//Closes the SPI
	void Disable();
	//Set the angle of a motor, changes will apply at the next Update() call
	//If sync is false the command is send to the microcontroller directly
	void SetAngle(int motor, int byteAngle, int speed, bool sync);
	//Get the angle of a motor from the cache of the last Update() call
	//if sync is false, the values are directly from the microcontroller directly
	float GetAngle(int motor, LegConfig modifier, bool sync);
	//Gets the motors that are at their destination, buffer MUST BE 20 ELEMENTS LONG, used internally
	int GetCompleted(int* buffer);
	//Gets whether all the motors in the array are at their destination
	bool IsCompleted(int motor[], int amount);
	//Same as above, but with multiple motors
	void Synchronize(int motor[], int amount);
};
#endif