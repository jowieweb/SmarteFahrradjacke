package fh.com.smartjacket.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.MapQuestAccountManager;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;

import java.util.Random;

import fh.com.smartjacket.Mapquest.LocationChangeListener;
import fh.com.smartjacket.Mapquest.Mapquest;
import fh.com.smartjacket.Mapquest.Route;
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
	private Location currentLocation = new Location("");

	public  Location locationToNavigate = null;
	private PolylineOptions routePolyline = null;
	private Marker destinationMarker = null;
	private Marker currentLocationMarker = null;

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
		this.mapView.getMapAsync((MapboxMap mapboxMap) -> {
			this.mapboxMap = mapboxMap;
			if (currentLocation != null) {
				onLocationChange(currentLocation);
			}
		});

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

	}

	/**
	 * set the new destination
	 * @param loc the location
	 * @param destinationName the name. displayed on the map marker
	 */
	public void setNewDestination(Location loc, String destinationName){
		locationToNavigate = loc;
		/* check for a valid destination and current location */
		if(mapboxMap == null){
			return;
		}
		if(destinationMarker != null)
			mapboxMap.removeMarker(destinationMarker);

		if(routePolyline != null)
			mapboxMap.removePolyline(routePolyline.getPolyline());

		if(currentLocation.getLongitude() == 0 && currentLocation.getLatitude()==0){
			Toast.makeText(this.getContext(), "NO POSITION FOUND!", Toast.LENGTH_SHORT).show();
			return;
		}

		/* create the marker */
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position( new LatLng(locationToNavigate.getLatitude(), locationToNavigate.getLongitude()));
		markerOptions.title("Destination");
		markerOptions.snippet(destinationName);
		destinationMarker = mapboxMap.addMarker(markerOptions);

		/* create the route */
		Mapquest mq = new Mapquest();
		Route r =mq.getRoute(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), new LatLng(locationToNavigate.getLatitude(),locationToNavigate.getLongitude()));
		if( r == null)
		{
			Toast.makeText(this.getContext(), "ROUTE IS NULL!", Toast.LENGTH_SHORT).show();
		}

		/* create the polyline */
		PolylineOptions polyline = new PolylineOptions();
		polyline.addAll(r.getShape()).width(5).color(Color.BLUE).alpha((float)0.75);
		routePolyline = polyline;
		mapboxMap.addPolyline(polyline);

		/*move the camera */
		LatLng middel = r.midPoint();
		Log.i(LOG_TAG, "Distance: " + r.getDistance() + " ZOOM Level" + r.getZoomlevel());
		mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middel,r.getZoomlevel()));


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
		if(location == null){
			return;
		}
		currentLocation = location;
		Log.d(LOG_TAG, "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());

		if(getActivity() == null)
			return;
		if( this.mapboxMap == null)
			return;

		mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));


		if(currentLocationMarker != null){
			mapboxMap.removeMarker(currentLocationMarker);
		}

		MarkerOptions options = new MarkerOptions();
		options.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
		Bitmap test = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.currentlocation);
		options.setIcon(IconFactory.recreate("a",test));
		currentLocationMarker = mapboxMap.addMarker(options);

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
