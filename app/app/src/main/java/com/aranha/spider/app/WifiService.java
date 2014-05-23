package com.aranha.spider.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Rutger on 20-05-14.
 */
public class WifiService  extends SpiderControllerService {
    private static final String TAG = "WifiService";

    // --------------------------------------------
    //    Set up WiFi
    // --------------------------------------------

    public static String raspberrySSID = "Aranha Groep 10";
    public static String raspberrySSID_quoted = "\"" + raspberrySSID + "\"";
    public static String raspberryPwd = "paashaas";


    WifiManager mWifiManager;
    WifiConfiguration mWifiConfiguration = null;
    List<WifiConfiguration> existingConfigs;
    private boolean isConnected = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        // Enable Wifi
        //
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }

        // Check if already connected to the network.
        //
        WifiInfo currentConnection = mWifiManager.getConnectionInfo();
        if(currentConnection != null) {
            if(currentConnection.getSSID() != null && currentConnection.getSSID().equals(raspberrySSID_quoted)) {
                isConnected = true;
                Log.d(TAG, "Already connected to the Raspberry");
            }
        }

        // Check existing configs
        //
        existingConfigs = mWifiManager.getConfiguredNetworks();

        if(checkExistingWifiConfigs(existingConfigs)) {
            Log.d(TAG, "Connecting to the Raspberry with a remembered network");
            isConnected = connectToExistingNetwork(mWifiConfiguration.SSID, existingConfigs);
        }
        else {
            // Find the wifi network.
            Log.d(TAG, "Trying to find the raspberry Wifi network with SSID " + raspberrySSID);
            registerWifiReceiver();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterWifiReceiver();
    }

    public void registerWifiReceiver() {
        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mIsWifiReceiverRegistered = true;
    }

    public void unregisterWifiReceiver() {
        if(mIsWifiReceiverRegistered) {
            unregisterReceiver(mWifiReceiver);
            mIsWifiReceiverRegistered = false;
        }
    }

    public boolean connectToExistingNetwork(String targetSSID, List<WifiConfiguration> configList) {
        for(WifiConfiguration config : configList) {
            if(config.SSID.equals(targetSSID)) {
                unregisterWifiReceiver();
                return mWifiManager.enableNetwork(config.networkId, true);
            }
        }
        return false;
    }

    public boolean checkExistingWifiConfigs(List<WifiConfiguration> configList) {
        if(mWifiConfiguration != null)
            return true;

        for(WifiConfiguration existingConfiguration : configList) {
            if(existingConfiguration.SSID.equals(raspberrySSID_quoted)) {
                mWifiConfiguration = existingConfiguration;
                return true;
            }
        }
        return false;
    }

    public boolean addNetworkConfig(String SSID_quoted, String preSharedKey) {
        if(mWifiConfiguration == null) {

            mWifiConfiguration = new WifiConfiguration();
            mWifiConfiguration.SSID = SSID_quoted;
            mWifiConfiguration.preSharedKey = "\"".concat(preSharedKey).concat("\"");
            mWifiConfiguration.status = WifiConfiguration.Status.DISABLED;
            mWifiConfiguration.priority = 40;

            mWifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            mWifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            mWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            mWifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            mWifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            mWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            mWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            mWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            mWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

            if(mWifiManager.addNetwork(mWifiConfiguration) == -1)
                return false;

            if(!mWifiManager.saveConfiguration())
                return false;

            Log.d(TAG, "Network added: " + SSID_quoted);

            return true;
        }
        return false;
    }

    boolean mIsWifiReceiverRegistered = false;
    BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            List<ScanResult> wifiScanList = mWifiManager.getScanResults();

            for(int i = 0; i < wifiScanList.size(); i++){
                ScanResult scanResult = wifiScanList.get(i);
                Log.d(TAG, "Received broadcast " + scanResult.SSID);
                if(scanResult.SSID.equals(raspberrySSID)) {
                    // 80:1f:02:af:5d:30

                    // Check if this network already exists.
                    if(!checkExistingWifiConfigs(existingConfigs)) {
                        if(addNetworkConfig(raspberrySSID_quoted, raspberryPwd)) {
                            connectToExistingNetwork(raspberrySSID, existingConfigs);
                        }
                    }

                    break;
                }
            }
        }
    };

    // --------------------------------------------
    //    Implemented SpiderController Methods
    // --------------------------------------------

    @Override
    public void sendMessageToActivity(SpiderMessage message) {

    }

    @Override
    public void setRaspberryPiName(String name) {

    }

    @Override
    public void sendMessageToActivity(SpiderMessage message, Object obj) {

    }

    @Override
    public void discoverDevices() {

    }


    @Override
    public void connect() {
        sendMessageToActivity(SpiderMessage.CONNECTED_TO_RASPBERRYPI);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send_getSpiderInfo() {

    }

    @Override
    public void send_requestCameraImage() {

    }

    @Override
    public void send(SpiderInstruction instruction) {

    }

    @Override
    public void send_move(int direction) {

    }

    @Override
    public void send_executeScript(int scriptIndex) {

    }
}
