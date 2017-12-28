#include "MPUWrapper.h"

MPUWrapper::MPUWrapper(int i2cAddress) {
  this->i2cAddress = i2cAddress;


}
void MPUWrapper::init(bool printToSerial, void (*callback)(MPUValues), SemaphoreHandle_t* mutex) {
  this->mutex = *mutex;

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


void MPUWrapper::createTask(void (*func)(void*)) {
  #ifdef MULTITHREADING
  xTaskCreate(    func,    "taskMPU",    20000,    this,    i2cAddress - 0x66,    NULL);
  #endif

}
int MPUWrapper::getI2CAddress() {
  return i2cAddress;
}

void MPUWrapper::enabledOutputToCallback(boolean enabled) {
  outputToCallback = enabled;
}

void MPUWrapper::taskMPU() {
  timer = millis();
  if (timer < 2500) {
    vTaskDelay(100);
    return;
  }
  getData();  
  vTaskDelay((timeStep * 1000) - (millis() - timer));

}

void MPUWrapper::getData() {

#ifdef MULTITHREADING
  if(xSemaphoreTake(mutex, 250)  =! pdTRUE ){
    return;
  }
#endif
  Vector norm = mpu.readNormalizeGyro();

#ifdef MULTITHREADING
  xSemaphoreGive(mutex);
#endif
  
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

  if(yaw > 60 || yaw < -60){
    runi = true;
  } else {
    runi = false;
  }

  // Output raw
  if (outputToSerial) {
    Serial.println(output);
    Serial.flush();
  }
  if (outputToCallback) {
    MPUValues value;
    value.pitch = pitch;
    value.roll = roll;
    value.yaw = yaw;
    value.text = output;
    value.i2cAddress = i2cAddress;
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

