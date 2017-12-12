package fh.com.smartjacket;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fh.com.smartjacket.Bluetooth.BluetoothWrapper;
import fh.com.smartjacket.Bluetooth.MessageReceivedCallback;
import fh.com.smartjacket.Mapquest.Mapquest;
import fh.com.smartjacket.Mapquest.MyLocationListener;
import fh.com.smartjacket.Mapquest.TurnPoint;
import fh.com.smartjacket.adapter.TabPagerAdapter;
import fh.com.smartjacket.fragment.RouteFragment;
import fh.com.smartjacket.fragment.SettingsFragment;

<<<<<<< HEAD
public class MainActivity extends AppCompatActivity implements MessageReceivedCallback{
    private static final String LOG_TAG = "MainActivity";
=======

public class MainActivity extends Activity implements MessageReceivedCallback{
>>>>>>> 392259bbe1a988f32d9f64c5e7e1117338afbb1b

    private BluetoothWrapper bw;
    private TextView tv;
    private ToggleButton virbation;
    private MyLocationListener mll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabs(tabLayout);

        /*
        tv = findViewById(R.id.textbox1);
        virbation = findViewById(R.id.vibration);
        addVibrationActionListener();

<<<<<<< HEAD
=======

>>>>>>> 392259bbe1a988f32d9f64c5e7e1117338afbb1b
        bw = new BluetoothWrapper(this, this);

        try{
            bw.init();

        } catch (Exception e){
            Log.e(LOG_TAG, "Error initializing Bluetooth: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Mapquest mq = new Mapquest();
        ArrayList<TurnPoint> list =  mq.debugTurnPoints();
        tv.setText("");

        for (TurnPoint tp:list) {
            tv.append(tp.toString() + "\n");
        }

        mll = new MyLocationListener(this);
        mll.init();

        Location location = new Location(52.296853, 8.904645);
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());
        String Sunrise =calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String sunset =calculator.getOfficialSunsetForDate(Calendar.getInstance());
        Log.i("sunset", sunset);
        Log.i("sunrise", Sunrise);
    */
    }

    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new RouteFragment(), "Navigation");
        adapter.addFragment(new SettingsFragment(), "Einstellungen");

        viewPager.setAdapter(adapter);
    }

    private void setupTabs(TabLayout tabLayout) {
        // TODO: Add icons to tabs
    }

    protected void onResume() {
        super.onResume();
        //bw.startScan();
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
                    bw.sendText("bv1");
                } else {
                    bw.sendText("bv0");
                }
            }
        });
    }
}
