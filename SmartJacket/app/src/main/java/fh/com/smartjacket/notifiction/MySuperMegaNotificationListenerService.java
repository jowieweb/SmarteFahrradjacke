package fh.com.smartjacket.notifiction;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by nils on 21.12.17.
 */

public class MySuperMegaNotificationListenerService extends NotificationListenerService {
	private static final String LOG_TAG = "MyNotificationListener";

	public MySuperMegaNotificationListenerService(){
		Log.i(LOG_TAG, "STARTED!");
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.d(LOG_TAG, "id: " + sbn.getId() + ", Package Name: " + sbn.getPackageName() + ", Post time: " + sbn.getPostTime() + ", Tag: " + sbn.getTag());

		Intent intent = new Intent("fh.com.smartjacket");
		intent.putExtra("notification_package_name", sbn.getPackageName());

		sendBroadcast(intent);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification statusBarNotification) {

	}

	@Override
	public void onListenerConnected(){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
			getActiveNotifications();
		Log.i(LOG_TAG, "CONNECTED!");
	}
	@TargetApi(Build.VERSION_CODES.N)
	@Override
	public void onListenerDisconnected(){
		Log.i(LOG_TAG, "DISCONNECTED!");
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
			requestRebind(new ComponentName(this,NotificationListenerService.class));
	}
}
