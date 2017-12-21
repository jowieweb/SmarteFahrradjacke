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
    private double distance = 0;
    private LatLng upperLeft;



    private LatLng lowerRight;

    public Route( ArrayList<TurnPoint> turnPoints,  ArrayList<LatLng> shape, double distance, LatLng upperLeft, LatLng lowerRight){
        this.turnPoints = turnPoints;
        this.shape = shape;
        this.distance = distance;
        this.upperLeft = upperLeft;
        this.lowerRight = lowerRight;
    }

    public ArrayList<TurnPoint> getTurnPoints(){
        return turnPoints;
    }

    public ArrayList<LatLng> getShape(){
        return shape;
    }
    public double getDistance() {
        return distance;
    }

    public LatLng getUpperLeft() {
        return upperLeft;
    }

    public LatLng getLowerRight() {
        return lowerRight;
    }

    public int getZoomlevel(){
        if(distance <2)
            return  13;
        if(distance < 3)
            return 12;
        else if (distance < 8)
            return  11;
        else if(distance < 20)
            return  10;
        else if(distance < 40)
            return  9;
        return  8;
    }

    public LatLng midPoint(){

        double lat1=upperLeft.getLatitude();
        double lon1=upperLeft.getLongitude();
        double lat2 =lowerRight.getLatitude();
        double lon2 =lowerRight.getLongitude();
        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }
}
