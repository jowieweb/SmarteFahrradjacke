#ifndef MOTORCONTROLLER_H
#define MOTORCONTROLLER_H
#include "Arduino.h"
#include <queue>
class MotorController
{
  public:
    MotorController(int motorpin, int channel);
    void enqueue(bool fadein, byte dutyCycle, int lengthOnInMs, int lengthOffInMs);
    void spinMotor();
    void stopMotor();
    void loop();
    void spinIntro(boolean start);

  private:
    int pwmChannel;
    bool running = false;
    int motorpin;
    int dutyCycleToReach;
    int dutyCycle;
    int timeToStop;

    int freq = 5000;

    int usedChannel;
    int resolution = 8;

    typedef struct  {
      bool fadein;
      byte dutyCycle;
      int lengthOn;
    } spin_t;

    std::queue<spin_t> myqueue;

    void getFromQueue();


};
#endif
