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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fh.com.smartjacket.R;
import fh.com.smartjacket.adapter.AppListAdapter;
import fh.com.smartjacket.pojo.AppNotification;

public class AppChooserActivity extends AppCompatActivity {
	private ArrayList<AppNotification> installedApps;
	private static final String LOG_TAG ="APPCHOOSERACTIVITY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_chooser);

		Toolbar toolbar = findViewById(R.id.activity_app_chooser_toolbar);
		ListView appListView = findViewById(R.id.activity_app_chooser_list_view);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Bitte App auswählen");

		this.installedApps = getInstalledApps();
		appListView.setAdapter(new AppListAdapter(this, this.installedApps));
		appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Log.d(LOG_TAG,"CLICK " + i + " " + installedApps.get(i).getAppName());
				Intent data = new Intent();
				Bundle b = new Bundle();

				b.putSerializable("AppNotification", installedApps.get(i));
				data.putExtras(b);
				if (getParent() == null) {
					setResult(Activity.RESULT_OK, data);
				} else {
					getParent().setResult(Activity.RESULT_OK, data);
				}
				finish();
			}
		});
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
