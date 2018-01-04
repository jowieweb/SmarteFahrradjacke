package fh.com.smartjacket.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import fh.com.smartjacket.Mapquest.Mapquest;
import fh.com.smartjacket.R;
import fh.com.smartjacket.activity.AppChooserActivity;
import fh.com.smartjacket.activity.MainActivity;
import fh.com.smartjacket.activity.NotificationConfigActivity;
import fh.com.smartjacket.activity.RouteChooserActivity;
import fh.com.smartjacket.adapter.AppNotificationListAdapter;
import fh.com.smartjacket.listener.OnAppChosenListener;
import fh.com.smartjacket.listener.OnNotificationChangeListener;
import fh.com.smartjacket.pojo.AppNotification;
import fh.com.smartjacket.pojo.AppNotificationComparator;
import fh.com.smartjacket.pojo.HomeAddress;

/**
 *
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, OnAppChosenListener, OnNotificationChangeListener {
	private static final int PICK_APP_REQUEST = MainActivity.PICK_APP_REQUEST;
	private AppNotificationListAdapter adapter;
	private ArrayList<AppNotification> apps = new ArrayList<>();
	private EditText address;
	private EditText houseNumber;
	private EditText postcode;
	private Location currentLocation = new Location("");

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

		address = view.findViewById(R.id.address);
		houseNumber = view.findViewById(R.id.hausnumber);
		postcode = view.findViewById(R.id.postcode);
		postcode.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) ->{
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				saveHomeAddress();
			}
			return false;
		});

		ImageButton useCurrentLocationAsHome = view.findViewById(R.id.fragmentSettingsChooseCurrentLocation);
		useCurrentLocationAsHome.setOnClickListener((View view1) -> {
			if(currentLocation.getLatitude() == 0 && currentLocation.getLongitude() == 0){
				return;
			}
			GetAddressFromLocationTask task = new GetAddressFromLocationTask();
			task.execute(currentLocation);



		});

		ListView appListView = view.findViewById(R.id.fragmentSettingsAppNotificationListView);
		loadNotificationAppList();
		this.adapter = new AppNotificationListAdapter(getActivity(), this.apps);
		appListView.setAdapter(this.adapter);
		appListView.setOnItemClickListener(((adapterView, view1, i, l) -> openNotificationConfigActivity((AppNotification) adapterView.getItemAtPosition(i))));
		appListView.setOnItemLongClickListener((adapterView, view1, i, l) -> removeSelectedAppFromList((AppNotification) adapterView.getItemAtPosition(i)));

		MainActivity mainActivity = (MainActivity) getActivity();
		mainActivity.setOnAppChosenListener(this);
		mainActivity.setOnNotificationChangeListener(this);

		loadHomeAddress();
		return view;
	}
	public void setCurrentLocation(Location currentLocation){
		this.currentLocation = currentLocation;
	}

	private boolean openNotificationConfigActivity(AppNotification app) {
		Intent intent = new Intent(getActivity(), NotificationConfigActivity.class);

		intent.putExtra(getString(R.string.intent_extra_selected_app), app.getAppPackageName());
		intent.putExtra(getString(R.string.intent_extra_vibration_pattern), app.getVibrationPatternIndex());

		getActivity().startActivityForResult(intent, MainActivity.CONFIG_NOTIFICAION_REQUEST);

		return true;
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
			String appString = app.getAppPackageName() + ";" + app.getVibrationPatternIndex();
			packageNames.add(appString);
		}

		editor.putStringSet(getString(R.string.shared_preferences_app_notification_list), packageNames);

		editor.apply();
	}

	/**
	 * Loads and restores the notification app list. Read each package name and tries to retrieve icon and app name from the package name.
	 */
	private void loadNotificationAppList() {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		Set<String> appNotificationSettings = sharedPreferences.getStringSet(getString(R.string.shared_preferences_app_notification_list), null);

		if (appNotificationSettings != null) {
			this.apps.clear();

			for (String setting : appNotificationSettings) {
				String settings[] = setting.split(";");

				AppNotification appNotification = new AppNotification(settings[0]);
				if (settings.length > 1) {
					appNotification.setVibrationPatternIndex(Integer.parseInt(settings[1]));
				}

				if (appNotification.restoreData(getActivity())) {
					// Only add app to list if it exists -> users can delete apps, so we must check if it still exists
					this.apps.add(appNotification);
				}
			}

			Collections.sort(this.apps, new AppNotificationComparator());
		}
	}

	private void saveHomeAddress(){
		SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		HomeAddress ha = new HomeAddress(address.getText().toString(), houseNumber.getText().toString(), postcode.getText().toString());
		editor.putString(getString(R.string.shared_preferences_home_address), ha.toJsonString());
		editor.apply();
		Toast.makeText(getActivity(),"Home address saved", Toast.LENGTH_SHORT).show();
	}

	public HomeAddress loadHomeAddress() {
		SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		String  jString = sharedPreferences.getString(getString(R.string.shared_preferences_home_address), null);
		HomeAddress ha = new HomeAddress(jString);
		address.setText(ha.getAddress());
		houseNumber.setText(ha.getHausnumber());
		postcode.setText(ha.getPostcode());
		return ha;
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

	@Override
	public void onNotificationChange(AppNotification app) {
		for (int i = 0; i < this.apps.size(); i++) {
			if (this.apps.get(i).getAppPackageName().equals(app.getAppPackageName())) {

				this.apps.set(i, app);
				this.adapter.notifyDataSetChanged();
				saveNotificationAppList();

				return;
			}
		}
	}


	private class GetAddressFromLocationTask extends AsyncTask<Location, Void, String> {

		@Override
		protected String doInBackground(Location... locations) {
			return Mapquest.getAddressFromLocation(locations[0]);
		}

		@Override
		protected void onPostExecute(String pAddress) {
			if (pAddress != null && !pAddress.isEmpty()) {
				Log.e("SETTINGSFRAGMENT", pAddress);

				String[] addressAndPlz = pAddress.split(",");
				String[] addressAndHN = addressAndPlz[0].split(" ");

				String hausnumber = "";
				String localAddress ="";
				String plz = addressAndPlz[addressAndPlz.length -1];
				for(int i =0;i< addressAndHN.length;i++){
					try {
						int temp = Integer.parseInt(addressAndHN[i]);
						hausnumber = ""+ temp;
					}catch (Exception e){
						if(localAddress.length()> 0){
							localAddress += " ";
						}
						localAddress += addressAndHN[i];
					}
				}

				postcode.setText(plz);
				address.setText(localAddress);
				houseNumber.setText(hausnumber);

				saveHomeAddress();
			}
		}
	}
}
