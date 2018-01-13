
//#define SENDMPUTOBLE
#include "Apa102c.hpp"
#include "MPUWrapper.h"
#include "BLEWrapper.h"
#include "LedController.h"
#include "MotorController.h"

// Workaround for dev boards without led.
#ifndef LED_BUILTIN
  #define LED_BUILTIN 2
#endif

#include <ArduinoJson.h>

#define RIGHTLEDDATA 13
#define RIGHTLEDCLOCK 14
#define LEFTLEDDATA 23
#define LEFTLEDCLOCK 18
#define RIGHTMPUADDRESS 0x68
#define LEFTMPUADDRESS 0x69
#define RIGHTMOTORPIN 2
#define LEFTMOTORPIN 19
#define TOUCHBUTTONPIN 4
#define TOUCHBUTTONTHRESHOLD 10
#define LEDCOUNT  72

#define BLEENABLETIME 2500

BLEWrapper *ble;

// We need to instantiate another SPIClass because there is only one default instance (called SPI).
SPIClass SPI2;

Apa102c strip1(LEDCOUNT, SPI);
Apa102c strip2(LEDCOUNT, SPI2);
//APA102<RIGHTLEDDATA, RIGHTLEDCLOCK> ledStrip;
//APA102<LEFTLEDDATA, LEFTLEDCLOCK> ledStrip2;

MPUWrapper mpu(RIGHTMPUADDRESS);
MPUWrapper mpu2(LEFTMPUADDRESS);

LEDController ledRight(strip1);
LEDController ledLeft(strip2);

MotorController motorRight(RIGHTMOTORPIN);
MotorController motorLeft(LEFTMOTORPIN);


boolean BLEEnabled = false;
long lastButtonDown = 0;
long timer;
bool led = false;


/**
 * callback for the mpus 
 * both, the left and the right mpu call this function, if yaw is greater than 60°
 */
void mpuCallback(MPUValues value) {
  bool triggered = value.triggered;
  bool turnRight = value.i2cAddress == 0x68;
  Serial.print("MPU Callback ");
  Serial.print(triggered);
  Serial.println(turnRight);

  if (triggered) {
    if (turnRight){
      ledRight.startBlink();
       motorRight.enqueue(true, 255, 250, 0);
    }
    else{
      ledLeft.startBlink();
      motorLeft.enqueue(true, 255, 250, 0);
    }
      
  }
}

/**
 * callback function for the BLE
 * called, whem BLE received a message
 */
void bleCallback(String recv) {
  Serial.println(recv);
  DynamicJsonBuffer jsonBuffer;
  JsonObject& root = jsonBuffer.parseObject(recv);
  if (!root.success()) {
    Serial.println("\n\n\nJSON PARSE FAILED\n\n\n");
  } else {
    Serial.println("\n\n\nJSON PARSE OK\n\n\n");
  }
  String type = root["type"];
  if (type == "vibration") {

    String name = root["name"];
    Serial.println(name);
    JsonArray& requests = root["parts"];
    for (auto& request : requests) {
      int on = request["on"];
      int off = request["off"];
      boolean fadein = request["fadeid"];
      int duty =  request["dutycycle"];
      motorRight.enqueue(fadein, duty, on, off);
      motorLeft.enqueue(fadein, duty, on, off);
      //todo: do same stuff with 2 motor controller
      Serial.println(on);
    }
  } else if ( type == "turnleft") {
    motorLeft.enqueue(true, 255, 500, 0);
    //dostuff
  } else if ( type == "turnright") {
    //dostuff
    motorRight.enqueue(true, 255, 500, 0);
  }

  if (recv == "bv1") {
    motorLeft.spinMotor();
    motorRight.spinMotor();
  } else if (recv == "bv0") {
    motorLeft.stopMotor();
    motorRight.stopMotor();
  }

}

/**
 * main entrypoint for the application code
 * called once at startup
 */
void setup() {
  delay(100);
  pinMode(LED_BUILTIN, OUTPUT);

  SPI.begin();
  SPI2.begin(14, -1, 13);

  // Clear led strip
  strip1.updateLeds();
  strip2.updateLeds();

  Serial.begin(115200);
  Serial.println("booted!");

  mpu.init(false, &mpuCallback);
  mpu.enabledOutputToCallback(true);


  mpu2.init(false, &mpuCallback);
  mpu2.enabledOutputToCallback(true);


  touchAttachInterrupt(TOUCHBUTTONPIN, ctxButtonDown, TOUCHBUTTONTHRESHOLD);
}


/**
 * main loop
 * called constantly
 */
void loop()
{
  if (!BLEEnabled) {
    if (millis() > BLEENABLETIME) {
      BLEEnabled = true;
      Serial.println("\n\n\nENABLEDBLE\n\n\n");
      ble = new BLEWrapper();
      ble->start(&bleCallback);
    }
  }

  mpu.loop();
  mpu2.loop();

  ledRight.loop();
  ledLeft.loop();
  motorRight.loop();
  motorLeft.loop();
  heartbeat();

}

/**
 * create 5hz a heartbeat on the build in LED
 */
void heartbeat(){
    long tempTimer = millis();
  if (tempTimer > timer + 200) {
    led = !led;
    digitalWrite(LED_BUILTIN, led);
    timer = tempTimer;
  }
}

/**
 * callback for the TouchButton
 */
void ctxButtonDown() {
  Serial.println("\n\nbutton down\n\n");
  if (!BLEEnabled || !ble) {
    return;
  }
  if (lastButtonDown + 500 > millis()) {
    return;
  }

  lastButtonDown = millis();
  ble->sendText("btn");

}



