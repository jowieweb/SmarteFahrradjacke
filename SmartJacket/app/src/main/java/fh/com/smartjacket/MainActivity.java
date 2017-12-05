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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import fh.com.smartjacket.Bluetooth.BluetoothWrapper;
import fh.com.smartjacket.Bluetooth.MessageReceivedCallback;
import fh.com.smartjacket.Mapquest.Mapquest;
import fh.com.smartjacket.Mapquest.MyLocationListener;
import fh.com.smartjacket.Mapquest.TurnPoint;

public class MainActivity extends Activity implements MessageReceivedCallback{

    private BluetoothWrapper bw;
    private TextView tv;
    private ToggleButton virbation;
    private MyLocationListener mll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textbox1);
        virbation = findViewById(R.id.vibration);
        addVibrationActionListener();


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
                tv.setText(new String(data));
                tv.invalidate();
            }
        });
    }

    private void addVibrationActionListener(){
        virbation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bw.sendText("bv1hallotestblahbubberichschreibvieltext");
                } else {
                    bw.sendText("bv0");
                }
            }
        });
    }
}
