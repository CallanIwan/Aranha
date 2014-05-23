package com.aranha.spider.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rutger on 10-05-2014.
 */
public class BluetoothService extends SpiderControllerService {
    private static final String TAG = "BluetoothService";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mRaspberryPiBluetoothDevice;
    private BluetoothSpiderConnectionThread mBluetoothSpiderConnectionThread;
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
        registerReceiver(mBluetoothReceiver, filter); // Don't forget to unregister in onDestroy()
        Log.d(TAG,"Bluetooth receiver registered");

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
                    if(msg.obj != null && msg.obj.getClass() == BluetoothSpiderConnectionThread.class) {
                        mBluetoothSpiderConnectionThread = (BluetoothSpiderConnectionThread)msg.obj;
                        Log.d(TAG, "Bluetooth connection thread established");
                    }
                    sendMessageToActivity(SpiderMessage.CONNECTED_TO_RASPBERRYPI);
                    break;

                case CONNECTING_FAILED:
                    mRaspberryPiBluetoothDevice = null;
                    mBluetoothSpiderConnectionThread = null;
                    sendMessageToActivity(SpiderMessage.CONNECTING_FAILED);
                    break;

                case CONNECTION_CLOSED:
                    mRaspberryPiBluetoothDevice = null;
                    mBluetoothSpiderConnectionThread = null;
                    sendMessageToActivity(SpiderMessage.CONNECTION_CLOSED);
                    break;

                case CONNECTION_LOST:
                    mRaspberryPiBluetoothDevice = null;
                    mBluetoothSpiderConnectionThread = null;
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
    public void discoverDevices() {
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
        Log.d(TAG, "Discovering bluetooth devices!");
    }

    @Override
    public void sendMessageToActivity(SpiderMessage message, Object obj) {
        if (mActivityMessenger != null) {
            Log.d(TAG, "Sending Object message to activity: " + message);
            try {
                mActivityMessenger.send(android.os.Message.obtain(null, message.ordinal(), obj));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Cannot send MSG to activity. Activity did not provide a Messenger!");
        }
    }

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
        BluetoothConnectToDeviceThread btThread = new BluetoothConnectToDeviceThread(mRaspberryPiBluetoothDevice, mBluetoothConnectorMessenger);
        btThread.start();
    }

    @Override
    public void disconnect() {
        if(mBluetoothSpiderConnectionThread != null)
            mBluetoothSpiderConnectionThread.cancel();
    }

    @Override
    public void send_getSpiderInfo() {

    }

    @Override
    public void send_requestCameraImage() {
        if(mBluetoothSpiderConnectionThread != null)
            mBluetoothSpiderConnectionThread.sendSpiderInstruction(SpiderInstruction.requestCameraImage);
    }

    @Override
    public void send(SpiderInstruction instruction) {
        if(mBluetoothSpiderConnectionThread != null)
            mBluetoothSpiderConnectionThread.sendSpiderInstruction(instruction);
    }

    @Override
    public void send_move(int direction) {

    }

    @Override
    public void send_executeScript(int scriptIndex) {

    }
}
