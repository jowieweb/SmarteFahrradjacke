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

public class GoogleMapsSearch {
    private static final String LOG_TAG = "GoogleMapsSearch";
    //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=ring&location=52.296907,8.904590&types=address&radius=5000&strictbounds&key=AIzaSyA3kqIUTIuGnYOuR8v44oBkcyDOpsovQzs

    private String prefix = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
    private String suffix ="&types=address&radius=5000&strictbounds&key=AIzaSyA3kqIUTIuGnYOuR8v44oBkcyDOpsovQzs";

    public  void GoogleMapsSearch(){

    }

    /**
     * querry the google api for suggestions
     * @param whatToSearch the userinput
     * @param loc user location
     */
    public void suggest(String whatToSearch, Location loc){
        String querryURL = prefix + whatToSearch + "&location=" + loc.getLatitude() + "," + loc.getLongitude() + suffix;
        RetrieveContentTask rct = new RetrieveContentTask();
        String retval ="";
        try {
            retval = rct.execute(querryURL).get();

        } catch (Exception e) {

        }

        ArrayList<String> retList = parseSuggestions(retval);


    }


    private ArrayList<String> parseSuggestions(String text) {

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



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }

}
