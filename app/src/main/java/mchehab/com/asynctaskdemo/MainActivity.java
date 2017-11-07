package mchehab.com.asynctaskdemo;

import android.app.AlertDialog;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

public class MainActivity extends BaseNetworkActivity{

    private TextView textView;
    private ImageView imageView;
    private ProgressBar progressBar;

    private final String URL = "http://validate.jsontest.com/?json=%7B%22key%22:%22value%22%7D";
    private final String IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3d/LARGE_elevation" +
            ".jpg/800px-LARGE_elevation.jpg";

    private HttpAsyncTask getJSON;
    private AsyncImageDownloader asyncImageDownloader;
    private String imageDirectory;

    private AlertDialog alertDialogNoInternet;

    private boolean isImageDownloading = false;
    private boolean isJSONDownloading = false;
    private boolean isJSONPosting = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                textView.setText(bundle.getString("result"));
            }
            isJSONDownloading = false;
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
                    isImageDownloading = false;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void registerBroadcast(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter){
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterBroadcasts(BroadcastReceiver...broadcastReceivers){
        for(BroadcastReceiver broadcastReceiver : broadcastReceivers){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerBroadcast(broadcastReceiver, new IntentFilter(BroadcastConstants.JSON));
        registerBroadcast(imageBroadcastReceiver, new IntentFilter(BroadcastConstants.IMAGE));
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterBroadcasts(broadcastReceiver, imageBroadcastReceiver);
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

        alertDialogNoInternet = new AlertDialog
                .Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please make sure you have a valid internet connection")
                .setPositiveButton("Ok", null)
                .create();

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        if(savedInstanceState != null){
            handleSavedInstanceState(savedInstanceState);
        }

        findViewById(R.id.button).setOnClickListener(e -> executeGetJSON());
        findViewById(R.id.buttonPost).setOnClickListener(e -> executePostJSON());
        findViewById(R.id.buttonImage).setOnClickListener(e -> executeAsyncImageDownloader());
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
        isJSONDownloading = true;
        if(hasInternetConnection()){
            getJSON = new HttpAsyncTask(new WeakReference<>(getApplicationContext()),
                    BroadcastConstants.JSON);
            getJSON.execute(URL);
        }else{
            displayNoInternetDialog();
        }
    }

    private void executePostJSON(){
        isJSONPosting = true;
        if(hasInternetConnection()){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("comments", "just deliver");
                jsonObject.put("custemail", "myemail");
                jsonObject.put("size", "medium");

                JSONObject jsonObjectForm = new JSONObject();
                jsonObjectForm.put("form", jsonObject);

                getJSON = new HttpAsyncTask(new WeakReference<Context>(this), BroadcastConstants.JSON,
                        HTTP.POST, jsonObject.toString());
                getJSON.execute("https://httpbin.org/post");
            }catch (JSONException jsonException){
                jsonException.printStackTrace();
            }
        }else{
            displayNoInternetDialog();
        }
    }

    private void executeAsyncImageDownloader(){
        isImageDownloading = true;
        if(hasInternetConnection()){
            imageView.setImageBitmap(null);
            progressBar.setVisibility(View.VISIBLE);
            asyncImageDownloader = new AsyncImageDownloader(new WeakReference<>(getApplicationContext
                    ()), BroadcastConstants.IMAGE);
            asyncImageDownloader.execute(IMAGE_URL);
        }else{
            displayNoInternetDialog();
        }
    }

    private void displayNoInternetDialog(){
        alertDialogNoInternet.show();
    }

    @Override
    void noInternetConnection() {
        displayNoInternetDialog();
    }

    @Override
    void internetConnectionAvailable() {
        if(alertDialogNoInternet.isShowing()){
            alertDialogNoInternet.dismiss();
        }
        if(isImageDownloading){
            executeAsyncImageDownloader();
        }
        if(isJSONDownloading){
            executeGetJSON();
        }
        if(isJSONPosting){
            executePostJSON();
        }
    }
}