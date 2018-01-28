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
  if(refreshTimer +5000< msTime){
    if(isBack){
     strip->write(colorOff, LEDCOUNT, 1);
     refreshTimer= msTime;    
    }
  }
  if(isBack){
    return;
  }
  
  if (breakStopTime < msTime) {
    update = true;
    breakStopTime = MAXTIME;
    isBreaking = false;
    off.red = 0;
    off.brightness = 0;
    for (int i = 0; i < LEDCOUNT; i++) {
      colorOff[i] = off;
    }
    Serial.println("end Break");
  }

  if (!update) {
    return;
  }




  if (blinkActive) {
    if ( blinkStartTime + BLINKMSTIME < msTime) {
      blinkActive = false;
      ledIndex = 0;
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
  update = true;
  isBreaking = true;

  breakStopTime = millis()+ 3500;
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
  breakStopTime = millis() + 250;
  

}

/**
    enable the blinking
*/
void LEDController::startBlink() {
  Serial.println("START BLINK");
  //check for double trigger 
  if(!blinkActive){
    blinkActive = true;
    blinkStartTime = millis();
    blinkTimer = millis();
    update = true;
    ledIndex = 0;
  }
}

void LEDController::setToBack(){
  isBack= true;
  off.red= BACKCOLORR;
  off.brightness = BACKCOLORBRIGHTNESS;
  for (int i = 0; i < 25; i++) {
      colorOff[i] = off;
  }
  off.red = BACKCOLORR;
  off.green = BACKCOLORR;
  off.blue = BACKCOLORR;
  for(int i = 25;i<LEDCOUNT;i++){
    colorOff[i] = off;
  }
  strip->write(colorOff, LEDCOUNT, 1);
  refreshTimer=millis();
  
  
}


