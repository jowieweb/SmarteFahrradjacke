package fh.com.smartjacket.Mapquest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jowie on 04.12.2017.
 */

public class RetrieveContentTask extends AsyncTask<String, Void, String> {
    private  ContentReadyListener crl;
    private Exception exception;
    private  StringBuffer buf = new StringBuffer();
    private static final String LOG_TAG = "RETRIEVE_CONTENT_TASK";


    public RetrieveContentTask(){

    }

    public  RetrieveContentTask(ContentReadyListener crl){
        this.crl = crl;
    }

    @Override
    protected String doInBackground(String... url) {
        return getSourceOfUrl(url[0]);
    }


    private String getSourceOfUrl(String pUrl){
        URL url;

        try {

            url = new URL(pUrl);
            URLConnection conn = url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                buf.append(inputLine);
            }

            br.close();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }

        return buf.toString();
    }

    protected void onPostExecute(String page)
    {
        if(crl != null) {
            Log.i(LOG_TAG, "POSTEXCEC" + page);
            crl.contentReady(page);
        }
    }


}
