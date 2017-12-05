package fh.com.smartjacket;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fh.com.smartjacket.Bluetooth.BluetoothWrapper;
import fh.com.smartjacket.Bluetooth.MessageReceivedCallback;
import fh.com.smartjacket.Mapquest.Mapquest;
import fh.com.smartjacket.Mapquest.MyLocationListener;
import fh.com.smartjacket.Mapquest.TurnPoint;

public class MainActivity extends Activity implements MessageReceivedCallback{

    private BluetoothWrapper bw;
    private TextView tv;
    private MyLocationListener mll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textbox1);

        bw = new BluetoothWrapper(this, this);

        try{
            bw.init();
        }catch (Exception e){

        }

        Mapquest mq = new Mapquest();
        ArrayList<TurnPoint> list =  mq.debugTurnPoints();
        tv.setText("");
        for(TurnPoint tp:list){
            tv.append(tp.toString() + "\n");
        }

        mll = new MyLocationListener(this);
        mll.init();

    }

    protected void onResume() {
        super.onResume();
        bw.startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void newMessage(final byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText("mytext "+new String(data));
                tv.invalidate();
            }
        });
    }
}
