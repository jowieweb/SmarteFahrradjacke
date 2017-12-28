package fh.com.smartjacket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fh.com.smartjacket.R;
import fh.com.smartjacket.pojo.AppNotification;

/**
 * Created by nils on 28.12.17.
 */

public class AppListAdapter extends ArrayAdapter<AppNotification> {
	private ArrayList<AppNotification> dataSet;
	private Context context;

	public AppListAdapter(Context context, ArrayList<AppNotification> data) {
		super(context, R.layout.app_list_item, data);

		this.context = context;
		this.dataSet = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppNotification app = getItem(position);

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(this.context);
			convertView = inflater.inflate(R.layout.app_list_item, parent, false);
		}

		TextView appNameTextView = convertView.findViewById(R.id.app_list_item_app_name);
		ImageView appIconImageView = convertView.findViewById(R.id.app_list_item_app_icon_image_view);

		appNameTextView.setText(app.getAppName());
		appIconImageView.setImageBitmap(app.getAppIcon());

		return convertView;
	}
}

