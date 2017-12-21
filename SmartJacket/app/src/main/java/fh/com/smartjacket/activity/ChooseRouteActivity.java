package fh.com.smartjacket.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import java.util.ArrayList;

import fh.com.smartjacket.Mapquest.GoogleMapsSearch;
import fh.com.smartjacket.Mapquest.Mapquest;
import fh.com.smartjacket.Mapquest.SuggestionListener;
import fh.com.smartjacket.R;
import fh.com.smartjacket.fragment.RouteFragment;

public class ChooseRouteActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, SuggestionListener {
	private static final String LOG_TAG = "ChooseRouteActivity";
	private AutoCompleteTextView searchTextView;
	private TextView currentPositionTextView;
	private GoogleMapsSearch gms = new GoogleMapsSearch(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_route);

		Location location = getIntent().getParcelableExtra("location");
		if (location == null) {
			location = new Location("dummyprovider");

		} else {

			GetAddressFromLocationTask task = new GetAddressFromLocationTask();
			task.execute(location);
		}

		Toolbar toolbar = findViewById(R.id.chooseRouteActivityToolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Zieleingabe");

		ImageView routeImageView = findViewById(R.id.chooseActivityRouteImageView);

		this.searchTextView = findViewById(R.id.chooseRouteActivityToEditText);


		this.currentPositionTextView = findViewById(R.id.chooseRouteActivityPositionTextView);
		this.currentPositionTextView.setText("Long: " + location.getLongitude() + " Lat: " + location.getLatitude());

		ImageButton searchAddressImageButton = findViewById(R.id.chooseRouteActivitySearchAddressImageButton);
		final Location lambdaLoc = location;
		searchAddressImageButton.setOnClickListener((View view) -> {
			new SearchForLocationFromAddressTask(this).execute(this.searchTextView.getText().toString());

		});

		this.searchTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!s.equals("")) {
					gms.suggest(searchTextView.getText().toString(), lambdaLoc);
				}

			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});



		Button startNavigationButton = findViewById(R.id.chooseRouteActivityStartNavigationButton);
		startNavigationButton.setOnClickListener((View view) -> {
			String searchInput = this.searchTextView.getText().toString();

			if (!searchInput.isEmpty()) {
				Location loc = gms.getLocationOfAddress(searchTextView.getText().toString(), lambdaLoc);
				this.searchTextView.setText("Long: " + loc.getLongitude() + " Lat: " + loc.getLatitude());

				Intent data = new Intent();
				data.putExtra("location", loc);
				data.putExtra("desinationName", searchInput);

				if (getParent() == null) {
					setResult(Activity.RESULT_OK, data);
				} else {
					getParent().setResult(Activity.RESULT_OK, data);
				}

				finish();

			} else {
				this.searchTextView.setError("Bitte Ziel eingeben!");
			}
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

	@Override
	public void suggest(String[] suggestions) {
		searchTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, suggestions));
		searchTextView.showDropDown();
	}

	private static class SearchForLocationFromAddressTask extends AsyncTask<String, Void, Location> {
		private WeakReference<ChooseRouteActivity> activity;

		public SearchForLocationFromAddressTask(ChooseRouteActivity activity) {
			this.activity = new WeakReference<>(activity);
		}

		@Override
		protected Location doInBackground(String... strings) {
			return Mapquest.getLocationFromAddress(strings[0]);
		}

		@Override
		protected void onPostExecute(Location location) {
			if (location != null) {
				Log.d(LOG_TAG, "Got location of destination: Lat: " + location.getLatitude() + ", Long: " + location.getLongitude());

				// Get map image of destination
				Picasso.with(this.activity.get()).load(Mapquest.getStaticMapApiUrlForLocation(location)).into((ImageView)this.activity.get().findViewById(R.id.chooseActivityRouteImageView));
			}
		}
	}

	private class GetAddressFromLocationTask extends  AsyncTask<Location, Void, String> {

		@Override
		protected String doInBackground(Location... locations) {
			return Mapquest.getAddressFromLocation(locations[0]);
		}

		@Override
		protected void onPostExecute(String address) {
			if (address != null && !address.isEmpty()) {
				currentPositionTextView.setText("Standort: " + address);
			}
		}
	}
}
