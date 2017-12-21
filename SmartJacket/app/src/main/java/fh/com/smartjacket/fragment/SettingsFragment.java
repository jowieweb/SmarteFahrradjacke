package fh.com.smartjacket.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import fh.com.smartjacket.R;
import fh.com.smartjacket.adapter.AppNotificationListAdapter;
import fh.com.smartjacket.pojo.AppVibrationConfig;

/**
 *
 */
public class SettingsFragment extends Fragment {
	private AppNotificationListAdapter adapter;
	private ArrayList<AppVibrationConfig> apps = new ArrayList<>();

	public SettingsFragment() {
		// Required empty public constructor
		this.apps.add(new AppVibrationConfig("Peter"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_settings, container, false);

		ListView appListView = view.findViewById(R.id.fragmentSettingsAppNotificationListView);
		this.adapter = new AppNotificationListAdapter(getActivity(), this.apps);
		appListView.setAdapter(this.adapter);

		return view;
	}
}
