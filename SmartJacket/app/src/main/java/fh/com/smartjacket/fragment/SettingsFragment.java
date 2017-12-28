package fh.com.smartjacket.fragment;

import android.content.Context;
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
import fh.com.smartjacket.pojo.AppNotification;

/**
 *
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

	private OnFragmentInteractionListener onFragmentInteractionListener;
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

		return view;
	}

	@Override
	public void onClick(View view) {
		this.onFragmentInteractionListener.onAddAppNotificationButtonClicked();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (context instanceof OnFragmentInteractionListener) {
			this.onFragmentInteractionListener = (OnFragmentInteractionListener) context;

		} else {
			throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.onFragmentInteractionListener = null;
	}



}
