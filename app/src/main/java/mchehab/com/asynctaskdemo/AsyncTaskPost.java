package mchehab.com.asynctaskdemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by muhammadchehab on 11/6/17.
 */

public class AsyncTaskPost extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> context;
    private String broadcastIntent;
    private String postData;

    public AsyncTaskPost(WeakReference<Context> context, String postData, String broadcastIntent){
        this.context = context;
        this.broadcastIntent = broadcastIntent;
        this.postData = postData;
    }

    @Override
    protected String doInBackground(String...params){

        try{
            URL url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection
                    .getOutputStream()));
            writer.write(postData);
            writer.flush();

            int responseCode = httpURLConnection.getResponseCode();
    //            check response code is OK
            if(responseCode == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (httpURLConnection.getInputStream()));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while((line = reader.readLine()) != null){
                    stringBuilder.append(line);
                }

                return stringBuilder.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result){
        Intent intent = new Intent(broadcastIntent);
        intent.putExtra("json", result);
        LocalBroadcastManager.getInstance(context.get().getApplicationContext()).sendBroadcast
                (intent);
    }
}