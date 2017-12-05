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
  mpu.init(false);
  mpu.createTask(stupid);
  
  mpu2.init(false);
  mpu2.createTask(stupid);

  ble.start(&bleCallback);
}

void loop()
{
  ble.loop();
  delay(1000);

}
