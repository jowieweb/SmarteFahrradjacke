
#define SENDMPUTOBLE

#include <APA102.h>
#include "MPUWrapper.h"
#include "BLEWrapper.h"
#include "LEDController.h"
#include "MotorController.h"
#include <Wire.h>


#include <ArduinoJson.h>


//#define AGGRESIVEOUTPUT 1
#define RIGHTLEDDATA 13
#define RIGHTLEDCLOCK 14
#define LEFTLEDDATA 23
#define LEFTLEDCLOCK 18
#define BACKLEDDATA 26
#define BACKLEDCLOCK 25
#define RIGHTMPUADDRESS 0x69
#define LEFTMPUADDRESS 0x68
#define RIGHTMOTORPIN 2
#define LEFTMOTORPIN 19
#define TOUCHBUTTONPIN 4
#define TOUCHBUTTONTHRESHOLD 10
#define TURNVIBRATIONLENGTH 250
#define TURNVIBRATIONSTRENGTH 255

#define BLEENABLETIME 2500

BLEWrapper *ble;

APA102<RIGHTLEDDATA, RIGHTLEDCLOCK> ledStripRIGHT;
APA102<LEFTLEDDATA, LEFTLEDCLOCK> ledStripLEFT;
APA102<BACKLEDDATA, BACKLEDCLOCK> ledStripBACK;

MPUWrapper mpuRight(RIGHTMPUADDRESS);
MPUWrapper mpuLeft(LEFTMPUADDRESS);

LEDController *ledRight;
LEDController *ledLeft;
LEDController *ledBack;

MotorController motorRight(RIGHTMOTORPIN,0);
MotorController motorLeft(LEFTMOTORPIN,1);


boolean BLEEnabled = false;
long lastButtonDown = 0;
long timer;
bool led = false;
int resetCount = 0;
bool btnTriggered = false;


/**
   callback for the mpus
   both, the left and the right mpu call this function, if yaw is greater than 60°
*/
void mpuCallback(MPUValues value) {
  bool triggered = value.triggered;
  bool turnRight = value.i2cAddress == RIGHTMPUADDRESS;
  Serial.print("MPU Callback ");
  Serial.print(triggered);
  Serial.println(turnRight);


  if (triggered) {
    if (turnRight) {
      if (mpuLeft.isNearTrigger()) {
        Serial.println("NEAR TRIGGER1!");
        return;
      }
      ledRight->startBlink();
      motorRight.enqueue(true, TURNVIBRATIONSTRENGTH, TURNVIBRATIONLENGTH, 0);
    }
    else {
      if (mpuRight.isNearTrigger()) {
        Serial.println("NEAR TRIGGER2!");
        return;
      }
      ledLeft->startBlink();
      motorLeft.enqueue(true, TURNVIBRATIONSTRENGTH, TURNVIBRATIONLENGTH, 0);
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
  /* check if valid json */
  if (root.success()) {

    String type = root["type"];
    if (type == "vibration") {
      /* vibration message found */
      String name = root["name"];
      Serial.println(name);
      JsonArray& requests = root["parts"];
      /* loop over all given vibration commands */
      for (auto& request : requests) {
        int on = request["on"];
        int off = request["off"];
        boolean fadein = request["fadeid"];
        int duty =  request["dutycycle"];
        motorRight.enqueue(fadein, duty, on, off);
        motorLeft.enqueue(fadein, duty, on, off);
        Serial.println(on);
      }
    } else if ( type == "turnleft") {
      /* turn left message found */
      motorLeft.enqueue(true, 255, 500, 0);
    } else if ( type == "turnright") {
      /* turn right message found */
      motorRight.enqueue(true, 255, 500, 0);
    } else if (type == "light") {
      /* adjust light message found */
      String lvl = root["light"];
      /* map the given command to a livelevel */
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
    /*debug commands */
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
  delay(1000);
  pinMode(LED_BUILTIN, OUTPUT);

  Serial.begin(115200);
  Serial.println("booted!");

  ledRight = new LEDController((Pololu::APA102Base*)&ledStripRIGHT);
  ledLeft = new LEDController((Pololu::APA102Base*)&ledStripLEFT);
  ledBack = new LEDController((Pololu::APA102Base*)&ledStripBACK);

  Serial.println("leds!");
  
  #ifdef AGGRESIVEOUTPUT
  mpuRight.init(true, &mpuCallback);
  #else
  mpuRight.init(false, &mpuCallback);
  #endif
  
  mpuRight.enabledOutputToCallback(true);
  Serial.println("mpu1!");

  #ifdef AGGRESIVEOUTPUT
    mpuLeft.init(true, &mpuCallback);
  #else
    mpuLeft.init(false, &mpuCallback);
  #endif
  mpuLeft.enabledOutputToCallback(true);
  Serial.println("mpu2!");
  Serial.flush();
  /* offset both MPU timers slightly */
  mpuRight.loop();
  mpuRight.loop();

  touchAttachInterrupt(TOUCHBUTTONPIN, ctxButtonDown, TOUCHBUTTONTHRESHOLD);
  delay(5);
  ledBack->setToBack();
}


/**
   main loop
   called constantly
*/
void loop()
{
  #ifdef AGGRESIVEOUTPUT
  long start = millis();
  #endif
  /* wait some time befor enabling BLE for improved system stability */
  if (!BLEEnabled) {
    if (millis() > BLEENABLETIME) {
      BLEEnabled = true;
      Serial.println("\n\n\nENABLEDBLE\n\n\n");
      ble = new BLEWrapper();
      ble->start(&bleCallback);
    }
  }

  /* get the data from both MPUs */
  boolean m1 = mpuRight.loop();
  boolean m2 = mpuLeft.loop();
  /* Only break, if both sensed a break */
  if (m1 && m2)
  {
    Serial.println("BREAK!");
    ledRight->startBreak();
    ledLeft->startBreak();   
  }
  else {
    //ledRight->stopBreak();
    //ledLeft->stopBreak();
  }
 
  /* loop over all instances */
  ledRight->loop();
  ledLeft->loop();
  ledBack->loop();
  motorRight.loop();
  motorLeft.loop();
  heartbeat();
  
  #ifdef AGGRESIVEOUTPUT
    Serial.print("loopTime: ");
    Serial.println(millis() - start);
  #endif

  if(btnTriggered){
    Serial.println("BTN DOWN");
    btnTriggered = false;
    if (BLEEnabled && ble){
      if(!(lastButtonDown + 500 > millis())){
        lastButtonDown = millis();
        ble->sendText("btn");
      }
    }    
  }

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
    resetCount ++;
    if (resetCount >= 10) {
      Wire.reset();
      resetCount = 0;
      Serial.println("wirereset");
    }
  }
}

/**
   callback for the TouchButton
*/
void ctxButtonDown() {
  //Serial.println("\n\nbutton down\n\n");
  //Serial.flush();
  btnTriggered = true;
  return;
 /*
  if (!BLEEnabled || !ble) { 
    return;
  }
  //debounce the button quite heavily by 500 ms 
  if (lastButtonDown + 500 > millis()) {
    return;
  }
  lastButtonDown = millis();
  ble->sendText("btn"); */

}


