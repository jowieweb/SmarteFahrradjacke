package fh.com.smartjacket.Mapquest;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jowie on 19.12.2017.
 */

public class GoogleMapsSearch implements ContentReadyListener{
    private static final String LOG_TAG = "GoogleMapsSearch";

    //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=ring&location=52.296907,8.904590&types=address&radius=5000&strictbounds&key=AIzaSyA3kqIUTIuGnYOuR8v44oBkcyDOpsovQzs
    private String suggestprefix = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
    private String suggestsuffix ="&types=address&radius=5000&strictbounds&key=AIzaSyA3kqIUTIuGnYOuR8v44oBkcyDOpsovQzs";

    //https://maps.googleapis.com/maps/api/place/textsearch/json?query=ringst&location=52.296907,8.904590&radius=1&key=AIzaSyA3kqIUTIuGnYOuR8v44oBkcyDOpsovQzs
    private String findprefix = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    private String findsuffix = "&radius=25000&key=AIzaSyA3kqIUTIuGnYOuR8v44oBkcyDOpsovQzs";

    private  SuggestionListener suggestionListener;

    public  GoogleMapsSearch(SuggestionListener suggestionListener){
        this.suggestionListener = suggestionListener;
    }


    public Location getLocationOfAddress(String address, Location currentLocation){
        Location retval = new Location("");
        String querryURL = findprefix + address + "&location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + findsuffix;
        RetrieveContentTask rct = new RetrieveContentTask();
        String text ="";
        try {
            text = rct.execute(querryURL).get();
            JSONObject json = new JSONObject(text);
            JSONArray results = json.getJSONArray("results");
            JSONObject loc =  results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            String lat = loc.getString("lat");
            String lng = loc.getString("lng");
            retval.setLatitude(Double.parseDouble(lat));
            retval.setLongitude(Double.parseDouble(lng));
            Log.i(LOG_TAG,"found at lat:" + lat + " lng:" + lng);

        } catch (Exception e) {

        }



        return  retval;
    }




    /**
     * querry the google api for suggestions
     * @param whatToSearch the userinput
     * @param loc user location
     */
    public void suggest(String whatToSearch, Location loc){
        String querryURL = suggestprefix + whatToSearch + "&location=" + loc.getLatitude() + "," + loc.getLongitude() + suggestsuffix;
        RetrieveContentTask rct = new RetrieveContentTask(this);
        String retval ="";
        try {
            rct.execute(querryURL);

        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }

       //return parseSuggestions(retval);
    }


    private String[] parseSuggestions(String text) {

        ArrayList<String> suggestions = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(text);
            JSONArray results = json.getJSONArray("predictions");

            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String add = result.getString("description");
                Log.i(LOG_TAG, add);
                suggestions.add(add);
            }



        } catch (Exception e){
            Log.e(LOG_TAG, e.toString());
        }

        String retArr[] = new String[suggestions.size()];
        suggestions.toArray(retArr);
        return retArr;
    }

    @Override
    public void contentReady(String content) {
        suggestionListener.suggest( parseSuggestions(content));
    }
}
