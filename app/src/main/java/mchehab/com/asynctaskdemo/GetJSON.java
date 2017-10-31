package mchehab.com.asynctaskdemo;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by muhammadchehab on 10/31/17.
 */

public class GetJSON extends AsyncTask<String, Integer, String> {

    private AsyncListener asyncListener;

    public GetJSON(AsyncListener asyncListener){
        this.asyncListener = asyncListener;
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
        asyncListener.getResult(result);
    }
}
