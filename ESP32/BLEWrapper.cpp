#include "Arduino.h"
#include "BLEWrapper.h"



void BLEWrapper::onConnect(BLEServer* pServer) {
  deviceConnected = true;
  Serial.println("DEV CONNECTED");
};

void BLEWrapper::onDisconnect(BLEServer* pServer) {
  deviceConnected = false;
  Serial.println("DEV DISCONNECTED");
}

void BLEWrapper::start(void (*callback)(String)) {
  BLEDevice::init("UART Service");
  this->callback = callback;
  // Create the BLE Server
  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(this);

  // Create the BLE Service
  BLEService *pService = pServer->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  pCharacteristic = pService->createCharacteristic(
                      CHARACTERISTIC_UUID_TX,
                      BLECharacteristic::PROPERTY_NOTIFY
                    );

  pCharacteristic->addDescriptor(new BLE2902());

  BLECharacteristic *pCharacteristic = pService->createCharacteristic(
                                         CHARACTERISTIC_UUID_RX,
                                         BLECharacteristic::PROPERTY_WRITE
                                       );

  pCharacteristic->setCallbacks(this);

  // Start the service
  pService->start();

  // Start advertising
  pServer->getAdvertising()->start();
  Serial.println("Waiting a client connection to notify...");
}

void BLEWrapper::onWrite(BLECharacteristic *pCharacteristic) {
  std::string rxValue = pCharacteristic->getValue();
  String recv = "";
  if (rxValue.length() > 0) {
    // Serial.println("*********");
    // Serial.print("Received Value: ");
    for (int i = 0; i < rxValue.length(); i++) {
      //Serial.print(rxValue[i]);
      recv += rxValue[i];
    }

    // Serial.println();
    //  Serial.println("*********");
  }
  callback(recv);
}

void BLEWrapper::sendText(String text) {
  if (deviceConnected) {
    const char *cstr = text.c_str();
    


    pCharacteristic->setValue(std::string(cstr, text.length()));
    Serial.println(text.length());
    pCharacteristic->notify();
  }
}

