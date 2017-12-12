//#define MULTITHREADING

#include "MPUWrapper.h"
#include "BLEWrapper.h"
#include "LEDController.h"

#define dataPin 23
#define clockPin 18
#define LEDLEN 4


MPUWrapper mpu(0x68);
MPUWrapper mpu2(0x69);
BLEWrapper ble;
LEDController leds(dataPin, clockPin, LEDLEN);

SemaphoreHandle_t i2cMutex;
SemaphoreHandle_t bleMutex;

#ifdef MULTITHREADING
void stupid(void * para) {
  MPUWrapper test = *((MPUWrapper*)para);
  while (true) {
    test.taskMPU();
  }
}
#endif


long timer;
bool led = false;


void mpuCallback(MPUValues value) {
  long tempTimer = millis();
  if (tempTimer > timer + 200) {

    led = !led;
    digitalWrite(LED_BUILTIN, led);
    timer = tempTimer;
    Serial.print(value.i2cAddress);
    Serial.println(value.text);

    ble.sendText(value.text);
    if (value.yaw > 60 || value.yaw < -60) {
      if (value.i2cAddress == 0x68) {
        leds.blink(true, true);
      }
      else {
        leds.blink(false, true);
      }
      Serial.println("ON");

    } else {
      if (value.i2cAddress == 0x68) {
        //is mpu
        if (!mpu2.isTriggerd()) {
          leds.blink(true, false);
        }
      } else {
        //is mpu2
        if (!mpu.isTriggerd()) {
          leds.blink(true, false);
        }
      }

    }


  }

}

void bleCallback(String recv) {
  //Serial.println(recv);
  if (recv == "bv1") {
    digitalWrite(2, HIGH);
  } else if (recv == "bv0") {
    digitalWrite(2, LOW);
  }
}

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);

  i2cMutex = xSemaphoreCreateMutex();
  bleMutex = xSemaphoreCreateMutex();
  Serial.begin(115200);
  pinMode(2, OUTPUT);

  mpu.init(false, &mpuCallback, &i2cMutex);
  mpu.enabledOutputToCallback(true);


  mpu2.init(true, &mpuCallback, &i2cMutex);
  mpu2.enabledOutputToCallback(true);

#ifdef MULTITHREADING
  Serial.println("!!!!!MULTITHREADING!!!!");
  mpu.createTask(stupid);
  mpu2.createTask(stupid);
#endif

  ble.start(&bleCallback, &bleMutex);

}


long blinkitimer = 0;


void loop()
{

#ifndef MULTITHREADING
  mpu.loop();
  mpu2.loop();
#endif

  leds.loop();

}




