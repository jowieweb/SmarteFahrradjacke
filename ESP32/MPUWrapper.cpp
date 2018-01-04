#include "MPUWrapper.h"

MPUWrapper::MPUWrapper(int i2cAddress) {
  this->i2cAddress = i2cAddress;


}
void MPUWrapper::init(bool printToSerial, void (*callback)(MPUValues)) {

  while (!mpu.begin(MPU6050_SCALE_2000DPS, MPU6050_RANGE_2G, i2cAddress))
  {
    Serial.print("Could not find a valid MPU6050 sensor at address");
    Serial.print(i2cAddress, HEX);
    Serial.println(", check wiring!");
    delay(500);
  }
  outputToSerial = printToSerial;
  mpu.calibrateGyro();
  mpu.setThreshold(1);
  this->callback = callback;

}

int MPUWrapper::getI2CAddress() {
  return i2cAddress;
}

void MPUWrapper::enabledOutputToCallback(boolean enabled) {
  outputToCallback = enabled;
}


void MPUWrapper::getData() {

  Vector norm = mpu.readNormalizeGyro();
 
  // Calculate Pitch, Roll and Yaw
  pitch = pitch + norm.YAxis * timeStep;
  roll = roll + norm.XAxis * timeStep;
  yaw = yaw + norm.ZAxis * timeStep;
  String output = "p=";
  output += pitch;
  output += " r=";
  output += roll;
  output += " y=";
  output += yaw;
  bool isTriggered = yaw > 60 || yaw < -60;


  // Output raw
  if (outputToSerial) {
    Serial.println(output);
    Serial.flush();
  }
  
  if(runi != isTriggered){
    runi = isTriggered;
    MPUValues value;
    value.pitch = pitch;
    value.roll = roll;
    value.yaw = yaw;
    value.text = output;
    value.i2cAddress = i2cAddress;
    value.triggered = runi;
    callback(value);
  }
}

void MPUWrapper::loop() {
  long tempTimer =  millis();

  if(tempTimer < timer){
    return;
  }

  getData();
  long timeNow = millis();
  timer = (timeStep * 1000) + timeNow - (timeNow - tempTimer); //(timeStep * 1000) - (millis() - tempTimer);
}

boolean MPUWrapper::isTriggerd(){
  return runi;
}

Vector MPUWrapper::getAccel(){
  Vector ret = mpu.readNormalizeAccel();
  
  Serial.print("\n\n\n");
  Serial.print(ret.XAxis);
  Serial.print("\t");
  Serial.print(ret.YAxis);
  Serial.print("\t");  
  Serial.print(ret.ZAxis);
  Serial.print("\t");
}

