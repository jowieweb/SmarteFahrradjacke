#include "Apa102c.hpp"

void Apa102c::updateLeds() {
	spiBus.beginTransaction(SPISettings(SPI_CLOCK, SPI_MSBFIRST, SPI_MODE0));

	// start frame
	spiBus.transfer(0);
	spiBus.transfer(0);
	spiBus.transfer(0);
	spiBus.transfer(0);

	spiBus.writeBytes((uint8_t *) pixel, numPixel * sizeof(pixel_t));

	// end frame
	spiBus.transfer(0xFF);
	for (uint16_t i = 0; i < 5 + numPixel / 16; i++) {
		spiBus.transfer(0);
	}

	spiBus.endTransaction();
}

void Apa102c::setPixel(uint8_t index, uint8_t red, uint8_t green, uint8_t blue, uint8_t brightness) {
	pixel[index].brightness = 0b11100000 | brightness;
	pixel[index].blue = blue;
	pixel[index].green = green;
	pixel[index].red = red;
}

void Apa102c::clear() {
	for (uint16_t i = 0; i < numPixel; i++) {
		setPixel(i, 0, 0, 0, 0);
	}
}
