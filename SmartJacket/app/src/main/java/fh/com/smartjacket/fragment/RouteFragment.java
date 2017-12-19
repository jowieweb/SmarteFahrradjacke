package fh.com.smartjacket.fragment;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.MapQuestAccountManager;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.OnMapReadyCallback;

import fh.com.smartjacket.Mapquest.LocationChangeListener;
import fh.com.smartjacket.R;
import fh.com.smartjacket.activity.MainActivity;

/**
 * A Fragment that displays a MapQuest map with the current route to the destination.
 */
public class RouteFragment extends Fragment implements LocationChangeListener {
	private static final String LOG_TAG = "RouteFragment";
	private OnFragmentInteractionListener onFragmentInteractionListener;
	private MapboxMap mapboxMap;
	private MapView mapView;
	//ugly af... aber wie komme ich an den intent den ich in ChooseRoute zurückwerfe?
	public static Location locationToNavigate = null;

	public RouteFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((MainActivity)getActivity()).setOnLocationListener(this);

		MapQuestAccountManager.start(getActivity().getApplicationContext());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_route, container, false);

		this.mapView = view.findViewById(R.id.mapquestMapView);
		this.mapView.onCreate(savedInstanceState);
		this.mapView.getMapAsync((MapboxMap mapboxMap) -> { this.mapboxMap = mapboxMap; } );

		FloatingActionButton fab = view.findViewById(R.id.addRouteActionButton);
		fab.setOnClickListener((View v) -> {
			if (onFragmentInteractionListener != null) {
				onFragmentInteractionListener.onAddRouteButtonClicked();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.mapView.onResume();
		if(locationToNavigate != null) {
			Log.i(LOG_TAG, "GEFUNDEN! " + locationToNavigate.toString());
			mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationToNavigate.getLatitude(), locationToNavigate.getLongitude()), 15));

			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position( new LatLng(locationToNavigate.getLatitude(), locationToNavigate.getLongitude()));
			markerOptions.title("Ziel");
			markerOptions.snippet("Ich bin zu faul rausfinden was die adresse war\nkönnen wir machen wenn der intent geht");
			mapboxMap.addMarker(markerOptions);
		}

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

	@Override
	public void onLocationChange(Location location) {
		if (this.mapboxMap != null) {
			mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
			this.mapboxMap.addMarker(new MarkerOptions().setPosition(new LatLng(location.getLatitude(), location.getLongitude())));
		}

		Log.d(LOG_TAG, "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		void onAddRouteButtonClicked();
	}
}
