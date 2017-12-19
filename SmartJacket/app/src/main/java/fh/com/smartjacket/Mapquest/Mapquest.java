package fh.com.smartjacket.Mapquest;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jowie on 04.12.2017.
 */

public class Mapquest {
    private static final String key = "YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7";
    private static final String urlPrefix = "https://www.mapquestapi.com/directions/v2/route?key=YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7&from=";
    private static final String suffix = "&outFormat=json&ambiguities=ignore&routeType=bicycle&doReverseGeocode=false&enhancedNarrative=false&avoidTimedConditions=false";
    private static final String STATIC_MAP_API_BASE_URL = "https://www.mapquestapi.com/staticmap/v5/map";
    private static final String GEOCODING_API_BASE_URL = "https://www.mapquestapi.com/geocoding/v1/address";

    private ArrayList<TurnPoint> turnPoints = new ArrayList<>();


    private String getURL(String from, String to) {
        return urlPrefix + from + "&to=" + to + suffix;
    }


    private String getExampleURL() {
        return "https://www.mapquestapi.com/directions/v2/route?key=YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7&from=Artilleriestra%C3%9Fe+15%2C+32427+Minden%2C+Germany&to=Ringstra%C3%9Fe+111%2C+32427+Minden%2C+Germany&outFormat=json&ambiguities=ignore&routeType=bicycle&doReverseGeocode=false&enhancedNarrative=false&avoidTimedConditions=false";
    }

    public static String getCoordinatesFromAddressRequestUrl(String search) {
        return GEOCODING_API_BASE_URL + "?key=" + key + "&location=" + search;
    }

    public static String getStaticMapApiUrlForLocation(Location location) {
        return STATIC_MAP_API_BASE_URL + "?key=" + key + "&center=" + location.getLatitude() + "," + location.getLongitude() + "&zoom=15";
    }

    /**
     * Searches for the coordinates of a given address.
     * This method blocks! Run it in an extra thread.
     * @param address Address
     * @return Location or null, if no location could be found.
     */
    public static Location getLocationFromAddress(String address) {
        try {
            URL url = new URL(getCoordinatesFromAddressRequestUrl(address));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            br.close();

            String jsonResult = sb.toString();
            JSONObject json = new JSONObject(jsonResult);
            JSONArray resultArray = json.getJSONArray("results");

            if (resultArray.length() > 0) {
                JSONArray jsonLocations = resultArray.getJSONObject(0).getJSONArray("locations");

                if (jsonLocations.length() > 0) {
                    JSONObject jsonLatLng = jsonLocations.getJSONObject(0).getJSONObject("latLng");

                        double lat = jsonLatLng.getDouble("lat");
                        double lng = jsonLatLng.getDouble("lng");

                        Location coords = new Location("dummyprovider");

                        coords.setLatitude(lat);
                        coords.setLongitude(lng);

                        return coords;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<TurnPoint> getTurnPoints(String text) {
        ArrayList<TurnPoint> turnPoints = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(text);
            JSONObject route = json.getJSONObject("route");
            JSONArray legs = route.getJSONArray("legs");
            JSONArray maneuvers = legs.getJSONObject(0).getJSONArray("maneuvers");

            for (int i = 0; i < maneuvers.length(); i++) {
                turnPoints.add(new TurnPoint(maneuvers.getJSONObject(i)));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return turnPoints;
    }


    public String debug() {
        String retval = "";
        RetrieveContentTask rct = new RetrieveContentTask();
        try {
            retval = rct.execute(getExampleURL()).get();

        } catch (Exception e) {

        }
        return retval;
    }


    public ArrayList<TurnPoint>  debugTurnPoints(){
        String json = debug();
        return  getTurnPoints(json);
    }


}
