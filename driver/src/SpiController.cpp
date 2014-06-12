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
	printf(TERM_RESET TERM_BOLD TERM_GREEN "SpiController>" TERM_RESET " INITIALIZING SPI");
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
void SpiController::Update()
{

}

float SpiController::GetAngle(int motor, LegConfig modifier, bool sync)
{
	return 0.0;
}


void SpiController::SetAngle(int motor, int byteAngle, int speed, bool sync)
{
	printf(TERM_RESET TERM_BOLD TERM_GREEN "SpiController>" TERM_RESET " Embedded command motor%i to %i @%i\n", motor, byteAngle, speed);
	char buffer[5] = { COMMAND_START, (int)motor, (int)byteAngle, (int)speed, COMMAND_ANGLE };
	bcm2835_spi_writenb(buffer, 5);
	if (sync)
	{
		printf(TERM_RESET TERM_BOLD TERM_GREEN "SpiController>" TERM_RESET " Synchronizing... \n");
		Synchronize(motor);
		printf("done\n");
	}
}

#define _BUFSIZE 25
void SpiController::Synchronize(int motor)
{
	int _array[] = { motor };
	Synchronize(_array, 1);
}

void SpiController::Synchronize(int motors[], int amount)
{
	while (true)
	{
		//Get the list of completed motors
		uint8_t received;
		uint8_t msg = COMMAND_START;
		uint8_t receivedbuffer[_BUFSIZE];
		uint8_t receivedcount = 0;
		bool enabled = false;
		for (int i = 0; i<_BUFSIZE; i++)
		{
			receivedbuffer[i] = 25;
		}
		//printf("Received:");
		while (true)
		{
			received = bcm2835_spi_transfer(msg);
			if (received == COMMAND_EOS || receivedcount >= _BUFSIZE) break;
			msg = COMMAND_PROGRESS;
			if (enabled)
			{
				receivedbuffer[receivedcount] = received;
				receivedcount++;
				//printf(" %i", received);
			}
			if (received == COMMAND_START_RESPONSE && msg == COMMAND_PROGRESS)
			{
				enabled = true;
			}
		}
		//printf("\n");
		//Check if the list contains the requested motor
		bool result = true;
		for (int i = 0; i < amount;i++)
		{
			//printf("Synced:");
			for (int j = 0; j<receivedcount; j++)
			{
				//printf(" %i", receivedbuffer[j]);
				if (receivedbuffer[j] == motors[i])
				{
					//printf("!");
					break;
				}
				if (receivedbuffer[j] == 25)
				{
					result = false;
					break;
				}
				//If we're at the end, and the above statements failed
				if (j == receivedcount - 1)
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
		if (result)
			return;
		//printf("Incomplete sync\n");
		//usleep(1000 * 500);
	}
}