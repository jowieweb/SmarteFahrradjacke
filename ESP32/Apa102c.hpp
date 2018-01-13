#ifndef _APA102C_HPP_
#define _APA102C_HPP_

#include <SPI.h>

#define SPI_CLOCK           5000000 // 5 MHz
#define DEFAULT_BRIGHTNESS  15

typedef struct {
	uint8_t brightness;
	uint8_t blue;
	uint8_t green;
	uint8_t red;
} pixel_t;

class Apa102c {
private:
	pixel_t *pixel;
	uint8_t numPixel;
	SPIClass &spiBus;

public:
  /*
   * Constructor.
   */
	Apa102c(uint8_t numLeds, SPIClass &spi) : numPixel(numLeds), spiBus(spi) {
		pixel = (pixel_t *) malloc(numPixel * sizeof(pixel_t));
		clear();
	}

  /*
   * Sends the values stored in the buffer to the leds.
   */
	void updateLeds();

  /*
   * Sets the color and brightness of a pixel in the pixel buffer. Don't forget to call updateLeds() after modifying to display the pixels.
   */
	void setPixel(uint8_t index, uint8_t red, uint8_t green, uint8_t blue, uint8_t brightness = DEFAULT_BRIGHTNESS);

  /*
   * Clears the pixel buffer and sets every pixel to #000000. 
   */
	void clear();
};

#endif
