package fh.com.smartjacket.Bluetooth;

/**
 * Created by jowie on 04.12.2017.
 */

public interface MessageReceivedCallback {
    public void BLEMessageReceived(byte[] data);
    void BLEDeviceConnected();
    void BLEDeviceDisconnected();
}
