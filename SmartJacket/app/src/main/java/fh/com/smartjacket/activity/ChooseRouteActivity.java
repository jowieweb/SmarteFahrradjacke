package fh.com.smartjacket.activity;

import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import fh.com.smartjacket.Mapquest.GoogleMapsSearch;
import fh.com.smartjacket.R;

public class ChooseRouteActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
	private static final String LOG_TAG = "ChooseRouteActivity";
	private TextView currentPositionTextView;
	private TextView searchTextView;
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


		});


		searchTextView.setOnKeyListener((View view, int i, KeyEvent keyEvent) -> {
			gms.suggest(searchTextView.getText().toString(), lambdaLoc);
			return  false;
		});




		Button startNavigationButton = findViewById(R.id.chooseRouteActivityStartNavigationButton);
		startNavigationButton.setOnClickListener((View view) -> {
			// TODO: Get address information and start navigation
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
