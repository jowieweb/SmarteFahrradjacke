package fh.com.smartjacket.Mapquest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jowie on 04.12.2017.
 */

public class Mapquest {
    private static final String key = "";
    private static final String urlPrefix = "https://www.mapquestapi.com/directions/v2/route?key=YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7&from=";
    private static final String suffix = "&outFormat=json&ambiguities=ignore&routeType=bicycle&doReverseGeocode=false&enhancedNarrative=false&avoidTimedConditions=false";



    private String getURL(String from, String to){
        return urlPrefix + from + "&to=" + to + suffix;
    }


    private String getExampleURL(){
        return "https://www.mapquestapi.com/directions/v2/route?key=YNF4GZfIelpgaU7ApDMhhDXyMoPYEcT7&from=Artilleriestra%C3%9Fe+15%2C+32427+Minden%2C+Germany&to=Ringstra%C3%9Fe+111%2C+32427+Minden%2C+Germany&outFormat=json&ambiguities=ignore&routeType=bicycle&doReverseGeocode=false&enhancedNarrative=false&avoidTimedConditions=false";
    }



    public String debug(){
       RetrieveContentTask rct =  new RetrieveContentTask();
       try{
           return rct.execute(getExampleURL()).get();
       }catch (Exception e){

       }
       return "";


    }


}
