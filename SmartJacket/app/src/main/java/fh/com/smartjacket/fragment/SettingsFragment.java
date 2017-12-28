package fh.com.smartjacket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import fh.com.smartjacket.R;
import fh.com.smartjacket.activity.AppChooserActivity;
import fh.com.smartjacket.activity.MainActivity;
import fh.com.smartjacket.adapter.AppNotificationListAdapter;
import fh.com.smartjacket.listener.OnAppChosenListener;
import fh.com.smartjacket.pojo.AppNotification;

/**
 *
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, OnAppChosenListener {
	private static final int PICK_APP_REQUEST = 1338;
	private AppNotificationListAdapter adapter;
	private ArrayList<AppNotification> apps = new ArrayList<>();

	public SettingsFragment() {
		// Required empty public constructor
		//this.apps.add(new AppNotification("Peter"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_settings, container, false);

		Button chooseAppButton = view.findViewById(R.id.fragmentSettingsChooseAppButton);
		chooseAppButton.setOnClickListener(this);

		ListView appListView = view.findViewById(R.id.fragmentSettingsAppNotificationListView);
		this.adapter = new AppNotificationListAdapter(getActivity(), this.apps);
		appListView.setAdapter(this.adapter);

		((MainActivity)getActivity()).setOnAppChosenListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent(getActivity(), AppChooserActivity.class);

		getActivity().startActivityForResult(intent, PICK_APP_REQUEST);
	}

	@Override
	public void OnAppChosen(AppNotification app) {
		this.apps.add(app);
		this.adapter.notifyDataSetChanged();
	}
}
