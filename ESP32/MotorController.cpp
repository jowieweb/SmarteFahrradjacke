#include "MotorController.h"



MotorController::MotorController(int motorpin) {
  this->motorpin = motorpin;
  usedChannel = this->pwmChannel;
  this->pwmChannel++;

  ledcSetup(usedChannel, freq, resolution);
  ledcAttachPin(motorpin, usedChannel);

}


void MotorController::enqueue(bool fadein, byte dutyCycle, int lengthOnInMs, int lengthOffInMs) {



  if(lengthOffInMs > 0){
    spin_t off;
    off.fadein= false;
    off.dutyCycle = 0;
    off.lengthOn = lengthOffInMs;
    myqueue.push(off);
  }
  
  spin_t obj;
  obj.fadein= fadein;
  obj.dutyCycle = dutyCycle;
  obj.lengthOn = lengthOnInMs;
  myqueue.push(obj);


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

void MotorController::getFromQueue(){
  spin_t obj = myqueue.front();
  myqueue.pop();
  

  this->timeToStop = millis() + obj.lengthOn;
  if (obj.fadein) {
    this->dutyCycle = 10;
  } else {
    this->dutyCycle = obj.dutyCycle;
  }
  this->dutyCycleToReach = obj.dutyCycle;
  this->running = true;
}

void MotorController::loop() {
  if (!running){
    if(!myqueue.empty()){
      getFromQueue();
    }
    return;  
  }
  //has to stop now
  if (timeToStop < millis()) {
    running = false;
    dutyCycle = 0;
    ledcWrite(usedChannel, dutyCycle);
  }

  //this might be to fast to notice
  if (dutyCycleToReach > dutyCycle) {
    dutyCycle++;
  }

  ledcWrite(usedChannel, dutyCycle);
}


