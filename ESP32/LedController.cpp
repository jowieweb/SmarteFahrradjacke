#include "LedController.h"

/**
 * constructor for the class
 */
LEDController::LEDController(Pololu::APA102Base* strip) {
  this->strip = strip;

  /* create the predefined colors and led arrays */
  color_normal.red = 254;
  color_normal.green = 150;
  color_normal.blue = 8;
  color_normal.brightness = 10;

  off.red = 0;
  off.green = 0;
  off.blue = 0;
  off.brightness = 0;

  /* add both to the arrays */
  for (int i = 0; i < LEDCOUNT; i++) {
    colorOrange[i] = off;
    colorOff[i] = off;
  }
}

/**
 * loop function for the class 
 * has to be called constantly
 * may take some time to address all leds
 */
void LEDController::loop() {

  if (blinkActive) {
    if ( blinkStartTime + BLINKMSTIME <  millis()) {
      blinkActive = false;
    }

    if (blinkTimer + BLINKSPEED < millis()) {
      blinkTimer = millis();
      colorOrange[ledIndex] = color_normal;
      ledIndex++;
      strip->write(colorOrange, LEDCOUNT, 10);
      if (ledIndex == LEDCOUNT) {
        ledIndex = 0;
        for (int i = 0; i < LEDCOUNT; i++) {
          colorOrange[i] = off;
        }
      }

    }
  } else {
    strip->write(colorOff, LEDCOUNT, 10);
  }
}

boolean LEDController::isBlinking() {
  return this->blinkActive;
}


void LEDController::setBrightness(byte brightness) {
  this->brightness = brightness;
  color_normal.brightness = brightness;
}

void LEDController::startBreak() {
  // TODO!
}

void LEDController::stopBreak() {
   // TODO!
}

/** 
 *  enable the blinking 
 */
void LEDController::startBlink() {
  Serial.println("START BLINK");
  blinkActive = true;
  blinkStartTime = millis();
  blinkTimer = millis();
}

