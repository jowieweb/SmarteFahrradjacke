#include "LedController.h"

LEDController::LEDController(Pololu::APA102Base* strip) {
  this->strip = strip;
  color_normal.red = 254;
  color_normal.green = 150;
  color_normal.blue = 8;
  color_normal.brightness = 10;

  off.red = 0;
  off.green = 0;
  off.blue = 0;
  off.brightness = 0;
  for (int i = 0; i < 72; i++) {
    colorOrange[i] = off;
    colorOff[i] = off;
  }
  colorOrange[10].brightness = 31;
}


void LEDController::loop() {

  if (blinkActive) {
   // Serial.println("COLOR!");

    if ( blinkStartTime + 7000 <  millis()) {
      blinkActive = false;
    }

    if (blinkTimer + 3 < millis()) {
      blinkTimer = millis();
      colorOrange[ledIndex] = color_normal;
      ledIndex++;
      strip->write(colorOrange, 72, 10);
      if (ledIndex == 72) {
        ledIndex = 0;
        for (int i = 0; i < 72; i++) {
          colorOrange[i] = off;
        }
      }

    }
  } else {
    //Serial.println("NO COLOR!");
    strip->write(colorOff, 72, 10);
  }
}

boolean LEDController::isBlinking() {
  return this->blinkActive;
}


void LEDController::setBrightness(byte brightness) {
  this->brightness = brightness;
}

void LEDController::startBreak() {

}

void LEDController::stopBreak() {

}

void LEDController::startBlink() {
  Serial.println("START BLINK");
  blinkActive = true;
  blinkStartTime = millis();
  blinkTimer = millis();
}

