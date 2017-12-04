#ifndef MPUWRAPPER_h
#define MPUWRAPPER_h
#include <Wire.h>
#include "MPU6050.h"

class MPUWrapper
{
  public:
    MPUWrapper(int i2cAddress);
    void init();
    void createTask(void (*func)(void*));
    void taskMPU();
    int getI2CAddress();
  private:
    MPU6050 mpu;
    int i2cAddress = 0x68;
    boolean runi = false;
    float pitch = 0;
    float roll = 0;
    float yaw = 0;
    unsigned long timer = 0;
    float timeStep = 0.01;
    const TickType_t xDelay = 100 / portTICK_PERIOD_MS;

    
   

};

#endif
