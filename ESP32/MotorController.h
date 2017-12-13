#ifndef MOTORCONTROLLER_H
#define MOTORCONTROLLER_H
#include "Arduino.h"
class MotorController
{
  public:
    MotorController(int motorpin);
    void spinMotor(bool fadein, byte dutyCycle, int lengthInMs);
    void spinMotor();
    void stopMotor();
    void loop();
    
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
    
};
#endif
