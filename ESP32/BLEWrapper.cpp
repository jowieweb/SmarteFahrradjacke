#include "Arduino.h"
#include "BLEWrapper.h"



void BLEWrapper::onConnect(BLEServer* pServer) {
  deviceConnected = true;
};

void BLEWrapper::onDisconnect(BLEServer* pServer) {
  deviceConnected = false;
}

void BLEWrapper::start() {
  BLEDevice::init("UART Service");

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

  if (rxValue.length() > 0) {
    Serial.println("*********");
    Serial.print("Received Value: ");
    for (int i = 0; i < rxValue.length(); i++)
      Serial.print(rxValue[i]);

    Serial.println();
    Serial.println("*********");
  }
}

void BLEWrapper::loop() {
  if (deviceConnected) {
    Serial.println("*** Sent Value:  ***\n");// txValue);
    pCharacteristic->setValue(&txValue, 1);
    pCharacteristic->notify();
    txValue++;
  }
   //vTaskDelay( xDelay );
}

