#include "MPUWrapper.h"
#include "BLEWrapper.h"

MPUWrapper mpu(0x69);
MPUWrapper mpu2(0x68);
BLEWrapper ble;

SemaphoreHandle_t i2cMutex;
SemaphoreHandle_t bleMutex;

long timer;
bool led = false;
void stupid(void * para) {
  MPUWrapper test = *((MPUWrapper*)para);
  while (true) {
    test.taskMPU();
  }
}

void mpuCallback(MPUValues value) {
  long tempTimer = millis();
  if (tempTimer > timer + 200) {

    led = !led;
    digitalWrite(LED_BUILTIN, led);
    timer = tempTimer;
    ble.sendText(value.text);
    Serial.println(value.text);
    if (value.yaw > 60 || value.yaw < -60) {
      Serial.println("ON");
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
  mpu.createTask(stupid);
  mpu.enabledOutputToCallback(true);

   mpu2.init(false, &mpuCallback, &i2cMutex);
   mpu2.createTask(stupid);
  mpu2.enabledOutputToCallback(true);
  ble.start(&bleCallback, &bleMutex);

  //vTaskStartScheduler();
}

void loop()
{
  vTaskDelay( 1000 );

}
