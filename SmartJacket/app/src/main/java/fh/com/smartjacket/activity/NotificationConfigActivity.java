package fh.com.smartjacket.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fh.com.smartjacket.R;
import fh.com.smartjacket.pojo.AppNotification;

public class NotificationConfigActivity extends AppCompatActivity {
	private AppNotification app;
	private Spinner vibrationPatternSpinner;
	private ArrayList<JSONObject> jsonPattern = new ArrayList<>();
	private TextView description;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_config);

		Toolbar toolbar = findViewById(R.id.notification_config_activity_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Benachrichtigungen");

		Intent intent = getIntent();
		this.app = new AppNotification(intent.getStringExtra(getString(R.string.intent_extra_selected_app)));
		this.app.setVibrationPatternIndex(intent.getIntExtra(getString(R.string.intent_extra_vibration_pattern), AppNotification.DEFAULT_VIBRATION_PATTERN_INDEX));
		if (this.app.restoreData(this)) {
			ImageView appIconView = findViewById(R.id.notification_config_activity_app_icon_image_view);
			TextView appNameTextView = findViewById(R.id.notification_config_activity_app_name_text_view);

			appIconView.setImageDrawable(this.app.getAppIcon());
			appNameTextView.setText(this.app.getAppName());
		}

		String vibrationPattern[] = getResources().getStringArray(R.array.vibration_pattern);

		ArrayList<String> vibrationPatternLabels = new ArrayList<>();
		for (int i = 0; i <  vibrationPattern.length; i++) {
			try {
				JSONObject jpattern = new JSONObject(vibrationPattern[i]);
				vibrationPatternLabels.add("Muster " +  jpattern.getString("name"));
				jsonPattern.add(jpattern);
			}catch (Exception e){

			}
		}

		this.vibrationPatternSpinner = findViewById(R.id.notification_config_activity_vibration_pattern_spinner);
		this.description = findViewById(R.id.notification_config_activity_vibration_pattern_description);

		ArrayAdapter<String> patternSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, vibrationPatternLabels);
		this.vibrationPatternSpinner.setAdapter(patternSpinnerAdapter);

		int vibrationPatternIndex = this.app.getVibrationPatternIndex();
		if (vibrationPatternIndex >= vibrationPattern.length) {
			vibrationPatternIndex = vibrationPattern.length - 1;
		}
		this.vibrationPatternSpinner.setSelection(vibrationPatternIndex);
		this.vibrationPatternSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		   @Override
		   public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			   setDescription();
		   }

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		Button patternTestButton = findViewById(R.id.notification_config_activity_vibration_test_button);
		patternTestButton.setOnClickListener((View view) -> onTestPatternButtonClicked());
	}

	private void onTestPatternButtonClicked() {
		// TODO: Send pattern to device
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		ArrayList<Long> vibrationlongs  = new ArrayList<Long>();
		JSONObject json = jsonPattern.get(this.vibrationPatternSpinner.getSelectedItemPosition());
		try{
			JSONArray parts = json.getJSONArray("parts");
			for (int i=0; i < parts.length(); i++) {
				JSONObject inner = parts.getJSONObject(i);
				vibrationlongs.add(inner.getLong("off"));
				vibrationlongs.add(inner.getLong("on"));
			}
		}catch (Exception e){}

		/*convert Long to long  -.- */
		long[] longs = new long[vibrationlongs.size()+1];
		for(int i = 0;i< vibrationlongs.size();i++) {
			longs[i] = vibrationlongs.get(i);
		}


		v.vibrate(longs, -1);
	}

	private void setDescription(){
		JSONObject json = jsonPattern.get(this.vibrationPatternSpinner.getSelectedItemPosition());
		this.description.setText("");
		String toSet = "";
		try {
			JSONArray parts = json.getJSONArray("parts");
			for (int i = 0; i < parts.length(); i++) {
				int off = parts.getJSONObject(i).getInt("off");
				int on = parts.getJSONObject(i).getInt("on");
				if( off != 0){
					toSet += ", dann " + off + "ms aus";
				}
				if(toSet.length() == 0){
					toSet += "Zuerst " + on + "ms an";
				} else {
					toSet += ", dann " + on + "ms an";
				}
			}
		}catch (Exception e){

		}
		this.description.setText(toSet);
	}

	private void endActivity() {
		Intent intent = new Intent();

		intent.putExtra(getString(R.string.intent_extra_selected_app), this.app.getAppPackageName());
		intent.putExtra(getString(R.string.intent_extra_vibration_pattern), this.vibrationPatternSpinner.getSelectedItemId());

		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				endActivity();
				return  true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		endActivity();
		super.onBackPressed();
	}
}
