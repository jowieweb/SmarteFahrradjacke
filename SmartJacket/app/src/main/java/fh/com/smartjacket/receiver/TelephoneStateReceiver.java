package fh.com.smartjacket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;

import fh.com.smartjacket.listener.OnIncomingCallListener;

/**
 * Created by nils on 04.01.18.
 */

public class TelephoneStateReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = "TelephoneStateReceiver";
	private OnIncomingCallListener onIncomingCallListener;

	public TelephoneStateReceiver(OnIncomingCallListener onIncomingCallListener) {
		this.onIncomingCallListener = onIncomingCallListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			Log.d(LOG_TAG, "Incoming call!");

			if (this.onIncomingCallListener != null) {
				this.onIncomingCallListener.onIncomingCall();
			}
		}
	}

}
