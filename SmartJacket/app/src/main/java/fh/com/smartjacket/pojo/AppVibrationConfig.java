package fh.com.smartjacket.pojo;

/**
 * Created by nils on 21.12.17.
 */

public class AppVibrationConfig {
	private String appName;

	public AppVibrationConfig(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
