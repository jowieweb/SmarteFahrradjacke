package fh.com.smartjacket.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import fh.com.smartjacket.Bluetooth.BluetoothWrapper;
import fh.com.smartjacket.Bluetooth.MessageReceivedCallback;
import fh.com.smartjacket.Mapquest.GoogleMapsSearch;
import fh.com.smartjacket.Mapquest.LocationChangeListener;
import fh.com.smartjacket.Mapquest.MyLocationListener;
import fh.com.smartjacket.R;
import fh.com.smartjacket.adapter.TabPagerAdapter;
import fh.com.smartjacket.listener.OnFragmentInteractionListener;
import fh.com.smartjacket.fragment.RouteFragment;
import fh.com.smartjacket.fragment.SettingsFragment;
import fh.com.smartjacket.listener.OnAppChosenListener;
import fh.com.smartjacket.listener.OnIncomingCallListener;
import fh.com.smartjacket.listener.OnNotificationChangeListener;
import fh.com.smartjacket.listener.OnNotificationListener;
import fh.com.smartjacket.notifiction.NotificationReceiver;
import fh.com.smartjacket.pojo.AppNotification;
import fh.com.smartjacket.pojo.LightCalculator;
import fh.com.smartjacket.receiver.TelephoneStateReceiver;

public class MainActivity extends AppCompatActivity implements LocationChangeListener, OnFragmentInteractionListener, OnNotificationListener,
        MessageReceivedCallback, ActivityCompat.OnRequestPermissionsResultCallback, OnIncomingCallListener {
    public static final int PICK_ROUTE_REQUEST = 1337;
    public static final int PICK_APP_REQUEST = 1338;
    public static final int CONFIG_NOTIFICAION_REQUEST = 1339;
    private static final String LOG_TAG = "MainActivity";

    private ImageView bluetoothImageView;
    private BluetoothWrapper bw;
    private MyLocationListener mll;
    private LocationChangeListener onLocationChangeListener;
    private OnAppChosenListener onAppChosenListener;
    private OnNotificationChangeListener onNotificationChangeListener;
    private LightCalculator.LightLevel lightLevel = LightCalculator.LightLevel.Medim;
    private Location currentLocation = new Location("");
    private NotificationReceiver notificationReceiver;
    private TelephoneStateReceiver telephoneStateReceiver;

    private boolean isRinging = false;
    private RouteFragment routeFragment = new RouteFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabs(tabLayout);

        this.bluetoothImageView = findViewById(R.id.activity_main_bluetooth_icon);

        if(requestIncomingCallPermission()) {
            setupTelephoneStateReceiver();
        }
        requestAnswerCallPermission();

        mll = new MyLocationListener(this);
        mll.setOnLocationChangeListener(this);

        routeFragment.onLocationChange(this.mll.getLastLocation());
        Log.d(LOG_TAG,""+ mll.getLastLocation());


        bw = new BluetoothWrapper(this,this);
        bw.init();

        setupNotificationListenerService();
    }

    private void setupTelephoneStateReceiver() {
        this.telephoneStateReceiver = new TelephoneStateReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(this.telephoneStateReceiver, filter);
    }

    private void acceptIncomingCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TelecomManager tm = (TelecomManager) getSystemService(TELECOM_SERVICE);

            if (tm == null) {
                // whether you want to handle this is up to you really
                throw new NullPointerException("tm == null");
            }

            tm.acceptRingingCall();

        } else {
            new Thread(() -> {
                try {
                    //Runtime.getRuntime().exec("input keyevent " + Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
                    Thread.sleep(1500);
                    String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                    Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                            Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                    KeyEvent.KEYCODE_HEADSETHOOK));
                    Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                            Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                    KeyEvent.KEYCODE_HEADSETHOOK));
                    sendOrderedBroadcast(btnDown, enforcedPerm);
                    sendOrderedBroadcast(btnUp, enforcedPerm);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Runtime.exec(String) had an I/O problem, try to fall back
                    String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                    Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                            Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                    KeyEvent.KEYCODE_HEADSETHOOK));
                    Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                            Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                    KeyEvent.KEYCODE_HEADSETHOOK));
                    sendOrderedBroadcast(btnDown, enforcedPerm);
                    sendOrderedBroadcast(btnUp, enforcedPerm);
                }
            }).start();
        }
    }

    private boolean requestIncomingCallPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE}, 2);
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean requestAnswerCallPermission() {
        if (Build.VERSION.SDK_INT >= 26) {
            if (checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ANSWER_PHONE_CALLS}, 3);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "Permission for reading phone state was granted!");
                    setupTelephoneStateReceiver();

                } else {
                    Log.i(LOG_TAG, "Permission for reading phone state was denied!");
                }
                break;

            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "Permission for answering phone calls was granted!");

                } else {
                    Log.i(LOG_TAG, "Permission for answering phone calls was denied!");
                }
                break;
        }
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
        //unregisterReceiver(this.telephoneStateReceiver);
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

            case CONFIG_NOTIFICAION_REQUEST:
                if (resultCode != Activity.RESULT_OK || data == null) {
                    return;
                }

                AppNotification app = new AppNotification(data.getStringExtra(getString(R.string.intent_extra_selected_app)));
                Log.i(LOG_TAG, "Bundle->Index: " + data.getLongExtra(getString(R.string.intent_extra_vibration_pattern), AppNotification.DEFAULT_VIBRATION_PATTERN_INDEX));
                app.setVibrationPatternIndex((int) data.getLongExtra(getString(R.string.intent_extra_vibration_pattern), AppNotification.DEFAULT_VIBRATION_PATTERN_INDEX));
                app.restoreData(this);
                if (this.onNotificationChangeListener != null) {
                    this.onNotificationChangeListener.onNotificationChange(app);
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

    public void setOnNotificationChangeListener(OnNotificationChangeListener onNotificationChangeListener) {
        this.onNotificationChangeListener = onNotificationChangeListener;
    }

    @Override
    public void onLocationChange(Location location) {
        lightLevel = new LightCalculator(location).getLightLevel();
        currentLocation = location;
        settingsFragment.setCurrentLocation(location);
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
        Log.d(LOG_TAG, "NOTIFICATION!");
        if (packageName != null) {
            Toast.makeText(this,packageName,Toast.LENGTH_LONG);
            Log.i(LOG_TAG, "Received notification from " + packageName);

            ArrayList<AppNotification> appNotifications = this.settingsFragment.getAppNotificationList();
            if (appNotifications != null) {

                for (AppNotification app : appNotifications) {
                    if (app.getAppPackageName().equals(packageName)) {

                        int vibrationPatternIndex = app.getVibrationPatternIndex();
                        if (vibrationPatternIndex >= getResources().getStringArray(R.array.vibration_pattern).length) {
                            vibrationPatternIndex = getResources().getStringArray(R.array.vibration_pattern).length - 1;
                        }
                        String vibrationPattern = getResources().getStringArray(R.array.vibration_pattern)[vibrationPatternIndex];
                        Log.d(LOG_TAG, "Sending vibration pattern " + vibrationPatternIndex + ": " + vibrationPattern);
                        bw.sendText(vibrationPattern);

                        return;
                    }
                }
            }
        }
    }

    @Override
    public void BLEMessageReceived(byte[] data) {

        String message = new String(data);
        Log.i(LOG_TAG, "BLE Message " + message);

        switch (message)
        {
            case "btn":
                Log.i(LOG_TAG, "BUTTON DOWN!");
                if(isRinging){
                    bw.sendText("bv0");
                    acceptIncomingCall();
                } else
                {
                    Location loc = new GoogleMapsSearch(null).getLocationOfAddress(settingsFragment.loadHomeAddress().toString(),currentLocation );
                    routeFragment.setNewDestination(loc, "Home");
                    break;
                }
                break;

        }
    }

    @Override
    public void BLEDeviceConnected() {
        this.bluetoothImageView.setImageDrawable(getDrawable(R.drawable.ic_action_ble_device_connected));
    }

    @Override
    public void BLEDeviceDisconnected() {
        this.bluetoothImageView.setImageDrawable(getDrawable(R.drawable.ic_action_bluetooth));
    }

    @Override
    public void onIncomingCall() {
        Log.d(LOG_TAG, "Got incoming call.");
        bw.sendText(getString(R.string.intent_extra_vibration_pattern_incomming_call));
        isRinging = true;

    }

    public void onCallCanceled(){
        Log.d(LOG_TAG, "incoming call canceled");
        bw.sendText("bv0");
        isRinging = false;
    }
}
