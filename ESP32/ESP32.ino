
//#define SENDMPUTOBLE

#include "MPUWrapper.h"
#include "BLEWrapper.h"
#include "LEDController.h"
#include "MotorController.h"
#include <ArduinoJson.h>

#define dataPin 23
#define clockPin 18
#define LEDLEN 4
#define ENABLEDBLE 32
#define CTXBUTTON 25


MPUWrapper mpu(0x68);
MPUWrapper mpu2(0x69);
BLEWrapper *ble;
LEDController leds(dataPin, clockPin, LEDLEN);
MotorController motor(2);


boolean BLEEnabled = false;
long lastButtonDown = 0;
long timer;
bool led = false;


void mpuCallback(MPUValues value) {
  bool triggered = value.triggered;
  bool turnRight = value.i2cAddress == 0x68;
  Serial.print("MPU Callback ");
  Serial.print(triggered);
  Serial.println(turnRight);
  
  leds.blink(turnRight, triggered);
  motor.enqueue(true, 255, 250,0);

}

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
  if(type == "vibration"){
      
    String name = root["name"];
    Serial.println(name);
    JsonArray& requests = root["parts"];
    for (auto& request : requests) {
      int on = request["on"];
      int off = request["off"];
      boolean fadein = request["fadeid"];
      int duty =  request["dutycycle"];
      motor.enqueue(fadein,duty,on,off);
      Serial.println(on);
    }
  }
  
  if (recv == "bv1") {
    motor.spinMotor();
  } else if (recv == "bv0") {
    motor.stopMotor();
  } else if(recv.indexOf("turn: ") >=0){
    //turn message
    if(recv.indexOf("turn: left") >= 0){
      //turn left
      
    } else if (recv.indexOf("turn: right") >= 0){
      //turn right
      
    }
  }
}


void setup() {
  pinMode(LED_BUILTIN, OUTPUT);

  Serial.begin(115200);

  mpu.init(false, &mpuCallback);
  mpu.enabledOutputToCallback(true);


  mpu2.init(false, &mpuCallback);
  mpu2.enabledOutputToCallback(true);



  pinMode(ENABLEDBLE, INPUT_PULLUP);
  pinMode(CTXBUTTON, INPUT_PULLUP);

  attachInterrupt(digitalPinToInterrupt(CTXBUTTON), ctxButtonDown, FALLING);
}



void loop()
{
  if (!BLEEnabled) {
    if (!digitalRead(ENABLEDBLE)) {
      BLEEnabled = true;
      Serial.println("\n\n\nENABLEDBLE\n\n\n");
      ble = new BLEWrapper();
      ble->start(&bleCallback);
    }
  }
  
  mpu.loop();
  mpu2.loop();

  leds.loop();
  motor.loop();
  
  long tempTimer = millis();
  if (tempTimer > timer + 200) {
    led = !led;
    digitalWrite(LED_BUILTIN, led);
    timer = tempTimer;
  }

}

void ctxButtonDown(){
  if(!BLEEnabled || !ble){
    return;
  } 
  if(lastButtonDown +500 > millis()){
    return;
  }
  Serial.println("\n\nbutton down\n\n");
  lastButtonDown = millis();
  ble->sendText("btn");
  
}



