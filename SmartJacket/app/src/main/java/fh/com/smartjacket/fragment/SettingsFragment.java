package fh.com.smartjacket.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
		loadNotificationAppList();
		this.adapter = new AppNotificationListAdapter(getActivity(), this.apps);
		appListView.setAdapter(this.adapter);
		appListView.setOnItemLongClickListener((adapterView, view1, i, l) -> removeSelectedAppFromList((AppNotification) adapterView.getItemAtPosition(i)));

		((MainActivity)getActivity()).setOnAppChosenListener(this);

		return view;
	}

	private boolean removeSelectedAppFromList(AppNotification app) {
		this.apps.remove(app);
		this.adapter.notifyDataSetChanged();

		saveNotificationAppList();

		return true;
	}

	private void saveNotificationAppList() {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		HashSet<String> pakageNames = new HashSet<>();
		for (AppNotification app : this.apps) {
			pakageNames.add(app.getAppPackageName());
		}

		editor.putStringSet("app_notifications", pakageNames);

		editor.commit();
	}

	private void loadNotificationAppList() {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		Set<String> pakageNames = sharedPreferences.getStringSet("app_notifications", null);

		if (pakageNames != null) {
			this.apps.clear();

			for (String pkg : pakageNames) {
				AppNotification appNotification = new AppNotification(pkg);

				if (appNotification.restoreData(getActivity())) {
					// Only add app to list if it exists -> users can delete apps, so we must check if it still exists
					this.apps.add(appNotification);
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent(getActivity(), AppChooserActivity.class);
		getActivity().startActivityForResult(intent, PICK_APP_REQUEST);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void OnAppChosen(AppNotification app) {
		this.apps.add(app);
		this.adapter.notifyDataSetChanged();

		saveNotificationAppList();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
