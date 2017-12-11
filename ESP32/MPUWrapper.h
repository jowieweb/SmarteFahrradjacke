#ifndef MPUWRAPPER_h
#define MPUWRAPPER_h
#include <Wire.h>
#include "MPU6050.h"

typedef struct MPUValues { 
    float pitch; 
    float roll; 
    float yaw;
    String text; 
} MPUValues;

class MPUWrapper
{
  public:
     

  
    MPUWrapper(int i2cAddress);
    void init(bool printToSerial, void (*)(MPUValues),SemaphoreHandle_t);
    void createTask(void (*func)(void*));
    void taskMPU();
    int getI2CAddress();
    void enabledOutputToCallback(boolean);
   
  private:
    MPU6050 mpu;
    SemaphoreHandle_t mutex;
    int i2cAddress = 0x68;
    boolean runi = false;
    float pitch = 0;
    float roll = 0;
    float yaw = 0;
    unsigned long timer = 0;
    float timeStep = 0.01;
    const TickType_t xDelay = 100 / portTICK_PERIOD_MS;
    boolean outputToSerial = true;
    boolean outputToCallback = false;
    void (*callback)(MPUValues);
    
   

};

#endif
