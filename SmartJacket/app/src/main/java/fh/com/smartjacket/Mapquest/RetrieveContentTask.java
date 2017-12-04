package fh.com.smartjacket.Mapquest;

import android.os.AsyncTask;

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
    private Exception exception;
    private  StringBuffer buf = new StringBuffer();

    public RetrieveContentTask(){

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

        } catch (MalformedURLException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }

        return buf.toString();
    }

    protected void onPostExecute(String page)
    {

    }


}
