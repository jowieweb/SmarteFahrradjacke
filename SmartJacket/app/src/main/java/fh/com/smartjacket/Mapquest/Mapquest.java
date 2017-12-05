package fh.com.smartjacket.Mapquest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by jowie on 04.12.2017.
 */

public class Mapquest {
    private static final String key = "";
    private static final String urlPrefix = "https://www.mapquestapi.com/directions/v2/route?key=YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7&from=";
    private static final String suffix = "&outFormat=json&ambiguities=ignore&routeType=bicycle&doReverseGeocode=false&enhancedNarrative=false&avoidTimedConditions=false";

    private ArrayList<TurnPoint> turnPoints = new ArrayList<>();


    private String getURL(String from, String to) {
        return urlPrefix + from + "&to=" + to + suffix;
    }


    private String getExampleURL() {
        return "https://www.mapquestapi.com/directions/v2/route?key=YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7&from=Artilleriestra%C3%9Fe+15%2C+32427+Minden%2C+Germany&to=Ringstra%C3%9Fe+111%2C+32427+Minden%2C+Germany&outFormat=json&ambiguities=ignore&routeType=bicycle&doReverseGeocode=false&enhancedNarrative=false&avoidTimedConditions=false";
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
