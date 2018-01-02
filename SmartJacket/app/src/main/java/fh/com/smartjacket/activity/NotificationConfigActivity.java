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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import fh.com.smartjacket.R;
import fh.com.smartjacket.pojo.AppNotification;

public class NotificationConfigActivity extends AppCompatActivity {
	private AppNotification app;
	private Spinner vibrationPatternSpinner;

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
			vibrationPatternLabels.add("Muster " + (i + 1));
		}

		this.vibrationPatternSpinner = findViewById(R.id.notification_config_activity_vibration_pattern_spinner);
		ArrayAdapter<String> patternSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, vibrationPatternLabels);
		this.vibrationPatternSpinner.setAdapter(patternSpinnerAdapter);

		int vibrationPatternIndex = this.app.getVibrationPatternIndex();
		if (vibrationPatternIndex >= vibrationPattern.length) {
			vibrationPatternIndex = vibrationPattern.length - 1;
		}
		this.vibrationPatternSpinner.setSelection(vibrationPatternIndex);

		Button patternTestButton = findViewById(R.id.notification_config_activity_vibration_test_button);
		patternTestButton.setOnClickListener((View view) -> onTestPatternButtonClicked());
	}

	private void onTestPatternButtonClicked() {
		// TODO: Send pattern to device
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		int index = this.vibrationPatternSpinner.getSelectedItemPosition();
		String vibrationPattern[] = getResources().getStringArray(R.array.vibration_pattern);
		String pattern = vibrationPattern[index];
		String[] triplet = pattern.split(";");
		ArrayList<Long> parts  = new ArrayList<Long>();
		parts.add((long)0);
		for(String s:triplet){
			String[] subparts = s.split(",");
			long time = Long.parseLong(subparts[2]);
			parts.add(time);

			parts.add((long)50);
		}
		long[] longs = new long[parts.size()+1];

		for(int i = 0;i< parts.size();i++) {
			longs[i] = parts.get(i);
		}


		v.vibrate(longs, -1);
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
