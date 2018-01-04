#ifndef BLEWRAPPER_H
  #define BLEWRAPPER_H
  #include <BLEDevice.h>
  #include <BLEServer.h>
  #include <BLEUtils.h>
  #include <BLE2902.h>
  
  #define SERVICE_UUID           "6E400001-B5A3-F393-E0A9-E50E24DCCA9E" // UART service UUID
  #define CHARACTERISTIC_UUID_RX "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
  #define CHARACTERISTIC_UUID_TX "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"
  class BLEWrapper:  public BLEServerCallbacks ,  public BLECharacteristicCallbacks
  {
    public:
      void start(void (*callback)(String));
      void onConnect(BLEServer* pServer);
      void onDisconnect(BLEServer* pServer);
      void onWrite(BLECharacteristic *pCharacteristic);
      void sendText(String text);
    private:
    
      BLECharacteristic *pCharacteristic;

      bool deviceConnected = false;
      uint8_t txValue = 0;
      const TickType_t xDelay = 1000 / portTICK_PERIOD_MS;
      void (*callback)(String); 
      long allowedToSend= 0;
      uint8_t temp= 0;
  };

#endif
