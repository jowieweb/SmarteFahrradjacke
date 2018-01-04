#include "LedController.h"

LEDController::LEDController(byte dataPin, byte clockPin, int ledCount) {
  this->dataPin = dataPin;
  this->clockPin = clockPin;
  this->ledCount = ledCount;
  strip = Adafruit_WS2801(ledCount, dataPin, clockPin);
  strip.begin();
  this->blinkColor = getColor(200, 0, 100,1);
  this->blinkHighColor = getColor(255, 0, 190,1);
}


void LEDController::loop(){
  if(isBlinking()){
    if(blinkTimer+125 < millis()){
      strip.setPixelColor(blinkHighLed, blinkColor);
      if(blinkRight){
        
      } else {
        
      }
      blinkHighLed= (blinkHighLed + blinkDirection) % ledCount;
      //check for negative -> flips to 255 so bigger
      if(blinkHighLed > ledCount)
      {
        blinkHighLed = ledCount-1;
      }
      strip.setPixelColor(blinkHighLed, blinkHighColor);
      strip.show();
      Serial.print("highLED: ");      
      Serial.println(blinkHighLed);
      blinkTimer = millis();
      
    }
  }
}

void LEDController::blink(boolean blinkRight, boolean enable){
  if(enable == blinkActive){
    return;
  }
  this->blinkRight = blinkRight;
  if(blinkRight){
    blinkDirection = 1;
  } else {
    blinkDirection = -1;
  }
  
  if(enable){
    blinkActive= true;
    this->blinkRight = blinkRight;
    setColor(blinkColor);
  } else{
    blinkActive= false;
  }
}

boolean LEDController::isBlinking(){
  return this->blinkActive;
}


void LEDController::setBrightness(byte brightness){
  this->brightness = brightness;
}

uint32_t LEDController::getColor(byte r, byte g, byte b, int brightness){
  //brightness = 1 + (brightness/10);
  r = r / brightness;
  g = g / brightness;
  b = b / brightness;
  uint32_t c;
  c = r;
  c <<= 8;
  c |= g;
  c <<= 8;
  c |= b;
  return c;
}

void LEDController::setColor(uint32_t c) {
  for (int i = 0; i < ledCount; i++) {
    strip.setPixelColor(i, c);
  }
  strip.show();
}

