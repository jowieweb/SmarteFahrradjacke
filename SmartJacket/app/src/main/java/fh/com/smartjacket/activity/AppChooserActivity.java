package fh.com.smartjacket.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fh.com.smartjacket.R;
import fh.com.smartjacket.adapter.AppListAdapter;
import fh.com.smartjacket.pojo.AppNotification;
import fh.com.smartjacket.pojo.AppNotificationComparator;

public class AppChooserActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_chooser);

		Toolbar toolbar = findViewById(R.id.activity_app_chooser_toolbar);
		ListView appListView = findViewById(R.id.activity_app_chooser_list_view);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Bitte App auswählen");

		ArrayList<AppNotification> installedApps = getInstalledApps();
		Collections.sort(installedApps, new AppNotificationComparator());

		appListView.setAdapter(new AppListAdapter(this, installedApps));
		appListView.setOnItemClickListener((adapterView, view, i, l) -> returnSelectedApp((AppNotification) adapterView.getItemAtPosition(i)));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void returnSelectedApp(AppNotification app) {
		Intent intent = new Intent();
		intent.putExtra(getString(R.string.intent_extra_selected_app), app.getAppPackageName());
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	/**
	 * Gets a list of all installed apps and filters out system apps.
	 * @return List of installed apps without system apps.
	 */
	private ArrayList<AppNotification> getInstalledApps() {
		ArrayList<AppNotification> apps = new ArrayList<>();
		PackageManager pkgMan = getPackageManager();
		String thisPackageName = getPackageName();

		List<PackageInfo> installedApps = pkgMan.getInstalledPackages(PackageManager.GET_META_DATA);
		Drawable defaultAppIcon = pkgMan.getDefaultActivityIcon();

		for (PackageInfo pkgInfo : installedApps) {

			// Skip own app
			if (pkgInfo.packageName.equals(thisPackageName)) {
				continue;
			}

			Intent appIntent = pkgMan.getLaunchIntentForPackage(pkgInfo.packageName);
			if (appIntent == null) {
				continue;
			}

			try {
				Drawable appIcon = pkgMan.getActivityIcon(appIntent);
				ApplicationInfo appInfo = pkgMan.getApplicationInfo(pkgInfo.packageName, 0);

				if (appIcon != null && !appIcon.equals(defaultAppIcon) && appInfo != null) {
					String appLabel = (String) pkgMan.getApplicationLabel(appInfo);
					apps.add(new AppNotification(appLabel, pkgInfo.packageName, appIcon));
				}

			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		return apps;
	}
}
