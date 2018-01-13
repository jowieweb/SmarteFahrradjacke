#ifndef LEDCONTROLLER_H
  #define LEDCONTROLLER_H
  #include "Adafruit_WS2801.h"
  #include "Apa102c.hpp"
  #define LEDCOUNT 72
  #define BLINKMSTIME 7000
  #define BLINKSPEED 3
  
  class LEDController
  {
    public:
      LEDController(Apa102c &ledStrip) : ledStrip(ledStrip) {}
      void loop();
      void setBrightness(byte brightness);
      boolean isBlinking();
      void startBlink();
      void startBreak();
      void stopBreak();
    private:
      byte brightness;
      int ledCount=72;
      boolean blinkActive = false;
      int ledIndex = 0;
      byte blinkHighLed;
      long blinkTimer;
      long blinkStartTime;
      Apa102c &ledStrip;
  };
#endif
