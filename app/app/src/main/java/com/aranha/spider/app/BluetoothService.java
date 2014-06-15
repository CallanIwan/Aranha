package com.aranha.spider.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rutger on 10-05-2014.
 */
public class BluetoothService extends SpiderControllerService {
    private static final String TAG = "BluetoothService";

    private boolean isConnected = false;
    private boolean isTryingToConnect = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mRaspberryPiBluetoothDevice;
    private SpiderConnectionThread mSpiderConnectionThread;
    private String mRaspberryPiName = "raspberrypi-0";
    private BluetoothDeviceAdapter discoveredDevicesAdapter;

    public BluetoothDeviceAdapter getDiscoveredDevicesAdapter() {
        return discoveredDevicesAdapter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Register bluetooth device-discovery.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mBluetoothReceiver, filter); // Don't forget to unregister in onDestroy()

        Log.d(TAG,"Bluetooth receiver registered Bonded devices: " + mBluetoothAdapter.getBondedDevices().size());

        discoveredDevicesAdapter = new BluetoothDeviceAdapter(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);
    }

    /**
     * Every time a bluetooth mBluetoothDevice is found the onReceive() function gets executed.
     */
    final List<String> discoveredDevices = new ArrayList<String>();
    final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a bluetooth device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // Get the BluetoothDevice object from the Intent
                discoveredDevices.add(device.getName() + "\n" + device.getAddress()); // Add the name and address to an array adapter to show in a ListView
                Log.d(TAG, " -> " + device.getName() + " Looking for " + mRaspberryPiName  + " - " + device.getAddress());

                if(device.getName()!= null && device.getName().equals(mRaspberryPiName)) {
                    onRaspberryPiFound(device);
                }

                if(discoveredDevicesAdapter.getPosition(device) != -1) {
                    discoveredDevicesAdapter.add(device);
                }
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                if(!isConnected && !isTryingToConnect) {
                    Log.d(TAG, "Continue searching for device.");
                    discoverDevices();
                }
            }
        }
    };

    /**
     * Gets called whenever the Raspberry Pi is found via bluetooth.
     * @param device Device with the Raspberry Pi MAC address
     */
    public void onRaspberryPiFound(BluetoothDevice device) {
        mRaspberryPiBluetoothDevice = device;
        mBluetoothAdapter.cancelDiscovery();
        sendMessageToActivity(SpiderMessage.RASPBERRYPI_FOUND);

        connect();
    }

    /**
     * The message handler. This receives all the incoming messages from the Raspberry Pi.
     */
    final Messenger mBluetoothConnectorMessenger = new Messenger(new BluetoothConnectionMessenger());
    class BluetoothConnectionMessenger extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            SpiderMessage spiderMsg = SpiderController.SpiderMessages[msg.what];
            switch(spiderMsg) {
                case CONNECTED_TO_RASPBERRYPI:
                    if(msg.obj != null && msg.obj.getClass() == SpiderConnectionThread.class) {
                        mSpiderConnectionThread = (SpiderConnectionThread)msg.obj;
                        isConnected = true;
                        isTryingToConnect = false;
                        Log.d(TAG, "Bluetooth connection thread established");
                    }
                    sendMessageToActivity(SpiderMessage.CONNECTED_TO_RASPBERRYPI);
                    break;

                case CONNECTING_FAILED:
                case CONNECTION_CLOSED:
                case CONNECTION_LOST:
                    disconnect();
                    sendMessageToActivity(spiderMsg);
                    break;

                case READ_MSG_FROM_RASPBERRYPI:
                    //Log.d(TAG, "Received a msg." + msg.obj);
                    break;

                case READ_IMAGE:
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
    public void discoverDevices() {
        if(!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
            Log.d(TAG, "Discovering bluetooth devices!");
        }
    }

    private static int CAMERA_UPDATE_DELAY = 5000;
    private static Handler requestCameraImagesHandler = new Handler();
    private Runnable requestCameraImagesRunnable = new Runnable() {
        @Override
        public void run() {
            send(SpiderInstruction.requestCameraImage);
            requestCameraImagesHandler.postDelayed(this, CAMERA_UPDATE_DELAY);
        }
    };

    @Override
    public void setCameraEnabled(MainActivity mainActivity, boolean value) {
        if(value)
            requestCameraImagesHandler.postDelayed(requestCameraImagesRunnable, CAMERA_UPDATE_DELAY);
        else
            requestCameraImagesHandler.removeCallbacks(requestCameraImagesRunnable);
    }

    @Override
    public void setRaspberryPiName(String name) {
        mRaspberryPiName = name;
    }

    /**
     * Connect to the Raspberry Pi with a known MAC address.
     * @param MACAddress string
     */
    public void manualConnect(String MACAddress) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MACAddress);
        onRaspberryPiFound(device);
    }

    @Override
    public void connect() {
        if(!isConnected) {
            isTryingToConnect = true;
            mBluetoothAdapter.cancelDiscovery();
            ConnectToSpiderThread btThread = new ConnectToSpiderThread(mRaspberryPiBluetoothDevice, mBluetoothConnectorMessenger);
            btThread.start();
        }
    }

    @Override
    public void disconnect() {
        if(mSpiderConnectionThread != null)
            mSpiderConnectionThread.cancel();

        mRaspberryPiBluetoothDevice = null;
        mSpiderConnectionThread = null;
        isConnected = false;
        isTryingToConnect = false;
    }

    @Override
    public void send(SpiderInstruction instruction) {
        if(mSpiderConnectionThread != null)
            mSpiderConnectionThread.sendSpiderInstruction(instruction);
    }
    @Override
    public void send(SpiderInstruction instruction, String extraData) {
        if(mSpiderConnectionThread != null)
            mSpiderConnectionThread.sendSpiderInstruction(instruction, extraData);
    }
}
