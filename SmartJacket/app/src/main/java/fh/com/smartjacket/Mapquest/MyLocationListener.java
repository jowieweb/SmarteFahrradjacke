package fh.com.smartjacket.Mapquest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by jowie on 05.12.2017.
 */

public class MyLocationListener implements android.location.LocationListener {
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final String LOG_TAG = "MyLocationListener";

    private Activity activity;
    private Location lastLocation;
    private LocationManager locationManager;
    private LocationChangeListener locationChangeListener;

    public MyLocationListener(Activity act) {
        this.activity = act;
        init();
    }

    private void init(){
        int accessLocationPermission = ContextCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (accessLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);

        } else {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            requestCurrentLocation();
        }
    }

    private void checkIfLocationIsEnabled() {
        if (!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this.activity);

            alertDialog.setTitle("GPS aktivieren");
            alertDialog.setMessage("GPS ist derzeit deaktiviert. Bitte aktiviere GPS, um auf den Standort zuzugreifen.");

            alertDialog.setPositiveButton("Einstellungen", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                this.activity.startActivity(intent);
            });
            alertDialog.setNegativeButton("Abbrechen", (dialog, which) -> {dialog.cancel();});

            alertDialog.create().show();
        }
    }

    private void requestCurrentLocation() {
        checkIfLocationIsEnabled();

        Location lastKnownLocation = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (!checkLastPosition(lastKnownLocation)) {
            Log.d(LOG_TAG,"GPS IS TOO OLD");
            lastKnownLocation = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (!checkLastPosition(lastKnownLocation)) {
                Log.d(LOG_TAG,"NETWORK IS TOO OLD");
                lastKnownLocation = this.locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                checkLastPosition(lastKnownLocation);
            }

        }

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 2000, 10, this);
    }

    private boolean checkLastPosition(Location lastKnownLocation){
        if (lastKnownLocation != null && lastKnownLocation.getTime() < Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {

            // Last known location is not older than 2 minutes -> use it
            setLastLocation(lastKnownLocation);
            return  true;
        }
        return  false;

    }

    private void setLastLocation(Location location) {
        this.lastLocation = location;
    }

    public Location getLastLocation() {
        return this.lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        setLastLocation(location);

        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();

        //Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, msg);

        if (this.locationChangeListener != null) {
            this.locationChangeListener.onLocationChange(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void setOnLocationChangeListener(LocationChangeListener locationChangeListener) {
        this.locationChangeListener = locationChangeListener;
    }
}
