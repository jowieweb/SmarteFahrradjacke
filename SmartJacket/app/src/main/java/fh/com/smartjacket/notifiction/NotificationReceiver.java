package fh.com.smartjacket.notifiction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fh.com.smartjacket.listener.OnNotificationListener;

/**
 * Created by nils on 30.12.17.
 */

public class NotificationReceiver extends BroadcastReceiver {
	private OnNotificationListener onNotificationListener;

	public NotificationReceiver(OnNotificationListener onNotificationListener) {
		this.onNotificationListener = onNotificationListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (this.onNotificationListener != null) {
			String packageName = intent.getStringExtra("notification_package_name");

			this.onNotificationListener.onNotification(packageName);
		}
	}
}
