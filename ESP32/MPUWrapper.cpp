#include "MPUWrapper.h"

/**
   constructor for the class
*/
MPUWrapper::MPUWrapper(int i2cAddress) {
  this->i2cAddress = i2cAddress;
}

/**
    initiate the mpu
*/
void MPUWrapper::init(bool printToSerial, void (*callback)(MPUValues)) {

  while (!mpu.begin(MPU6050_SCALE_2000DPS, MPU6050_RANGE_2G, i2cAddress))
  {
    Serial.print("Could not find a valid MPU6050 sensor at address ");
    Serial.print(i2cAddress, HEX);
    Serial.println(", check wiring!");
    delay(500);
  }
  Serial.println("init done");
  outputToSerial = printToSerial;
  mpu.calibrateGyro();
  mpu.setThreshold(1);
  this->callback = callback;

}

int MPUWrapper::getI2CAddress() {
  return i2cAddress;
}

/**
   enable the output to the callback, if yaw is greater than 60°
*/
void MPUWrapper::enabledOutputToCallback(boolean enabled) {
  outputToCallback = enabled;
}


/**
   get the data from the mpu
   and set it to the attributes
*/
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

  if (runi != isTriggered) {
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

  //Vector accel =  mpu.readNormalizeAccel();

}

/**
   main loop of the class
   has to be called instantly
*/
boolean MPUWrapper::loop() {
  long tempTimer =  millis();

  if (tempTimer < timer) {
    return isBreaking;
  }

  getData();
  checkReCal();

  long timeNow = millis();
  timer = timeNow + 10; //(timeStep * 1000) + timeNow - (timeNow - tempTimer); //(timeStep * 1000) - (millis() - tempTimer);
  return getBreaking();
  //return false;
}


/**
   recalibrate the pitch, yaw and roll values
   only happens, if not much movement is deteceted over a periode of 10 sec.
*/
void MPUWrapper::checkReCal() {
  if (!(pitch + 5 > pitch_last && pitch - 5 < pitch_last)) {
    setto();
    return;
  }
  if (!(roll + 5 > roll_last && roll - 5 < roll_last)) {
    setto();
    return;
  }
  if (!(yaw + 5 > yaw_last && yaw - 5 < yaw_last)) {
    setto();
    return;
  }

  if (millis() > timeLastUpdate + RESETTIME) {
    Serial.println("\nRESET\n");
    pitch = 0.0;
    roll = 0.0;
    yaw = 0.0;
    pitch_last = 0.0;
    roll_last = 0.0;
    yaw_last = 0.0;
    timeLastUpdate = millis();
  }

}


/**
   get the acceleration data from the MPU
*/
boolean MPUWrapper::getBreaking() {
  Vector ret = mpu.readNormalizeAccel();

  
//  Vector ret = mpu.readRawAccel();
  int16_t value = ret.XAxis + ret.YAxis + ret.ZAxis;
  if (value > 20) {

    long t_now = millis();

    if (breakingStarted) {

      if (t_now > breakStartTime + BREAKTIGGERTIME) {
        isBreaking = true;
        return true;

      }
    } else {
      breakingStarted = true;
      breakStartTime = t_now;
      isBreaking = false;
    }
  } else {
    if (value < 15) {
      breakingStarted = false;
      breakStartTime = MAXTIME;
      isBreaking = false;
    }

  }

  return false; 
}



void MPUWrapper::setto() {
  timeLastUpdate = millis();
  pitch_last = pitch;
  roll_last = roll;
  yaw_last = yaw;
}


boolean MPUWrapper::isTriggerd() {
  return runi;
}

