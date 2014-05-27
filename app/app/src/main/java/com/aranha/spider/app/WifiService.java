package com.aranha.spider.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Rutger on 20-05-14.
 */
public class WifiService  extends SpiderControllerService {
    private static final String TAG = "WifiService";

    // --------------------------------------------
    //    Set up WiFi
    // --------------------------------------------

    public static String raspberryIP = "10.0.0.2";
    public static int raspberryPort = 9999;
    public static String raspberrySSID = "Aranha Groep 10";
    public static String raspberrySSID_quoted = "\"" + raspberrySSID + "\"";
    public static String raspberryPwd = "paashaas";
    private SpiderConnectionThread mSpiderConnectionThread;

    WifiManager mWifiManager;
    WifiConfiguration mWifiConfiguration = null;
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
                setIsConnected(true);
                Log.d(TAG, "Already connected to the Raspberry");
            }
        }

        IntentFilter filters = new IntentFilter();
        filters.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filters.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filters.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWifiReceiver, filters);
        mIsWifiReceiverRegistered = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mIsWifiReceiverRegistered) {
            unregisterReceiver(mWifiReceiver);
            mIsWifiReceiverRegistered = false;
        }
    }

    public boolean connectToExistingNetwork(String targetSSID, List<WifiConfiguration> configList) {
        Log.d(TAG, "Trying to connect to Wifi network: " + targetSSID);
        for(WifiConfiguration config : configList) {
            if(config.SSID.equals(targetSSID)) {
                //unregisterWifiReceiver();
                Log.d(TAG, "Connecting to Wifi network: " + targetSSID);
                return mWifiManager.enableNetwork(config.networkId, true);
            }
        }
        return false;
    }

    public boolean checkExistingWifiConfigs(List<WifiConfiguration> configList) {
        if(mWifiConfiguration != null)
            return true;

        if(configList == null) {
            Log.e(TAG, "no config list found!");
            return false;
        }

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

    public void checkCurrentWifiScanList() {
        List<ScanResult> wifiScanList = mWifiManager.getScanResults();

        for (int i = 0; i < wifiScanList.size(); i++) {
            ScanResult scanResult = wifiScanList.get(i);
            Log.d(TAG, "Received broadcast " + scanResult.SSID);
            if (scanResult.SSID.equals(raspberrySSID)) { // TODO 80:1f:02:af:5d:30

                // Check if this network already exists.
                if (!checkExistingWifiConfigs(mWifiManager.getConfiguredNetworks())) {
                    if (addNetworkConfig(raspberrySSID_quoted, raspberryPwd)) {
                        connectToExistingNetwork(raspberrySSID_quoted, mWifiManager.getConfiguredNetworks());
                    }
                } else {
                    connectToExistingNetwork(raspberrySSID_quoted, mWifiManager.getConfiguredNetworks());
                }
                break;
            }
        }
    }

    boolean mIsWifiReceiverRegistered = false;
    BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                if(!isConnected)
                    checkCurrentWifiScanList();
            }
            else if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {

                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                if(state == WifiManager.WIFI_STATE_DISABLED) {
                    Log.d(TAG, "State changed to disabled");
                    setIsConnected(false);
                }
                else if(state == WifiManager.WIFI_STATE_ENABLED) {
                    Log.d(TAG, "State changed to enabled");
                    checkCurrentWifiScanList();
                }
            }
            else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {

                NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(NetworkInfo.State.CONNECTED.equals(nwInfo.getState())) {
                    if(nwInfo.getExtraInfo().equals(raspberrySSID_quoted)) {
                        Log.d(TAG, "is Connected!!!!! ");

                        setIsConnected(true);
                    }
                    else {
                        setIsConnected(false);
                    }
                }
                else if(NetworkInfo.State.CONNECTING.equals(nwInfo.getState())) {
                    Toast.makeText(WifiService.this, "Trying to connect to the spidah", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    public void setIsConnected(boolean value) {
        isConnected = value;

        if(value) {
            ConnectToSpiderThread btThread = new ConnectToSpiderThread(raspberryIP, raspberryPort, mBluetoothConnectorMessenger);
            btThread.start();
        } else {
            sendMessageToActivity(SpiderMessage.CONNECTION_CLOSED);
        }
    }

    /**
     * The message handler. This receives all the incoming messages from the Raspberry Pi.
     */
    final Messenger mBluetoothConnectorMessenger = new Messenger(new BluetoothConnectionMessenger());
    class BluetoothConnectionMessenger extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            switch(SpiderController.SpiderMessages[msg.what]) {
                case CONNECTED_TO_RASPBERRYPI:
                    if(msg.obj != null && msg.obj.getClass() == SpiderConnectionThread.class) {
                        mSpiderConnectionThread = (SpiderConnectionThread)msg.obj;
                        Log.d(TAG, "Bluetooth connection thread established");
                    }
                    sendMessageToActivity(SpiderMessage.CONNECTED_TO_RASPBERRYPI);
                    break;

                case CONNECTING_FAILED:
                    mSpiderConnectionThread = null;
                    sendMessageToActivity(SpiderMessage.CONNECTING_FAILED);
                    break;

                case CONNECTION_CLOSED:
                    mSpiderConnectionThread = null;
                    sendMessageToActivity(SpiderMessage.CONNECTION_CLOSED);
                    break;

                case CONNECTION_LOST:
                    mSpiderConnectionThread = null;
                    sendMessageToActivity(SpiderMessage.CONNECTION_LOST);
                    break;

                case READ_MSG_FROM_RASPBERRYPI:
                    //Log.d(TAG, "Received a msg." + msg.obj);
                    break;

                case READ_IMAGE:
                    //Log.d(TAG, "Received an image.");
                    sendMessageToActivity(SpiderMessage.READ_IMAGE, msg.obj);
                    break;

                case READ_SCRIPT_LIST:
                    sendMessageToActivity(SpiderMessage.READ_SCRIPT_LIST, msg.obj);
                    break;
            }
        }
    }

    // --------------------------------------------
    //    Implemented SpiderController Methods
    // --------------------------------------------


    @Override
    public void setRaspberryPiName(String name) {
        raspberrySSID = name;
    }

    @Override
    public void discoverDevices() {

    }


    @Override
    public void connect() {
        //sendMessageToActivity(SpiderMessage.CONNECTED_TO_RASPBERRYPI);
    }

    @Override
    public void disconnect() {
        if(isConnected) {
            mWifiManager.disconnect();
            setIsConnected(false);
        }
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
