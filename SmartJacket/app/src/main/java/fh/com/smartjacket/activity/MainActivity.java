package fh.com.smartjacket.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import fh.com.smartjacket.Bluetooth.BluetoothWrapper;
import fh.com.smartjacket.Mapquest.LocationChangeListener;
import fh.com.smartjacket.Mapquest.MyLocationListener;
import fh.com.smartjacket.R;
import fh.com.smartjacket.adapter.TabPagerAdapter;
import fh.com.smartjacket.listener.OnFragmentInteractionListener;
import fh.com.smartjacket.fragment.RouteFragment;
import fh.com.smartjacket.fragment.SettingsFragment;
import fh.com.smartjacket.listener.OnAppChosenListener;
import fh.com.smartjacket.listener.OnNotificationListener;
import fh.com.smartjacket.notifiction.NotificationReceiver;
import fh.com.smartjacket.pojo.AppNotification;
import fh.com.smartjacket.pojo.LightCalculator;

public class MainActivity extends AppCompatActivity implements LocationChangeListener, OnFragmentInteractionListener, OnNotificationListener {
    public static final int PICK_ROUTE_REQUEST = 1337;
    private static final int PICK_APP_REQUEST = 1338;
    private static final String LOG_TAG = "MainActivity";

    private BluetoothWrapper bw;
    private MyLocationListener mll;
    private LocationChangeListener onLocationChangeListener;
    private OnAppChosenListener onAppChosenListener;
    private LightCalculator.LightLevel lightLevel = LightCalculator.LightLevel.Medim;
    private NotificationReceiver notificationReceiver;

    private RouteFragment routeFragment = new RouteFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

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

        setupNotificationListenerService();
    }

    private void setupNotificationListenerService() {
        if (!isNotificationServiceActive()) {
            Log.i(LOG_TAG, "NotificationListenerService is not active. Showing permission screen.");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Benachrichtigungen");
            builder.setMessage("Der Zugriff auf Benachrichtigungen ist nicht aktiviert. Jetzt aktivieren?");
            builder.setPositiveButton("Ja", (DialogInterface dialog, int id) -> startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));
            builder.setNegativeButton("Nein", (DialogInterface dialog, int id) -> Log.i(LOG_TAG,"Notification permission not enabled!"));
            builder.create().show();

        } else {
            this.notificationReceiver = new NotificationReceiver(this);

            IntentFilter filter = new IntentFilter();
            filter.addAction("fh.com.smartjacket");

            registerReceiver(this.notificationReceiver, filter);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(this.routeFragment, "Navigation");
        adapter.addFragment(this.settingsFragment, "Einstellungen");

        viewPager.setAdapter(adapter);
    }

    /**
     * Check if this app is allowed to listen to and/or send notifications.
     * @return True if this application is allowed to listen to notifications. False otherwise.
     */
    private boolean isNotificationServiceActive() {
        String packageName = getPackageName();
        String enabledNotificationListeners = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");

        if (enabledNotificationListeners != null && !enabledNotificationListeners.isEmpty()) {
            Log.d(LOG_TAG, "Enabled notification listeners: " + enabledNotificationListeners);
            String enabledListeners[] = enabledNotificationListeners.split(":");

            for (String enabledComponentName : enabledListeners) {
                ComponentName componentName = ComponentName.unflattenFromString(enabledComponentName);
                if (componentName != null && componentName.getPackageName().equals(packageName)) {
                    return true;
                }
            }
        }

        return false;
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.notificationReceiver);
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
                Location loc = data.getParcelableExtra(getString(R.string.intent_extra_location));
                String desName = data.getSerializableExtra(getString(R.string.intent_extra_destination_name)).toString();
                if(loc != null && desName != null) {
                    routeFragment.setNewDestination(loc, desName);
                }
                break;

            case PICK_APP_REQUEST:
                if (resultCode != Activity.RESULT_OK || data == null) {
                    return;
                }

                AppNotification appNotification = new AppNotification(data.getStringExtra(getString(R.string.intent_extra_selected_app)));
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

    /**
     * Is called when a notification is received.
     * @param packageName Package name of the app that posted this notification.
     */
    @Override
    public void onNotification(String packageName) {
        if (packageName != null) {
            Log.i(LOG_TAG, "Received notification from " + packageName);

            ArrayList<AppNotification> appNotifications = this.settingsFragment.getAppNotificationList();
            if (appNotifications != null) {

                for (AppNotification app : appNotifications) {
                    if (app.getAppPackageName().equals(packageName)) {

                        Log.d(LOG_TAG, "App in list!");

                        // TODO: Send vibration data for this app

                        return;
                    }
                }
            }
        }
    }
}
