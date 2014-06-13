#include <bcm2835.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "SpiController.h"
#include "LegConfig.h"

#define COMMAND_START		200
#define COMMAND_SET			201
#define COMMAND_GET			202
#define COMMAND_PROGRESS	203
#define COMMAND_ANGLE		204
#define COMMAND_EOS			0xFA
#define COMMAND_NOP			0xFB
#define COMMAND_START_RESPONSE	160

SpiController::SpiController()
{
	lock = 0;
	if (!bcm2835_init())
	{
		printf(TERM_RESET TERM_BOLD TERM_RED"SpiController> ERROR: PERMISSION DENIED TO SPI DRIVER\n");
		exit(EXIT_FAILURE);
	}
}

SpiController::~SpiController()
{
}

void SpiController::Enable()
{
	printf(TERM_RESET TERM_BOLD TERM_GREEN "SpiController>" TERM_RESET " Initializing SPI\n");
	bcm2835_spi_begin();
	//Using default spi settings
	bcm2835_spi_setBitOrder(BCM2835_SPI_BIT_ORDER_MSBFIRST);
	bcm2835_spi_setDataMode(BCM2835_SPI_MODE0);
	bcm2835_spi_setClockDivider(BCM2835_SPI_CLOCK_DIVIDER_65536);
	bcm2835_spi_chipSelect(BCM2835_SPI_CS0);
	bcm2835_spi_setChipSelectPolarity(BCM2835_SPI_CS0, LOW);
}
void SpiController::Disable()
{
	bcm2835_spi_end();
	bcm2835_close();
}


float SpiController::GetAngle(int motor, LegConfig modifier, bool sync)
{
	printf("Error: SpiController::GetAngle() is not yet implemented\n");
	return 0.0;
}

void SpiController::SetAngle(int motor, int byteAngle, int speed, bool sync)
{
	char buffer[5] = { COMMAND_START, (int)motor, (int)byteAngle, (int)speed, COMMAND_ANGLE };
	GetLock();
	bcm2835_spi_writenb(buffer, 5);
	FreeLock();
	if (sync)
	{
		Synchronize(&motor,1);
	}
}

#define _BUFSIZE 20
int SpiController::GetCompleted(int* buffer)
{
	GetLock();
	uint8_t received;
	uint8_t msg = COMMAND_START;
	int receivedcount = 0;
	bool enabled = false;
	for (int i = 0; i < _BUFSIZE; i++)
	{
		buffer[i] = 25;
	}

	//Receive spi data
	while (true)
	{
		received = bcm2835_spi_transfer(msg);
		if (received == COMMAND_EOS || receivedcount >= _BUFSIZE) break;
		msg = COMMAND_PROGRESS;
		//Add received byte to buffer, after receiving a reply from the first command byte
		if (enabled)
		{
			buffer[receivedcount] = received;
			receivedcount++;
		}
		//The received byte is a response to the first byte we send
		if (received == COMMAND_START_RESPONSE)
		{
			enabled = true;
		}
	}
	FreeLock();
	return receivedcount;
}
bool SpiController::IsCompleted(int motors[], int amount)
{
	GetLock();
	//Get the list of completed motors
	int buffer[18] = { 25 };
	int received = GetCompleted(&buffer[0]);

	//Check if the list contains the requested motor
	bool result = true;
	for (int i = 0; i < amount; i++)
	{
		//printf("Synced:");
		for (int j = 0; j < received; j++)
		{
			if (buffer[j] == motors[i])
			{
				//printf(" %i", buffer[j]);
				break;
			}
			if (buffer[j] == 25)
			{
				result = false;
				break;
			}
			//If we're at the end, and the above statements failed
			if (j == received - 1)
			{
				result = false;
			}
		}
		//printf("\n");
		if (!result)
		{
			//printf("Failed to sync motor %i\n", motors[i]);
			break;
		}
	}
	FreeLock();
	return result;
}
void SpiController::Synchronize(int motors[], int amount)
{
	printf("Syncing for:");
	for (int i = 0; i < amount; i++)
	{
		printf(" %i", motors[i]);
	}
	printf("\n");

	uint16_t attempts = 1;
	while (!IsCompleted(motors,amount))
	{
		attempts++;
	}
	printf("Synced after %i attempts\n", attempts);
}