package fh.com.smartjacket.pojo;

import android.content.Context;
import android.util.Log;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fh.com.smartjacket.R;

/**
 * Created by jowie on 29.12.2017.
 */

public class LightCalculator {
    private static LightCalculator instance;
    private Location currentLocation;
    private Date sunrise;
    private Date sunset;
    private SunriseSunsetCalculator calculator;
    private static String LOG_TAG ="LIGHTLEVEL";
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public LightCalculator(android.location.Location location){
        currentLocation = new Location(location.getLatitude(), location.getLongitude());
        calculator = new SunriseSunsetCalculator(currentLocation, TimeZone.getDefault());
        String sunrise =calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String sunset =calculator.getOfficialSunsetForDate(Calendar.getInstance());

        DateFormat format = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        try {
            this.sunset = format.parse(sunset + " " + df.format(Calendar.getInstance().getTime()));
            this.sunrise = format.parse(sunrise + " " + df.format(Calendar.getInstance().getTime()));

        } catch (ParseException e) {

        }
        Log.i(LOG_TAG, this.sunset.toString());
        Log.i(LOG_TAG, this.sunrise.toString());
    }

    public LightLevel getLightLevel(){
        long currentTime = Calendar.getInstance().getTimeInMillis();
        /* sun is there for more than half an hour and will stay longer than half an hour */
        if(sunrise.getTime() + 18000 < currentTime && sunset.getTime() -18000 > currentTime){
            return LightLevel.Medim;
        } else {
            /* otherwise no sun */
            return  LightLevel.Low;
        }

        //TODO: any idea for high LightLevel?
    }

    public static String getJsonFromEnum(Context cntx, LightLevel ll){
        switch (ll){
            case Low:
                return cntx.getResources().getString(R.string.intent_extra_low_light);
            case Medim:
                return cntx.getResources().getString(R.string.intent_extra_medium_light);
            case High:
                return cntx.getResources().getString(R.string.intent_extra_high_light);
        }
        return cntx.getResources().getString(R.string.intent_extra_low_light);

    }

    public enum  LightLevel{
        High,
        Medim,
        Low
    }

}


