#ifndef LEDCONTROLLER_H
  #define LEDCONTROLLER_H
  #include "Adafruit_WS2801.h"
  
  class LEDController
  {
    public:
      LEDController(byte dataPin, byte clockPin, int ledCount);
      void loop();
      void setBrightness(byte brightness);
      void blink(boolean blinkRight, boolean enable);
      boolean isBlinking();
    private:
      byte dataPin;
      byte clockPin;
      byte brightness;
      int ledCount;
      boolean blinkActive;
      byte blinkHighLed;
      long blinkTimer;      
      uint32_t blinkColor;
      uint32_t blinkHighColor;
      uint32_t defaultColor;
      uint32_t stopColor;    
      Adafruit_WS2801 strip;
      boolean blinkRight;
      int blinkDirection = 0;
      
      uint32_t getColor(byte r, byte g, byte b, int brightness);
      void setColor(uint32_t c);  

  };
#endif
