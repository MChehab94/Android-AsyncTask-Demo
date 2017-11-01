package mchehab.com.asynctaskdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by muhammadchehab on 11/1/17.
 */

public class AsyncImageDownloader extends AsyncTask<String, Integer, Bitmap>{

    private AsyncImageListener listener;

    public AsyncImageDownloader(AsyncImageListener listener){
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params){

        try{
            URL url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        listener.getBitmap(bitmap);
    }
}