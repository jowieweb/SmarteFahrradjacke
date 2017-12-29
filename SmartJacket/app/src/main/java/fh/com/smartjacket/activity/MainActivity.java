package fh.com.smartjacket.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import fh.com.smartjacket.Bluetooth.BluetoothWrapper;
import fh.com.smartjacket.Bluetooth.MessageReceivedCallback;
import fh.com.smartjacket.Mapquest.LocationChangeListener;
import fh.com.smartjacket.Mapquest.MyLocationListener;
import fh.com.smartjacket.R;
import fh.com.smartjacket.adapter.TabPagerAdapter;
import fh.com.smartjacket.listener.OnFragmentInteractionListener;
import fh.com.smartjacket.fragment.RouteFragment;
import fh.com.smartjacket.fragment.SettingsFragment;
import fh.com.smartjacket.listener.OnAppChosenListener;
import fh.com.smartjacket.pojo.AppNotification;
import fh.com.smartjacket.pojo.LightCalculator;

public class MainActivity extends AppCompatActivity implements LocationChangeListener, OnFragmentInteractionListener {
    public static final int PICK_ROUTE_REQUEST = 1337;
    private static final int PICK_APP_REQUEST = 1338;
    private static final String LOG_TAG = "MainActivity";

    private BluetoothWrapper bw;
    private MyLocationListener mll;
    private LocationChangeListener onLocationChangeListener;
    private OnAppChosenListener onAppChosenListener;
    private LightCalculator.LightLevel lightLevel = LightCalculator.LightLevel.Medim;

    private RouteFragment routeFragment = new RouteFragment();

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

        routeFragment.onLocationChange(this.mll.getLastLocation());
        Log.d(LOG_TAG,""+ mll.getLastLocation());


        bw = new BluetoothWrapper(this,(byte[] data) -> {
            Log.i(LOG_TAG, "BLE Message " + new String(data));
        });
        bw.init();


    }

    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(routeFragment, "Navigation");
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
    public void onAddRouteButtonClicked() {
        Location location = this.mll.getLastLocation();
        Intent intent = new Intent(this, RouteChooserActivity.class);
        intent.putExtra("location", location);

        startActivityForResult(intent, PICK_ROUTE_REQUEST);
    }

    @Override
    public void onAddAppNotificationButtonClicked() {
        Intent intent = new Intent(this, AppChooserActivity.class);

        startActivityForResult(intent, PICK_APP_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_ROUTE_REQUEST:
                if(data == null)
                    return;

                Log.i(LOG_TAG, "GOT DATA IN MAINACTIVITY");
                Location loc = data.getParcelableExtra("location");
                String desName = data.getSerializableExtra("desinationName").toString();
                if(loc != null && desName != null) {
                    routeFragment.setNewDestination(loc, desName);
                }
                break;

            case PICK_APP_REQUEST:
                if (resultCode != Activity.RESULT_OK || data == null) {
                    return;
                }

                AppNotification appNotification = new AppNotification(data.getStringExtra("selected_app"));
                appNotification.restoreData(this);
                if (this.onAppChosenListener != null) {
                    this.onAppChosenListener.OnAppChosen(appNotification);
                }
                break;
        }
    }

    public void setOnLocationListener(LocationChangeListener locationChangeListener) {
        this.onLocationChangeListener = locationChangeListener;
    }

    public void setOnAppChosenListener(OnAppChosenListener onAppChosenListener) {
        this.onAppChosenListener = onAppChosenListener;
    }

    @Override
    public void onLocationChange(Location location) {
        lightLevel = new LightCalculator(location).getLightLevel();
        Log.i(LOG_TAG, "LightLevel: " + lightLevel);
        if (this.onLocationChangeListener != null) {
            this.onLocationChangeListener.onLocationChange(location);
        }
    }
}
