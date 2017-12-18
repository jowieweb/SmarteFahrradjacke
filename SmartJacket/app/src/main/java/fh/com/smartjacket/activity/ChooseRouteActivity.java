package fh.com.smartjacket.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

import fh.com.smartjacket.R;

public class ChooseRouteActivity extends AppCompatActivity implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
	private static final String LOG_TAG = "ChooseRouteActivity";
	private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
	private LocationManager locationManager;
	private TextView currentPositionTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_route);

		Toolbar toolbar = findViewById(R.id.chooseRouteActivityToolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Zieleingabe");

		this.currentPositionTextView = findViewById(R.id.chooseRouteActivityPositionTextView);

		ImageButton searchAddressImageButton = findViewById(R.id.chooseRouteActivitySearchAddressImageButton);
		searchAddressImageButton.setOnClickListener((View view) -> {
			// TODO: Search address via web API
		});

		Button startNavigationButton = findViewById(R.id.chooseRouteActivityStartNavigationButton);
		startNavigationButton.setOnClickListener((View view) -> {
			// TODO: Get address information and start navigation
		});

		if (this.locationManager == null) {
			this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			int accessLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
			if (accessLocationPermission != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);

			} else {
				requestCurrentLocation();
			}

		} else {
			requestCurrentLocation();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_FINE_LOCATION:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					requestCurrentLocation();

				} else {
					this.locationManager = null;
				}
		}
	}

	private void requestCurrentLocation() {
		if (this.locationManager == null) {
			return;
		}

		checkIfLocationIsEnabled();

		Location lastKnownLocation = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation != null && lastKnownLocation.getTime() < Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {

			// Last known location is not older than 2 minutes -> use it
			setCurrentPosition(lastKnownLocation);

			return;
		}

		this.locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
	}

	private void checkIfLocationIsEnabled() {
		if (this.locationManager == null) {
			return;
		}

		if (!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

			alertDialog.setTitle("GPS aktivieren");
			alertDialog.setMessage("GPS ist derzeit deaktiviert. Bitte aktiviere GPS, um auf den Standort zuzugreifen.");

			alertDialog.setPositiveButton("Einstellungen", (dialog, which) -> {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			});
			alertDialog.setNegativeButton("Abbrechen", (dialog, which) -> {dialog.cancel(); locationManager = null; });

			alertDialog.create().show();
		}
	}

	private void setCurrentPosition(Location location) {
		// TODO: Search for address details for the given location
		this.currentPositionTextView.setText("Standort: Artilleriestraße 9, 32427 Minden, Germany");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLocationChanged(Location location) {
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();

		Log.d(LOG_TAG, "Got new Location! Lat: " + latitude + ", Long: " + longitude);
		setCurrentPosition(location);
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
