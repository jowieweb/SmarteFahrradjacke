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
  BLEDevice::init("UART Service");
  this->callback = callback;
  
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

  // Start the service
  pService->start();

  // Start advertising
  pServer->getAdvertising()->start();
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

