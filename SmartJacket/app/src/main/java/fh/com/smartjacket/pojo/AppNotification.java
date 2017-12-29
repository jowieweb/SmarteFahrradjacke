package fh.com.smartjacket.pojo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by nils on 21.12.17.
 */

public class AppNotification {
	private String appName;
	private String appPackageName;
	private Drawable appIcon;

	public AppNotification(String appName, String appPackageName, Drawable appIcon) {
		this.appName = appName;
		this.appPackageName = appPackageName;
		this.appIcon = appIcon;
	}

	public AppNotification(String appPackageName) {
		this.appPackageName = appPackageName;
	}

	/**
	 * Retrieves name and icon of the app.
	 * @param context Context
	 */
	public boolean restoreData(Context context) {
		if (context == null) {
			return false;
		}

		PackageManager pkgMan = context.getPackageManager();
		Intent intent = pkgMan.getLaunchIntentForPackage(this.appPackageName);
		if (intent == null) {
			return false;
		}

		try {
			this.appIcon = pkgMan.getActivityIcon(intent);

			ApplicationInfo info = pkgMan.getApplicationInfo(this.appPackageName, 0);
			if (info != null) {
				this.appName = (String) pkgMan.getApplicationLabel(info);
			}

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return true;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppPackageName() {
		return this.appPackageName;
	}

	public void setAppPackageName(String appPackageName) {
		this.appPackageName = appPackageName;
	}

	public Drawable getAppIcon() {
		return this.appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
}
