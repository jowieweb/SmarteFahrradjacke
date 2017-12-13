package fh.com.smartjacket.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
	private OnFragmentInteractionListener onFragmentInteractionListener;
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

		FloatingActionButton fab = view.findViewById(R.id.addRouteActionButton);
		fab.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (onFragmentInteractionListener != null) {
					onFragmentInteractionListener.onAddRouteButtonClicked();
				}
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
