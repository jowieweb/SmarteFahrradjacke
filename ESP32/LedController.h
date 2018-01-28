#ifndef LEDCONTROLLER_H
  #define LEDCONTROLLER_H
  #include <APA102.h>
  #define LEDCOUNT 72
  #define BLINKMSTIME 7000
  #define BLINKSPEED 3
  #define MAXTIME 999999999
  #define NORMALCOLORR 254
  #define NORMALCOLORG 150
  #define NORMALCOLORB 8
  #define NORMALBRIGHTNESS 10
  #define BLINKENDONTIME 500
  #define BACKCOLORR 127
  #define BACKCOLORBRIGHTNESS 1
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
      void setToBack();
    private:
      byte brightness;
      int ledCount=72;
      boolean blinkActive = false;
      boolean update = false;
      boolean isBack = false;
      int ledIndex = 0;
      byte blinkHighLed;
      long refreshTimer = 0;
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

