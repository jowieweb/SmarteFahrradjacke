package fh.com.smartjacket.Bluetooth;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fh.com.smartjacket.Mapquest.TurnPoint;

/**
 * Created by jowie on 04.12.2017.
 */
@TargetApi(21)
public class BluetoothWrapper {

    private static  BluetoothWrapper instance;

    private BluetoothAdapter mBluetoothAdapter;
    private  static String LOG_TAG ="BLEWRAPPER";
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 100000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private Activity activity;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private static final UUID serviceUuid = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID characteristicUuid = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID characteristicUuidWrite = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private MessageReceivedCallback callback;
    private boolean isConnected = false;

    public BluetoothWrapper(Activity act, MessageReceivedCallback callback) {
        activity = act;
        this.callback = callback;
    }

    public static BluetoothWrapper getInstance(){
        return instance;
    }


    public boolean init()  {
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect beacons.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                public void onDismiss(DialogInterface dialog) {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
        mHandler = new Handler();

        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "BLE Not Supported", Toast.LENGTH_SHORT).show();
           return  false;
        }
        final android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        startScan();
        BluetoothWrapper.instance = this;
        return true;
    }

    public void startScan() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            filters = new ArrayList<ScanFilter>();
            scanLeDevice(true);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                        mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            mLEScanner.startScan(filters, settings, mScanCallback);

        } else {
                mLEScanner.stopScan(mScanCallback);
        }
    }


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice btDevice = result.getDevice();
            if(btDevice.getAddress().equals("30:AE:A4:38:7F:76"))
                connectToDevice(btDevice);
        }

    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(LOG_TAG,  "leScan: " +  device.toString());

                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(activity, true, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(LOG_TAG,"onConnectionStateChange: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i(LOG_TAG, "!!STATE_CONNECTED!!");
                    gatt.discoverServices();
                    isConnected = true;
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i(LOG_TAG, "!!STATE_DISCONNECTED!!");
                    isConnected = false;
                    startScan();
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        public boolean setCharacteristicNotification(BluetoothGatt gatt, BluetoothDevice device, UUID serviceUuid, UUID characteristicUuid, boolean enable) {
            BluetoothGattCharacteristic characteristic = gatt.getService(serviceUuid).getCharacteristic(characteristicUuid);
            gatt.setCharacteristicNotification(characteristic, enable);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            return gatt.writeDescriptor(descriptor);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            setCharacteristicNotification(gatt, gatt.getDevice(), serviceUuid, characteristicUuid, true);

        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            String read = new String(characteristic.getValue());
            Log.i("value", "" + read);
            callback.BLEMessageReceived(characteristic.getValue());
        }
    };

    /**
     * send any text to the BLE device
     * @param text
     */
    public void sendText(String text) {
        if(!isConnected)
            return;
        if(text == null)
            return;
        if(text.length() < 1)
            return;
        if(mGatt == null)
            return;

        BluetoothGattCharacteristic writeChara = mGatt.getService(serviceUuid).getCharacteristic(characteristicUuidWrite);
        writeChara.setValue(text);
        mGatt.writeCharacteristic(writeChara);
    }

    /**
     * send a turnpoint message to the BLE device
     * @param tp
     */
    public void sendText(TurnPoint tp){
        if(tp == null)
            return;
        if(!isConnected)
            return;

        BluetoothGattCharacteristic writeChara = mGatt.getService(serviceUuid).getCharacteristic(characteristicUuidWrite);
        TurnPoint.TurnDirection td = tp.getTurnDirection();
        if(td == TurnPoint.TurnDirection.left)
           sendText("turn: left");
        else if (td == TurnPoint.TurnDirection.right)
            sendText("turn: right");
    }
}
