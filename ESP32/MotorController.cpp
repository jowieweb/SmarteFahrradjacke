#include "MotorController.h"



MotorController::MotorController(int motorpin) {
  this->motorpin = motorpin;
  usedChannel = this->pwmChannel;
  this->pwmChannel++;

  ledcSetup(usedChannel, freq, resolution);
  ledcAttachPin(motorpin, usedChannel);

}


void MotorController::spinMotor(bool fadein, byte dutyCycle, int lengthInMs) {
  this->timeToStop = millis() + lengthInMs;
  if (fadein) {
    this->dutyCycle = 10;
  } else {
    this->dutyCycle = dutyCycle;
  }
  this->dutyCycleToReach = dutyCycle;
  this->running = true;

}

void MotorController::spinMotor() {
  this->timeToStop = millis() + 100000;
  this->dutyCycle = 255;
  this->running = true;
}
void MotorController::stopMotor() {
  this->timeToStop = 0;
  this->dutyCycle = 0;
}

void MotorController::loop() {
  if (!running)
    return;

  //has to stop now
  if (timeToStop < millis()) {
    running = false;
    dutyCycle = 0;
    ledcWrite(usedChannel, dutyCycle);
    return;
  }

  //this might be to fast to notice
  if (dutyCycleToReach > dutyCycle) {
    dutyCycle++;
  }

  ledcWrite(usedChannel, dutyCycle);
}


