package fh.com.smartjacket.Mapquest;

import android.location.Location;

import org.json.JSONObject;

/**
 * Created by jowie on 05.12.2017.
 */

public class TurnPoint {
    private double lat;
    private double lng;
    private Location location;
    private String narrativ;

    public TurnPoint(JSONObject json)throws Exception{

        lat = json.getJSONObject("startPoint").getDouble("lat");
        lng = json.getJSONObject("startPoint").getDouble("lng");
        narrativ = json.getString("narrative");
        location  = new Location("");
        location.setLongitude(lng);
        location.setLatitude(lat);

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

    public TurnDirection getTurnDirection(){
        if(getNarrativ().contains("left"))
            return TurnDirection.left;
        else if (getNarrativ().contains("right"))
            return TurnDirection.right;
        return TurnDirection.undefined;
    }

    @Override
    public String toString() {
        return "TurnPoint{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", narrativ='" + narrativ + '\'' +
                '}';
    }

    public Location getLocation(){ return location;}

    public  enum TurnDirection
    {
        left,
        right,
        undefined
    }
}
