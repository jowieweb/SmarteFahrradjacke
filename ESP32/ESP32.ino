
//#define SENDMPUTOBLE

#include <APA102.h>
#include "MPUWrapper.h"
#include "BLEWrapper.h"
#include "LEDController.h"
#include "MotorController.h"


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

#define BLEENABLETIME 2500

BLEWrapper *ble;

APA102<RIGHTLEDDATA, RIGHTLEDCLOCK> ledStrip;
APA102<LEFTLEDDATA, LEFTLEDCLOCK> ledStrip2;

MPUWrapper mpu(RIGHTMPUADDRESS);
MPUWrapper mpu2(LEFTMPUADDRESS);

LEDController *ledRight;
LEDController *ledLeft;

MotorController motorRight(RIGHTMOTORPIN);
MotorController motorLeft(LEFTMOTORPIN);


boolean BLEEnabled = false;
long lastButtonDown = 0;
long timer;
bool led = false;


/**
   callback for the mpus
   both, the left and the right mpu call this function, if yaw is greater than 60°
*/
void mpuCallback(MPUValues value) {
  bool triggered = value.triggered;
  bool turnRight = value.i2cAddress == 0x68;
  Serial.print("MPU Callback ");
  Serial.print(triggered);
  Serial.println(turnRight);

  if (triggered) {
    if (turnRight) {
      ledRight->startBlink();
      motorRight.enqueue(true, 255, 250, 0);
    }
    else {
      ledLeft->startBlink();
      motorLeft.enqueue(true, 255, 250, 0);
    }

  }
}

/**
   callback function for the BLE
   called, whem BLE received a message
*/
void bleCallback(String recv) {
  Serial.println(recv);
  DynamicJsonBuffer jsonBuffer;
  JsonObject& root = jsonBuffer.parseObject(recv);
  if (root.success()) {

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
    } else if (type == "light") {
      String lvl = root["light"];
      if (lvl == "low") {
        Serial.println("low brightness");
        ledRight->setBrightness(2);
        ledLeft->setBrightness(2);
      } else  if (lvl == "medium") {
        Serial.println("medium brightness");
        ledRight->setBrightness(6);
        ledLeft->setBrightness(6);
      } else if (lvl = "high") {
        Serial.println("high brightness");
        ledRight->setBrightness(12);
        ledLeft->setBrightness(12);
      }
    }
  } else {
    if (recv == "bv1") {
      motorLeft.spinMotor();
      motorRight.spinMotor();
    } else if (recv == "bv0") {
      motorLeft.stopMotor();
      motorRight.stopMotor();
    }
  }
}

/**
   main entrypoint for the application code
   called once at startup
*/
void setup() {
  delay(100);
  pinMode(LED_BUILTIN, OUTPUT);

  Serial.begin(115200);
  Serial.println("booted!");

  ledRight = new LEDController((Pololu::APA102Base*)&ledStrip);
  ledLeft = new LEDController((Pololu::APA102Base*)&ledStrip2);
    Serial.println("leds!");
  mpu.init(false, &mpuCallback);
  mpu.enabledOutputToCallback(true);
    Serial.println("mpu1!");

  mpu2.init(false, &mpuCallback);
  mpu2.enabledOutputToCallback(true);
    Serial.println("mpu2!");


  touchAttachInterrupt(TOUCHBUTTONPIN, ctxButtonDown, TOUCHBUTTONTHRESHOLD);
}


/**
   main loop
   called constantly
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

  ledRight->loop();
  ledLeft->loop();
  motorRight.loop();
  motorLeft.loop();
  heartbeat();

}

/**
   create 5hz a heartbeat on the build in LED
*/
void heartbeat() {
  long tempTimer = millis();
  if (tempTimer > timer + 200) {
    led = !led;
    digitalWrite(LED_BUILTIN, led);
    timer = tempTimer;
  }
}

/**
   callback for the TouchButton
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



