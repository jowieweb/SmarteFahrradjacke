package fh.com.smartjacket.pojo;

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
