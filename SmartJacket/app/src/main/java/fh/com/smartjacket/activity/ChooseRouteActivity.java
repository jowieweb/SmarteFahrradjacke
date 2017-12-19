package fh.com.smartjacket.activity;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import fh.com.smartjacket.Mapquest.GoogleMapsSearch;
import fh.com.smartjacket.Mapquest.Mapquest;
import fh.com.smartjacket.R;

public class ChooseRouteActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
	private static final String LOG_TAG = "ChooseRouteActivity";
	private TextView searchTextView;
	private Location destinationLocation;
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

		ImageView routeImageView = findViewById(R.id.chooseActivityRouteImageView);
		this.searchTextView = findViewById(R.id.chooseRouteActivityToEditText);
		TextView currentPositionTextView = findViewById(R.id.chooseRouteActivityPositionTextView);
		currentPositionTextView.setText("Long: " + location.getLongitude() + " Lat: " + location.getLatitude());

		ImageButton searchAddressImageButton = findViewById(R.id.chooseRouteActivitySearchAddressImageButton);
		final Location lambdaLoc = location;
		searchAddressImageButton.setOnClickListener((View view) -> {
			new SearchForLocationFromAddressTask(this).execute(this.searchTextView.getText().toString());
		});


		searchTextView.setOnKeyListener((View view, int i, KeyEvent keyEvent) -> {
			gms.suggest(searchTextView.getText().toString(), lambdaLoc);
			return  false;
		});


		Button startNavigationButton = findViewById(R.id.chooseRouteActivityStartNavigationButton);
		startNavigationButton.setOnClickListener((View view) -> {

			if (this.destinationLocation != null) {
				// TODO: Location of the destination is available -> Do something with it

			} else {
				// TODO: Show error
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

	public void setDestinationLocation(Location location) {
		this.destinationLocation = location;
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

			this.activity.get().setDestinationLocation(location);
		}
	}
}
