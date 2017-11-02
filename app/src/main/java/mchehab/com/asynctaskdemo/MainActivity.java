package mchehab.com.asynctaskdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity{

    private TextView textView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private final String URL = "http://validate.jsontest.com/?json=%7B%22key%22:%22value%22%7D";
    private final String IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3d/LARGE_elevation" +
            ".jpg/800px-LARGE_elevation.jpg";

    private GetJSON getJSON;
    private AsyncImageDownloader asyncImageDownloader;
    private String imageDirectory;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                String result = bundle.getString("result");
                textView.setText(result);
            }
        }
    };

    private BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                imageDirectory = bundle.getString("image");
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(imageDirectory));
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new
                IntentFilter(BroadcastConstants.JSON));
        LocalBroadcastManager.getInstance(this).registerReceiver(imageBroadcastReceiver, new
                IntentFilter(BroadcastConstants.IMAGE));
    }

    @Override
    protected void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(imageBroadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String text = textView.getText().toString();
        if(asyncImageDownloader != null && asyncImageDownloader.getStatus() == AsyncTask.Status
                .RUNNING){
            outState.putBoolean(SaveInstanceConstants.MAIN_ACTIVITY_ASYNC_JSON, true);
        }
        if(imageView.getDrawable() != null){
            //save to internal storage
            BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            imageDirectory = Util.saveToInternalStorage("image", bitmap, getApplicationContext());
            outState.putString(SaveInstanceConstants.MAIN_ACTIVITY_IMAGE, imageDirectory);
        }

        outState.putString(SaveInstanceConstants.MAIN_ACTIVITY_TEXT, text);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        if(savedInstanceState != null){
            handleSavedInstanceState(savedInstanceState);
        }

        Button button = findViewById(R.id.button);
        Button buttonImage = findViewById(R.id.buttonImage);

        button.setOnClickListener(e -> {
            executeGetJSON();
        });
        buttonImage.setOnClickListener(e -> {
            executeAsyncImageDownloader();
        });
    }

    private void handleSavedInstanceState(Bundle savedInstanceState){
        imageDirectory = savedInstanceState.getString(SaveInstanceConstants.MAIN_ACTIVITY_IMAGE);
        if(imageDirectory != null){
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(imageDirectory));
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        textView.setText(savedInstanceState.getString(SaveInstanceConstants.MAIN_ACTIVITY_TEXT));

        if(savedInstanceState.getBoolean(SaveInstanceConstants.MAIN_ACTIVITY_ASYNC_JSON)){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void executeGetJSON(){
        getJSON = new GetJSON(new WeakReference<>(getApplicationContext()), BroadcastConstants.IMAGE);
        getJSON.execute(URL);
    }

    private void executeAsyncImageDownloader(){
        imageView.setImageBitmap(null);
        progressBar.setVisibility(View.VISIBLE);
        asyncImageDownloader = new AsyncImageDownloader(new WeakReference<>(getApplicationContext
                ()), BroadcastConstants.IMAGE);
        asyncImageDownloader.execute(IMAGE_URL);
    }
}