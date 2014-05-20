package com.aranha.spider.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Created by Rutger on 20-05-14.
 */
public class WifiService  extends Service implements SpiderController  {
    private static final String TAG = "WifiService";

    /**
     * The activity which connects to this service can receive
     * messages by providing a Messenger as extra data when binding.
     */
    private Messenger mActivityMessenger;


    /**
     * The binder which Activities (clients) use to communicate with this bluetooth service.
     */
    private final IBinder mBinder = new WifiBinder();
    public class WifiBinder extends Binder {
        WifiService getService() {
            return WifiService.this;
        }
    }

    /**
     * When an activity binds to this service this gets called.
     * @return The this instance.
     */
    @Override
    public IBinder onBind(Intent intent) {
        mActivityMessenger = intent.getParcelableExtra("messageReceiver");
        if(mActivityMessenger == null) {
            Log.d(TAG, "Activity bound to the service but did not send a MessageReceiver");
        }
        Log.d(TAG, "New activity bound to this service");
        return mBinder;
    }

    // --------------------------------------------
    //    Set up WiFi
    // --------------------------------------------

    WifiManager mWifiManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiManager.startScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiReceiver);
        mWifiManager.disconnect();
    }

    BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, "Received broadcast" + intent.getAction());
        }
    };


    @Override
    public void connect() {
        sendMessageToActivity(SpiderMessage.CONNECTED_TO_RASPBERRYPI);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void sendMessageToActivity(SpiderMessage message) {

    }

    @Override
    public void send_getSpiderInfo() {

    }

    @Override
    public void send(SpiderInstruction instruction) {

    }

    @Override
    public void send_move(int direction) {

    }

    @Override
    public void send_moveLeft() {

    }

    @Override
    public void send_moveRight() {

    }

    @Override
    public void send_moveForward() {

    }

    @Override
    public void send_moveBackwards() {

    }

    @Override
    public void send_moveUp() {

    }

    @Override
    public void send_moveDown() {

    }

    @Override
    public void send_dance() {

    }

    @Override
    public void send_resetToDefaultPosition() {

    }

    @Override
    public void send_executeScript(int scriptIndex) {

    }
}
