#include "MPUWrapper.h"
#include "BLEWrapper.h"

MPUWrapper mpu(0x69);
MPUWrapper mpu2(0x68);
BLEWrapper ble;

SemaphoreHandle_t i2cMutex;

void stupid(void * para) {
  MPUWrapper test = *((MPUWrapper*)para);
  while (true) {
    test.taskMPU();
     
  }
}

void mpuCallback(MPUValues value){
  ble.sendText(value.text);
  Serial.println(value.text);
  if(value.yaw > 60 || value.yaw < -60){
    Serial.println("ON");
  }
}

void bleCallback(String recv){
  Serial.println(recv);
  if(recv == "bv1"){
    digitalWrite(2,HIGH);
  } else if (recv == "bv0"){
    digitalWrite(2,LOW);
  }
}

void setup() {
  i2cMutex = xSemaphoreCreateMutex();
  Serial.begin(115200);
  pinMode(2,OUTPUT);
  mpu.init(false, &mpuCallback,i2cMutex);
  mpu.createTask(stupid);
  mpu.enabledOutputToCallback(true);
  
  mpu2.init(false, &mpuCallback, i2cMutex);
  mpu2.createTask(stupid);
  //mpu2.enabledOutputToCallback(true);
  ble.start(&bleCallback);

  //vTaskStartScheduler();
}

void loop()
{
   vTaskDelay( 1000 );

}
