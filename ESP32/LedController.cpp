#include "LedController.h"

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
      ledStrip.setPixel(ledIndex, 254, 150, 8, brightness);
      ledStrip.updateLeds();
      
      ledIndex++;
      if (ledIndex == LEDCOUNT) {
        ledIndex = 0;
        ledStrip.clear();
      }

    }
  } else {
    ledStrip.clear();
    ledStrip.updateLeds();
  }
}

boolean LEDController::isBlinking() {
  return this->blinkActive;
}


void LEDController::setBrightness(byte brightness) {
  this->brightness = brightness;
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

