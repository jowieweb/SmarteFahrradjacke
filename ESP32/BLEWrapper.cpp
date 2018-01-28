#include "Arduino.h"
#include "BLEWrapper.h"



/**
 * called when BLE connection is established
 */
void BLEWrapper::onConnect(BLEServer* pServer) {
  deviceConnected = true;
  Serial.println("DEV CONNECTED");
  //give the connection some time to settle in
  allowedToSend = millis() + 1000;
};

/**
 * called when BLE connection is closed
 */
void BLEWrapper::onDisconnect(BLEServer* pServer) {
  deviceConnected = false;
  Serial.println("DEV DISCONNECTED");
}



/**
 * start the BLE service
 */
void BLEWrapper::start(void (*callback)(String)) {

  
  delay(10);
  BLEDevice::init("SmartJacket");
  this->callback = callback;


  BLEDescriptor pFormat(BLEUUID((uint16_t)0x2904));
  BLEDescriptor charConf(BLEUUID((uint16_t)0x2902));
  pFormat.setValue("Percent from 0 to 100");
  charConf.setValue("kp");
  
  // Create the BLE Server
  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(this);

  // Create the BLE Service
  BLEService *pService = pServer->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  pCharacteristic = pService->createCharacteristic(CHARACTERISTIC_UUID_TX,BLECharacteristic::PROPERTY_NOTIFY);
  pCharacteristic->addDescriptor(new BLE2902());
  BLECharacteristic *pCharacteristic = pService->createCharacteristic(CHARACTERISTIC_UUID_RX,BLECharacteristic::PROPERTY_WRITE);
  pCharacteristic->setCallbacks(this);

  BLEService *pServiceBattery = pServer->createService(BATTERYSERVICE);
  BLECharacteristic *pCharacteristicPower = pServiceBattery->createCharacteristic(CHARACTERISTIC_BATTERY_LEVEL,BLECharacteristic::PROPERTY_READ);
  pCharacteristicPower->setCallbacks(this);

   pCharacteristicPower->addDescriptor(&pFormat);
   pCharacteristicPower->addDescriptor(&charConf);
   pCharacteristicPower->addDescriptor(new BLE2902());
  //pServiceBattery->addCharacteristic(pCharacteristicPower);
  //L in asii = 75 -> 75 % Battery
  pCharacteristicPower->setValue("K");

  pServiceBattery->start();
  // Start the service
  pService->start();

  // Start advertising
  pServer->getAdvertising()->start();
}

void BLEWrapper::onRead(BLECharacteristic *pCharacteristic) {
  Serial.println("ONREAD");
}

/**
 * called when a messaged is received
 */
void BLEWrapper::onWrite(BLECharacteristic *pCharacteristic) {
  std::string rxValue = pCharacteristic->getValue();
  String recv = "";
  if (rxValue.length() > 0) {
    for (int i = 0; i < rxValue.length(); i++) {
      recv += rxValue[i];
    }
  }
  callback(recv);
}

/**
 * send a string to the BLE Host
 */
void BLEWrapper::sendText(String text) {
  if (deviceConnected && allowedToSend < millis()) {
    const char *cstr = text.c_str();
    pCharacteristic->setValue(std::string(cstr, text.length()));
    pCharacteristic->notify();
  }
}

