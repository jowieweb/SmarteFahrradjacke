package fh.com.smartjacket.Mapquest;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jonas on 21.12.2017.
 */

public class Route {
    private ArrayList<TurnPoint> turnPoints;
    private ArrayList<LatLng> shape;

    public Route( ArrayList<TurnPoint> turnPoints,  ArrayList<LatLng> shape){
        this.turnPoints = turnPoints;
        this.shape = shape;
    }

    public ArrayList<TurnPoint> getTurnPoints(){
        return turnPoints;
    }

    public ArrayList<LatLng> getShape(){
        return shape;
    }
}
