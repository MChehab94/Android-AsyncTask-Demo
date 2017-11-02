package mchehab.com.asynctaskdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by muhammadchehab on 11/1/17.
 */

public class AsyncImageDownloader extends AsyncTask<String, Integer, Bitmap>{

    private WeakReference<Context> applicationContext;

    public AsyncImageDownloader(WeakReference<Context> context){
        this.applicationContext = context;
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
        Intent intent = new Intent("image");
        intent.putExtra("image", Util.saveToInternalStorage("image", bitmap, applicationContext
                        .get()));
        LocalBroadcastManager.getInstance(applicationContext.get()).sendBroadcast(intent);
    }
}