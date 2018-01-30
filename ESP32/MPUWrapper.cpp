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
void MPUWrapper::init(bool printToSerial, void (*callback)(MPUValues), bool middel) {


  int sda = 21;
  int scl = 22;
  this->middel = middel;
  if (middel) {
    sda = 33;
    scl = 27;
  }
  while (!mpu.begin(MPU6050_SCALE_2000DPS, MPU6050_RANGE_2G, i2cAddress, sda, scl))
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
  pitch = norm.YAxis * timeStep;
  roll = roll + norm.XAxis * timeStep;
  yaw = yaw + norm.ZAxis * timeStep;
  String output = "p=";
  output += pitch;
  output += " r=";
  output += roll;
  output += " y=";
  output += yaw;
  bool isTriggered = false;
  if (i2cAddress == 0x69) {
    isTriggered = yaw > TRIGGERVALUE;
  } else {
    isTriggered = yaw < -TRIGGERVALUE;
  }


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


}

/**
   main loop of the class
   has to be called repeatedly
*/
boolean MPUWrapper::loop() {
  long tempTimer =  millis();

  if (tempTimer < timer) {
    return isBreaking;
  }
  if (!middel) {
    getData();
  }
  checkReCal();

  long timeNow = millis();
  timer = timeNow + INTERVALTIME; //(timeStep * 1000) + timeNow - (timeNow - tempTimer); //(timeStep * 1000) - (millis() - tempTimer);
  if (!middel) {
    return false;
  }
  return getBreaking();
  //return false;
}


/**
   recalibrate the pitch, yaw and roll values
   only happens, if not much movement is deteceted over a periode of 10 sec.
*/
void MPUWrapper::checkReCal() {
  if (!(pitch + NOTMOVEDVALUE > pitch_last && pitch - NOTMOVEDVALUE < pitch_last)) {
    setto();
    return;
  }
  if (!(roll + NOTMOVEDVALUE > roll_last && roll - NOTMOVEDVALUE < roll_last)) {
    setto();
    return;
  }
  if (!(yaw + NOTMOVEDVALUE > yaw_last && yaw - NOTMOVEDVALUE < yaw_last)) {
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
  // Vector ret = mpu.readNormalizeAccel();
  

  bool retval = false;
  Vector ret = mpu.readNormalizeAccel();
  float value = ret.XAxis;//sqrt( (ret.XAxis * ret.XAxis) +  (ret.YAxis * ret.YAxis) +  (ret.ZAxis * ret.ZAxis) -96.2361);
  oldAccel.XAxis += ret.XAxis;
  oldAccel.YAxis += ret.YAxis;
  oldAccel.ZAxis += (ret.ZAxis+1);
  sampleCount++;

  if (sampleCount > 25) {
    oldAccel.XAxis /= sampleCount;
    oldAccel.YAxis /= sampleCount;
    oldAccel.ZAxis /= sampleCount;
    Serial.print("");
    Serial.print(  oldAccel.XAxis);
    Serial.print(",");
    Serial.print(  oldAccel.YAxis);
    Serial.print(",");
    Serial.print(  oldAccel.ZAxis);
    if(oldAccel.ZAxis< -2){
      Serial.print(",10");
      retval = true;
    }else {
       Serial.print(",0");
    }
    Serial.println();
    sampleCount = 1;
    oldAccel.XAxis = ret.XAxis;
    oldAccel.YAxis = ret.YAxis;
    oldAccel.ZAxis = ret.ZAxis;
  }









  return retval;
}



void MPUWrapper::setto() {
  timeLastUpdate = millis();
  pitch_last = pitch;
  roll_last = roll;
  yaw_last = yaw;
}

boolean MPUWrapper::isNearTrigger() {
    if (i2cAddress == 0x69) {
    return yaw > NEARTRIGGERVALUE;
  } else {
    return yaw < -NEARTRIGGERVALUE;
  }
  //return yaw > NEARTRIGGERVALUE || yaw < -NEARTRIGGERVALUE;
}

boolean MPUWrapper::isTriggerd() {
  return runi;
}

