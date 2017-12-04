#include "MPUWrapper.h"
#include "BLEWrapper.h"

MPUWrapper mpu(0x69);
MPUWrapper mpu2(0x68);
BLEWrapper ble;

void stupid(void * para) {
  MPUWrapper test = *((MPUWrapper*)para);
  while (true) {
    test.taskMPU();
  }
}

void setup() {
  Serial.begin(115200);
  mpu.init();
  mpu.createTask(stupid);
  
  mpu2.init();
  mpu2.createTask(stupid);

  ble.start();
}

void loop()
{
  ble.loop();
  delay(1000);

}
