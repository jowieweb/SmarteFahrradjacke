package fh.com.smartjacket.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapquest.mapping.MapQuestAccountManager;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.OnMapReadyCallback;

import fh.com.smartjacket.R;

/**
 * A Fragment that displays a MapQuest map with the current route to the destination.
 */
public class RouteFragment extends Fragment {
	private MapboxMap mapboxMap;
	private MapView mapView;

	public RouteFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MapQuestAccountManager.start(getActivity().getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_route, container, false);

		this.mapView = view.findViewById(R.id.mapquestMapView);
		this.mapView.onCreate(savedInstanceState);

		this.mapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(MapboxMap mapboxMap) {
				mapboxMap = mapboxMap;
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		this.mapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.mapView.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		this.mapView.onSaveInstanceState(outState);
	}
}
