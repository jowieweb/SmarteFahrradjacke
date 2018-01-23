package fh.com.smartjacket.pojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jowie on 01.01.2018.
 */

public class HomeAddress {
    private String address;
    private String postcode;
    private String housenumber;

    public HomeAddress(String json){
        try {
            JSONObject js = new JSONObject(json);
            address = js.getString("address");
            postcode = js.getString("postcode");
            housenumber = js.getString("hausnumber");
        }catch (Exception jse){
            address = "Artilleriestraße";
            postcode = "32427 Minden";
            housenumber = "15";
        }
        if(address.length() == 0){
            address = " ";
        }
        if(postcode.length() == 0){
            postcode = " ";
        }
        if(housenumber.length() == 0){
            housenumber = " ";
        }
    }

    public HomeAddress(String address, String postcode, String housenumber){
        this.address = address;
        this.housenumber = housenumber;
        this.postcode = postcode;
    }



    public String toJsonString(){
        JSONObject json = new JSONObject();
        try {
            json.put("address", address);
            json.put("hausnumber", housenumber);
            json.put("postcode", postcode);
        }catch (JSONException jse ){

        }
        return  json.toString();
    }



    public String getAddress() {
        return address;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getHausnumber() {
        return housenumber;
    }


    @Override
    public String toString(){
        return address + " " + housenumber + ", " + postcode;
    }


}
