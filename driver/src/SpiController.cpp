#include <bcm2835.h>
#include <stdio.h>

#include "SpiController.h"
#include "LegConfig.h"

#define COMMAND_START		200
#define COMMAND_SET			201
#define COMMAND_GET			202
#define COMMAND_PROGRESS	203
#define COMMAND_ANGLE		204
#define COMMAND_EOS			0xFA
#define COMMAND_NOP			0xFB

SpiController::SpiController()
{
	bcm2835_init();
	bcm2835_spi_begin();
	//Using default spi settings
	bcm2835_spi_setBitOrder(BCM2835_SPI_BIT_ORDER_MSBFIRST);
	bcm2835_spi_setDataMode(BCM2835_SPI_MODE0);
	bcm2835_spi_setClockDivider(BCM2835_SPI_CLOCK_DIVIDER_65536);
	bcm2835_spi_chipSelect(BCM2835_SPI_CS0);
	bcm2835_spi_setChipSelectPolarity(BCM2835_SPI_CS0, LOW);
}


SpiController::~SpiController()
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

#define _BUFSIZE 25
void autoComplete(uint8_t trigger)
{
	while (true)
	{
		uint8_t received;
		uint8_t msg = COMMAND_START;
		uint8_t receivedbuffer[_BUFSIZE];
		uint8_t receivedcount = 0;
		bool enabled = false;
		for (int i = 0; i<_BUFSIZE; i++)
		{
			receivedbuffer[i] = 25;
		}
		while (true)
		{
			received = 0;// bcm2835_spi_transfer(msg);
			if (received == COMMAND_EOS || receivedcount >= _BUFSIZE) break;
			if (msg == COMMAND_PROGRESS)
			{
				//Wait until the start command has been send, and the controller has returned a NOP over it
				enabled = true;
			}
			msg = COMMAND_PROGRESS;
			if (enabled)
			{
				receivedbuffer[receivedcount] = received;
				receivedcount++;
			}
			//printf("%02X ",received);
		}
		for (int i = 0; i<receivedcount; i++)
		{
			if (receivedbuffer[i] == trigger)
			{
				return;
			}
			if (receivedbuffer[i] == 25)
			{
				break;
			}
		}
	}
}

void SpiController::SetAngle(int motor, int byteAngle, int speed, bool sync)
{
	printf(TERM_RESET TERM_BOLD TERM_GREEN "SpiController>" TERM_RESET " Embedded command motor%i to %i @%i\n", motor, byteAngle, speed);
	char buffer[5] = { COMMAND_START, (char)motor, (char)byteAngle, (char) speed, COMMAND_ANGLE };
	//bcm2835_spi_writenb(buffer, 5);
	if (sync)
	{
		printf(TERM_RESET TERM_BOLD TERM_GREEN "SpiController>" TERM_RESET " Synchronizing... ");
		autoComplete(motor);
		printf("done\n");
	}
}

bool SpiController::IsDone(int motor, bool sync)
{
	return false;
}