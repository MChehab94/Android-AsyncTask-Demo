package mchehab.com.asynctaskdemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by muhammadchehab on 10/31/17.
 */

public class GetJSON extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> applicationContext;
    private String broadcastIntent;

    public GetJSON(WeakReference<Context> context, String broadcastIntent){
        this.applicationContext = context;
        this.broadcastIntent = broadcastIntent;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                    (httpURLConnection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent intent = new Intent(broadcastIntent);
        intent.putExtra("result", result);
        LocalBroadcastManager.getInstance(applicationContext.get()).sendBroadcast(intent);
    }
}