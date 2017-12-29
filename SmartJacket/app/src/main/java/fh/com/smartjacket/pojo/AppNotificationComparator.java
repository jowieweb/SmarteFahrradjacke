package fh.com.smartjacket.pojo;

import java.util.Comparator;

/**
 * Created by nils on 29.12.17.
 */

public class AppNotificationComparator implements Comparator<AppNotification> {
	@Override
	public int compare(AppNotification a1, AppNotification a2) {
		return a1.getAppName().compareTo(a2.getAppName());
	}
}
