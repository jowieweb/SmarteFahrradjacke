package fh.com.smartjacket.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import fh.com.smartjacket.R;
import fh.com.smartjacket.activity.AppChooserActivity;
import fh.com.smartjacket.activity.MainActivity;
import fh.com.smartjacket.adapter.AppNotificationListAdapter;
import fh.com.smartjacket.listener.OnAppChosenListener;
import fh.com.smartjacket.pojo.AppNotification;
import fh.com.smartjacket.pojo.AppNotificationComparator;

/**
 *
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, OnAppChosenListener {
	private static final int PICK_APP_REQUEST = 1338;
	private AppNotificationListAdapter adapter;
	private ArrayList<AppNotification> apps = new ArrayList<>();

	public SettingsFragment() {
		// Required empty public constructor
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

	/**
	 * Saves the notification app list. Only the package name of each app is saved.
	 */
	private void saveNotificationAppList() {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		HashSet<String> packageNames = new HashSet<>();
		for (AppNotification app : this.apps) {
			packageNames.add(app.getAppPackageName());
		}

		editor.putStringSet(getString(R.string.shared_preferences_app_notification_list), packageNames);

		editor.apply();
	}

	/**
	 * Loads and restores the notification app list. Read each package name and tries to retrieve icon and app name from the package name.
	 */
	private void loadNotificationAppList() {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		Set<String> packageNames = sharedPreferences.getStringSet(getString(R.string.shared_preferences_app_notification_list), null);

		if (packageNames != null) {
			this.apps.clear();

			for (String pkg : packageNames) {
				AppNotification appNotification = new AppNotification(pkg);

				if (appNotification.restoreData(getActivity())) {
					// Only add app to list if it exists -> users can delete apps, so we must check if it still exists
					this.apps.add(appNotification);
				}
			}

			Collections.sort(this.apps, new AppNotificationComparator());
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
		if (isAppInList(app)) {
			Toast.makeText(getActivity(), "App ist schon vorhanden.", Toast.LENGTH_SHORT).show();

		} else {
			this.apps.add(app);

			// Sort app notification list. After loading the list from SharedPreferences, the list is unordered. So we should sort the list after
			// adding an element to have a more consistent user experience. :P
			Collections.sort(this.apps, new AppNotificationComparator());

			this.adapter.notifyDataSetChanged();

			saveNotificationAppList();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	/**
	 * Checks if a given app is in the notification app list.
	 * @param app App
	 * @return true if app is in notification app list, false otherwise.
	 */
	private boolean isAppInList(AppNotification app) {
		for (AppNotification a: this.apps) {
			if (a.getAppPackageName().equals(app.getAppPackageName())) {
				return true;
			}
		}

		return false;
	}

	public ArrayList<AppNotification> getAppNotificationList() {
		return this.apps;
	}
}
