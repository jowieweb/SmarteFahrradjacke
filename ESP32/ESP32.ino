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

void mpuCallback(String value){
  ble.sendText(value);
  Serial.println(value);
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
  Serial.begin(115200);
  pinMode(2,OUTPUT);
  mpu.init(false, &mpuCallback);
  mpu.createTask(stupid);
  
  mpu2.init(false, &mpuCallback);
  mpu2.createTask(stupid);
  mpu2.enabledOutputToCallback(true);
  ble.start(&bleCallback);
}

void loop()
{
  delay(1000);

}
