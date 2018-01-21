#include "LedController.h"

/**
   constructor for the class
*/
LEDController::LEDController(Pololu::APA102Base* strip) {
  this->strip = strip;

  /* create the predefined colors and led arrays */
  color_normal.red = NORMALCOLORR;
  color_normal.green = NORMALCOLORG;
  color_normal.blue = NORMALCOLORB;
  color_normal.brightness = NORMALBRIGHTNESS;

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
   loop function for the class
   has to be called constantly
   may take some time to address all leds
*/
void LEDController::loop() {

  long msTime = millis();
  if (breakStopTime < msTime) {
    update = true;
    breakStopTime = MAXTIME;
    off.red = 0;
    off.brightness = 0;
    for (int i = 0; i < LEDCOUNT; i++) {
      colorOff[i] = off;
    }
  }

  if (!update) {
    return;
  }




  if (blinkActive) {
    if ( blinkStartTime + BLINKMSTIME < msTime) {
      blinkActive = false;
    }

    if (blinkTimer + BLINKSPEED < msTime) {
      blinkTimer = msTime;
      colorOrange[ledIndex] = color_normal;
      ledIndex++;
      strip->write(colorOrange, LEDCOUNT, 10);
      if (ledIndex == LEDCOUNT) {
        ledIndex = 0;
        for (int i = 0; i < LEDCOUNT; i++) {
          colorOrange[i] = off;
        }
        blinkTimer += BLINKENDONTIME;
      }
    }
  } else {
    strip->write(colorOff, LEDCOUNT, 10);
    update = false;
  }
}

boolean LEDController::isBlinking() {
  return this->blinkActive;
}


void LEDController::setBrightness(byte brightness) {
  this->brightness = brightness;
  color_normal.brightness = brightness;
  update = true;
}

void LEDController::startBreak() {
  if (isBreaking) {
    return;
  }
  update = true;
  isBreaking = true;


  off.red = 255;
  off.brightness = NORMALBRIGHTNESS;
  for (int i = 0; i < LEDCOUNT; i++) {
    colorOff[i] = off;
  }

}

void LEDController::stopBreak() {
  if (!isBreaking)
    return;

  update = true;
  isBreaking = false;
  breakStopTime = millis() + 1000;
  /*
    off.red = 0;
    off.brightness = 0;
    for (int i = 0; i < LEDCOUNT; i++) {
      colorOff[i] = off;
    } */
}

/**
    enable the blinking
*/
void LEDController::startBlink() {
  Serial.println("START BLINK");
  blinkActive = true;
  blinkStartTime = millis();
  blinkTimer = millis();
  update = true;
}

void LEDController::setToBack(){
  off.red= BACKCOLORR;
  off.brightness = BACKCOLORBRIGHTNESS;
  for (int i = 0; i < LEDCOUNT; i++) {
      colorOff[i] = off;
  }
  strip->write(colorOff, LEDCOUNT, 1);
  
}

