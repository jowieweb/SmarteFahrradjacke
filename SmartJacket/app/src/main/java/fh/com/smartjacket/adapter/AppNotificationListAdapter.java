package fh.com.smartjacket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fh.com.smartjacket.R;
import fh.com.smartjacket.pojo.AppVibrationConfig;


/**
 * Created by nils on 21.12.17.
 */

public class AppNotificationListAdapter extends ArrayAdapter<AppVibrationConfig> {
	private ArrayList<AppVibrationConfig> dataSet;
	private Context context;

	public AppNotificationListAdapter(Context context, ArrayList<AppVibrationConfig> data) {
		super(context, R.layout.app_notification_list_item, data);
		this.context = context;
		this.dataSet = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppVibrationConfig config = getItem(position);

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(this.context);
			convertView = inflater.inflate(R.layout.app_notification_list_item, parent, false);
		}

		TextView appNameTextView = convertView.findViewById(R.id.app_notification_list_item_app_name);
		appNameTextView.setText(config.getAppName());

		return convertView;
	}
}
