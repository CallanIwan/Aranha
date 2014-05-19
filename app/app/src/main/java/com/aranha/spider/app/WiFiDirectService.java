package com.aranha.spider.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.*;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectService extends Service implements SpiderController {
    private static final String TAG = "WiFiDirectService";

    /**
     * The activity which connects to this service can receive
     * messages by providing a Messenger as extra data when binding.
     */
    private Messenger mActivityMessenger;


    /**
     * The binder which Activities (clients) use to communicate with this bluetooth service.
     */
    private final IBinder mBinder = new WifiDirectBinder();
    public class WifiDirectBinder extends Binder {
        WiFiDirectService getService() {
            return WiFiDirectService.this;
        }
    }

    /**
     * When an activity binds to this service this gets called.
     * @return The this instance.
     */
    @Override
    public IBinder onBind(Intent intent) {
        this.mActivityMessenger = intent.getParcelableExtra("messageReceiver");
        if(mActivityMessenger == null) {
            Log.d(TAG, "Activity bound to the service but did not send a MessageReceiver");
        }
        Log.d(TAG, "New activity bound to this service");
        return mBinder;
    }

    // --------------------------------------------
    //    Set up Wifi-Direct
    // --------------------------------------------

    IntentFilter mIntentFilter;

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiP2pDevice mRaspberryPiDevice;
    private String mRaspberryPiWifiDirectName = "Piracast";


    @Override
    public void onCreate() {
        super.onCreate();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(mWifiP2PReceiver, mIntentFilter);
        Log.d(TAG,"Bluetooth receiver registered");

        mManager.discoverPeers(mChannel, mConnectActionListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiP2PReceiver);
    }

    public void setRaspberryPiWifiDirectName(String newName) {
        mRaspberryPiWifiDirectName = newName;
    }

    public void onRaspberryPiFound(WifiP2pDevice device) {
        mRaspberryPiDevice = device;
        mManager.stopPeerDiscovery(mChannel, null);
        sendMessageToActivity(SpiderController.SpiderMessage.RASPBERRYPI_FOUND);
        Log.d(TAG, "Raspberry found");
    }

    final BroadcastReceiver mWifiP2PReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers

                if (mManager != null) {
                    mManager.requestPeers(mChannel, mPeerListener);
                }
                Log.d(TAG, "P2P peers changed");
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
            }
        }
    };

    final PeerListListener mPeerListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {

            for(WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                Log.d(TAG, "device " + device.deviceName);

                if(device.deviceName.equals(mRaspberryPiWifiDirectName)) {
                    onRaspberryPiFound(device);
                }
            }
        }
    };

    public void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 1;
        config.wps.pin = "12345678";

        Log.d(TAG, "Connecting to a device with address: " + device.deviceAddress);
        mManager.connect(mChannel, config,  new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectService.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ActionListener mConnectActionListener = new ActionListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "?????????????????");
        }

        @Override
        public void onFailure(int reason) {
            //failure logic
            Log.d(TAG, "Si senõr? " + reason);
        }
    };
    // --------------------------------------------
    //    Implemented SpiderController Methods
    // --------------------------------------------

    @Override
    public void connect() {
        if(mRaspberryPiDevice != null) {
            connect(mRaspberryPiDevice);
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void sendMessageToActivity(SpiderMessage message) {
        if (mActivityMessenger != null) {
            Log.d(TAG, "Sending message to activity: " + message);
            try {
                mActivityMessenger.send(android.os.Message.obtain(null, message.ordinal(), 0, 0));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Cannot send MSG to activity. Activity did not provide a Messenger!");
        }
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