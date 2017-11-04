package mchehab.com.asynctaskdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by muhammadchehab on 11/4/17.
 */

public abstract class BaseNetworkActivity extends AppCompatActivity {

    abstract void noInternetConnection();
    abstract void internetConnectionAvailable();

    private BroadcastReceiver broadcastReceiverConnectionChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!NetworkUtil.isNetworkAvailable(BaseNetworkActivity.this)) {
                noInternetConnection();
            } else {
                internetConnectionAvailable();
            }
        }
    };

    @Override
    protected void onPause() {
//        remove broadcast receiver when activity stops
        unregisterReceiver(broadcastReceiverConnectionChanged);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        register broadcast receiver after starting activity
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiverConnectionChanged, intentFilter);
    }

    protected boolean hasInternetConnection(){
        return NetworkUtil.isNetworkAvailable(this);
    }

    protected boolean isWifiInternet(){
        return NetworkUtil.isWifiNetwork(this);
    }

    protected boolean isMobileData(){
        return !(NetworkUtil.isWifiNetwork(this));
    }
}