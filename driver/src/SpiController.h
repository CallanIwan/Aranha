#ifndef HEADER_GUARD_SPICONTROLLER
#define HEADER_GUARD_SPICONTROLLER

#include "Globals.h"

class SpiController
{
private:
	int configs[GLOBAL_MOTOR_COUNT][3];
	int completed[GLOBAL_MOTOR_COUNT];
public:
	SpiController();
	~SpiController();
	//Enables the SPI
	void Enable();
	//Closes the SPI
	void Disable();
	//Updates the cache of the class, and sends angle changes
	void Update();
	//Set the angle of a motor, changes will apply at the next Update() call
	//If sync is false the command is send to the microcontroller directly
	void SetAngle(int motor, int byteAngle, int speed, bool sync);
	//Get the angle of a motor from the cache of the last Update() call
	//if sync is false, the values are directly from the microcontroller directly
	float GetAngle(int motor, LegConfig modifier, bool sync);
	//Blocks until the selected motor is at its destination
	void Synchronize(int motor);
	//Same as above, but with multiple motors
	void Synchronize(int motor[], int amount);
};
#endif