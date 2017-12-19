package fh.com.smartjacket.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import fh.com.smartjacket.Mapquest.GoogleMapsSearch;
import fh.com.smartjacket.R;
import fh.com.smartjacket.fragment.RouteFragment;

public class ChooseRouteActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
	private static final String LOG_TAG = "ChooseRouteActivity";
	private TextView currentPositionTextView;
	private AutoCompleteTextView searchTextView;
	private GoogleMapsSearch gms = new GoogleMapsSearch();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_route);

		Location location = getIntent().getParcelableExtra("location");
		if (location == null) {
			location = new Location("dummyprovider");
		}

		Toolbar toolbar = findViewById(R.id.chooseRouteActivityToolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Zieleingabe");



		this.searchTextView = findViewById(R.id.chooseRouteActivityToEditText);



		this.currentPositionTextView = findViewById(R.id.chooseRouteActivityPositionTextView);
		this.currentPositionTextView.setText("Long: " + location.getLongitude() + " Lat: " + location.getLatitude());

		ImageButton searchAddressImageButton = findViewById(R.id.chooseRouteActivitySearchAddressImageButton);
		final Location lambdaLoc = location;
		searchAddressImageButton.setOnClickListener((View view) -> {
			// TODO: Search address via web API
			ArrayList<String> suggestions =  gms.suggest(searchTextView.getText().toString(), lambdaLoc);
			searchTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, suggestions));
			searchTextView.showDropDown();
		});


		searchTextView.setOnKeyListener((View view, int i, KeyEvent keyEvent) -> {
			ArrayList<String> suggestions =  gms.suggest(searchTextView.getText().toString(), lambdaLoc);
			searchTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, suggestions));
			searchTextView.showDropDown();
			return false;
		});




		Button startNavigationButton = findViewById(R.id.chooseRouteActivityStartNavigationButton);
		startNavigationButton.setOnClickListener((View view) -> {
			// TODO: Get address information and start navigation
			Location loc = 	gms.getLocationOfAddress(searchTextView.getText().toString(), lambdaLoc);
			searchTextView.setText("Long: " + loc.getLongitude() + " Lat: " + loc.getLatitude());

			Intent data = new Intent();

			data.putExtra("location", loc);

			if (getParent() == null) {
				setResult(Activity.RESULT_OK, data);
			} else {
				getParent().setResult(Activity.RESULT_OK, data);
			}
			RouteFragment.locationToNavigate = loc;

			finish();
		});
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

}
