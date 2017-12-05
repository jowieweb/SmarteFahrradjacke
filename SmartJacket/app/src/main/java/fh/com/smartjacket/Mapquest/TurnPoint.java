package fh.com.smartjacket.Mapquest;

import org.json.JSONObject;

/**
 * Created by jowie on 05.12.2017.
 */

public class TurnPoint {
    private double lat;
    private double lng;
    private String narrativ;

    public TurnPoint(JSONObject json)throws Exception{

        lat = json.getJSONObject("startPoint").getDouble("lat");
        lng = json.getJSONObject("startPoint").getDouble("lng");
        narrativ = json.getString("narrative");

    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getNarrativ() {
        return narrativ;
    }

    @Override
    public String toString() {
        return "TurnPoint{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", narrativ='" + narrativ + '\'' +
                '}';
    }
}
