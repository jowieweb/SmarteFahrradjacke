#ifndef MPUWRAPPER_h
#define MPUWRAPPER_h
#include <Wire.h>
#include "MPU6050.h"
#define RESETTIME 10000
#define MAXTIME 999999999
#define BREAKTIGGERTIME 100
#define BREAKINGINTENSITY 20
#define BREAKINGSTOPINTENSITY 15
#define NEARTRIGGERVALUE 45
#define TRIGGERVALUE 60
#define INTERVALTIME 10
#define NOTMOVEDVALUE 5

typedef struct MPUValues {
  float pitch;
  float roll;
  float yaw;
  String text;
  int i2cAddress;
  bool triggered;
} MPUValues;

class MPUWrapper
{
  public:

    MPUWrapper(int i2cAddress);
    void init(bool printToSerial, void (*)(MPUValues));
    void createTask(void (*func)(void*));
    void taskMPU();
    int getI2CAddress();
    void enabledOutputToCallback(boolean);
    boolean loop();
    boolean isTriggerd();
    boolean getBreaking();
    boolean isNearTrigger();

  private:
    MPU6050 mpu;
    int i2cAddress = 0x68;
    boolean runi = false;
    float pitch = 0;
    float roll = 0;
    float yaw = 0;

    float pitch_last = 0;
    float roll_last = 0;
    float yaw_last = 0;
    unsigned long timeLastUpdate = 0;

    boolean isBreaking = false;
    boolean breakingStarted = false;
    long breakStartTime = 0;

    unsigned long timer = 0;
    float timeStep = 0.01;
    const TickType_t xDelay = 100 / portTICK_PERIOD_MS;
    boolean outputToSerial = true;
    boolean outputToCallback = false;
    void (*callback)(MPUValues);
    void getData();
    void checkReCal();
    void setto();

};

#endif
