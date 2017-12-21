package fh.com.smartjacket.Mapquest;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jowie on 04.12.2017.
 */

public class Mapquest {
    private static final String key = "YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7";
    private static final String urlPrefix = "https://www.mapquestapi.com/directions/v2/route?key=YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7&from=";
    private static final String suffix = "&outFormat=json&ambiguities=ignore&routeType=bicycle&generalize=0";
    private static final String STATIC_MAP_API_BASE_URL = "https://www.mapquestapi.com/staticmap/v5/map";
    private static final String GEOCODING_API_BASE_URL = "https://www.mapquestapi.com/geocoding/v1/address";
    private static final String REVERSE_GEOCODING_API_BASE_URL = "https://www.mapquestapi.com/geocoding/v1/reverse";

    private ArrayList<TurnPoint> turnPoints = new ArrayList<>();


    private String getURL(String from, String to) {
        return urlPrefix + from + "&to=" + to + suffix;
    }


    private String getExampleURL() {
        return "https://www.mapquestapi.com/directions/v2/route?key=YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7&from=Artilleriestra%C3%9Fe+15%2C+32427+Minden%2C+Germany&to=Ringstra%C3%9Fe+111%2C+32427+Minden%2C+Germany&outFormat=json&ambiguities=ignore&routeType=bicycle&generalize=0";
    }

    public static String getCoordinatesFromAddressRequestUrl(String search) {
        return GEOCODING_API_BASE_URL + "?key=" + key + "&location=" + search;
    }

    public static String getStaticMapApiUrlForLocation(Location location) {
        return STATIC_MAP_API_BASE_URL + "?key=" + key + "&center=" + location.getLatitude() + "," + location.getLongitude() +
                "&zoom=16&shape=radius:0.01km|fill:ff0000|border:000000|" + location.getLatitude() + "," + location.getLongitude();
    }

    public Route getRoute(LatLng from, LatLng to){
        String retval = getURL(from.getLatitude() + "%2C" + from.getLongitude(), to.getLatitude() +"%2C" +to.getLongitude());
        RetrieveContentTask rct = new RetrieveContentTask();
        try {
            retval = rct.execute(getExampleURL()).get();
            return paraseRoute(retval);
        } catch (Exception e) {

        }

        return null;
    }

    private Route paraseRoute(String text){
        ArrayList<TurnPoint> turnPoints = getTurnPoints(text);
        try{
            JSONArray points = new JSONObject(text)
                    .getJSONObject("route")
                    .getJSONObject("shape")
                    .getJSONArray("shapePoints");

            // get every other shape point
            int pointcount = points.length() / 2;

            // create a shape point list
            ArrayList<LatLng> shapePoints = new ArrayList<>();

            // fill list with every even value as lat and odd value as lng
            for (int point = 0; point < pointcount; point = point + 1) {
                shapePoints.add(new LatLng(
                        (double) points.get(point * 2),
                        (double) points.get(point * 2 + 1)
                ));
            }
            return new Route(turnPoints, shapePoints);
        }
        catch (Exception e)     {}
        return  null;

    }

    private static String downloadStringFromUrl(String url) {
        String result = null;

        try {
            URL u = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) u.openConnection();

            connection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            br.close();
            result = sb.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

    public static String getAddressFromLocation(Location location) {
        String address = "";

        String lat = String.format(Locale.US, "%.6f", location.getLatitude());
        String lng = String.format(Locale.US, "%.6f", location.getLongitude());

        String url = REVERSE_GEOCODING_API_BASE_URL + "?key=" + key + "&location=" + lat + "," + lng;

        address = downloadStringFromUrl(url);

        try {
            JSONObject resultObj = new JSONObject(address);
            JSONArray results = resultObj.getJSONArray("results");

            if (results.length() > 0) {
                JSONArray locations = results.getJSONObject(0).getJSONArray("locations");
                if (locations.length() > 0) {

                    JSONObject firstLocation = locations.getJSONObject(0);

                    String street = firstLocation.getString("street");
                    String plz = firstLocation.getString("postalCode");
                    String city = firstLocation.getString("adminArea5");

                    if (street.indexOf(' ') != -1 && street.substring(0, street.indexOf(' ')).matches("-?\\d+")) {
                        String houseNo = street.substring(0, street.indexOf(' '));
                        street = street.substring(street.indexOf(' ') + 1);
                        address = street + " " + houseNo + ", " + plz + " " + city;

                    } else {
                        address = street + ", " + plz + " " + city;
                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return address;
    }

    /**
     * Searches for the coordinates of a given address.
     * This method blocks! Run it in an extra thread.
     * @param address Address
     * @return Location or null, if no location could be found.
     */
    public static Location getLocationFromAddress(String address) {
        try {
            String jsonResult = downloadStringFromUrl(getCoordinatesFromAddressRequestUrl(address));
            if (jsonResult == null) {
                return null;
            }

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
