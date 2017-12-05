package fh.com.smartjacket.Mapquest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by jowie on 05.12.2017.
 */

public class MyLocationListener implements android.location.LocationListener {

    private Activity activity;
    private Location lastLocation;
    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 20;

    public MyLocationListener(Activity act) {
        this.activity = act;

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);


    }

    public  void init(){
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);

    }
    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();

        Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_LONG).show();

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
}
