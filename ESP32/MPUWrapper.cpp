#include "MPUWrapper.h"

MPUWrapper::MPUWrapper(int i2cAddress) {
  this->i2cAddress = i2cAddress;
}
void MPUWrapper::init(bool printToSerial, void (*callback)(String)) {
  while (!mpu.begin(MPU6050_SCALE_2000DPS, MPU6050_RANGE_2G, i2cAddress))
  {
    Serial.print("Could not find a valid MPU6050 sensor at address");
    Serial.print(i2cAddress, HEX);
    Serial.println(", check wiring!");
    delay(500);
  }
   outputToSerial = printToSerial;
   mpu.calibrateGyro();
   mpu.setThreshold(3);
   this->callback = callback;

}


void MPUWrapper::createTask(void (*func)(void*)) {
  xTaskCreate(
    func,
    "taskMPU",
    10000,
    this,
    1,
    NULL);
}
int MPUWrapper::getI2CAddress() {
  return i2cAddress;
}

void MPUWrapper::enabledOutputToCallback(boolean enabled){
  outputToCallback = enabled;
}

void MPUWrapper::taskMPU() {
  timer = millis();
  Vector norm = mpu.readNormalizeGyro();

  // Calculate Pitch, Roll and Yaw
  pitch = pitch + norm.YAxis * timeStep;
  roll = roll + norm.XAxis * timeStep;
  yaw = yaw + norm.ZAxis * timeStep;
  String output = "p=";
  output+= pitch;
  output+=" r=";
  output+=roll;
  output+=" y=";
  output+=yaw;
 
  // Output raw
  if(outputToSerial){
   Serial.println(output);
    Serial.flush();
    if (yaw > 50 || yaw < -50) {
      runi = true;
    } else {
      runi = false;
    }
  }
  if(outputToCallback){
    callback(output);
  }

  
  //delay((timeStep*1000) - (millis() - timer));
  vTaskDelay( xDelay );
}


