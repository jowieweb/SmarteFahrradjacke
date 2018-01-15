#ifndef LEDCONTROLLER_H
  #define LEDCONTROLLER_H
  #include "Adafruit_WS2801.h"
  #include <APA102.h>
  #define LEDCOUNT 72
  #define BLINKMSTIME 7000
  #define BLINKSPEED 3
  #define MAXTIME 999999999
  
  class LEDController
  {
    public:
      LEDController(Pololu::APA102Base * strip);
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
      boolean update = false;
      int ledIndex = 0;
      byte blinkHighLed;
      long blinkTimer;
      long blinkStartTime;     
      long breakStopTime;
      boolean isBreaking = false;     
      Pololu::APA102Base* strip;
      Pololu::rgb_color colorOrange[72];
      Pololu::rgb_color colorOff[72];

      Pololu::rgb_color off;
      Pololu::rgb_color color_normal;
      Pololu::rgb_color color_high;
  };
#endif
