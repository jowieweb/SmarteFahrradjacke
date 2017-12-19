package fh.com.smartjacket.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import fh.com.smartjacket.Bluetooth.BluetoothWrapper;
import fh.com.smartjacket.Bluetooth.MessageReceivedCallback;
import fh.com.smartjacket.Mapquest.LocationChangeListener;
import fh.com.smartjacket.Mapquest.MyLocationListener;
import fh.com.smartjacket.R;
import fh.com.smartjacket.adapter.TabPagerAdapter;
import fh.com.smartjacket.fragment.RouteFragment;
import fh.com.smartjacket.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity implements LocationChangeListener, MessageReceivedCallback, RouteFragment.OnFragmentInteractionListener {
    public static final int PICK_ROUTE_REQUEST = 1337;
    private static final String LOG_TAG = "MainActivity";

    private BluetoothWrapper bw;
    private TextView tv;
    private ToggleButton virbation;
    private MyLocationListener mll;
    private LocationChangeListener onLocationChangeListener;

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

        mll = new MyLocationListener(this);
        mll.setOnLocationChangeListener(this);
        mll.init();

        /*
        tv = findViewById(R.id.textbox1);
        virbation = findViewById(R.id.vibration);
        addVibrationActionListener();

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

    @Override
    public void onAddRouteButtonClicked() {
        Location location = this.mll.getLastLocation();
        Intent intent = new Intent(this, ChooseRouteActivity.class);
        intent.putExtra("location", location);

        startActivityForResult(intent, PICK_ROUTE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_ROUTE_REQUEST) {
            // TODO: Do stuff and show route
        }
    }

    public void setOnLocationListener(LocationChangeListener locationChangeListener) {
        this.onLocationChangeListener = locationChangeListener;
    }

    @Override
    public void onLocationChange(Location location) {
        if (this.onLocationChangeListener != null) {
            this.onLocationChangeListener.onLocationChange(location);
        }
    }
}
