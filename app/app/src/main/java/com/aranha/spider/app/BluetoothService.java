package com.aranha.spider.app;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rutger on 10-05-2014.
 */
public class BluetoothService extends Service implements SpiderController {
    private static final String TAG = "BluetoothService";

    /**
     * The activity which connects to this service can receive
     * messages by providing a Messenger as extra data when binding.
     */
    private Messenger mActivityMessenger;


    /**
     * The binder which Activities (clients) use to communicate with this bluetooth service.
     */
    private final IBinder mBinder = new BluetoothBinder();
    public class BluetoothBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
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
    //    Set up bluetooth
    // --------------------------------------------

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mRaspberryPiBluetoothDevice;
    private BluetoothSpiderConnectionThread bluetoothSpiderConnectionThread;
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

    public void setRaspberryPiBluetoothName(String newName) {
        mRaspberryPiName = newName;
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
                Log.d(TAG, "New Device: " + device.getName() + " - " + device.getAddress() + " Looking for " + mRaspberryPiName);

                if(device.getName()!= null && device.getName().equals(mRaspberryPiName)) {
                    onRaspberryPiFound(device);
                }

                if(discoveredDevicesAdapter.getPosition(device) != -1) {
                    discoveredDevicesAdapter.add(device);
                }
            }
        }
    };


    public void discoverBluetoothDevices() {
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
        Log.d(TAG, "Discovering bluetooth devices!");
    }

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
                        bluetoothSpiderConnectionThread = (BluetoothSpiderConnectionThread)msg.obj;
                        Log.d(TAG, "Bluetooth connection thread established");
                    }
                    sendMessageToActivity(SpiderMessage.CONNECTED_TO_RASPBERRYPI);
                    break;

                case CONNECTING_FAILED:
                    mRaspberryPiBluetoothDevice = null;
                    bluetoothSpiderConnectionThread = null;
                    sendMessageToActivity(SpiderMessage.CONNECTING_FAILED);
                    break;

                case CONNECTION_CLOSED:
                    mRaspberryPiBluetoothDevice = null;
                    bluetoothSpiderConnectionThread = null;
                    sendMessageToActivity(SpiderMessage.CONNECTION_CLOSED);
                    break;

                case CONNECTION_LOST:
                    mRaspberryPiBluetoothDevice = null;
                    bluetoothSpiderConnectionThread = null;
                    sendMessageToActivity(SpiderMessage.CONNECTION_LOST);
                    break;

                case READ_MSG_FROM_RASPBERRYPI:
                    String in = new String(Base64.decode((byte[]) msg.obj,Base64.NO_PADDING));
                   Log.d(TAG, "Received: " + in);
                    break;
            }
        }
    }




    // --------------------------------------------
    //    Implemented SpiderController Methods
    // --------------------------------------------

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
        if(bluetoothSpiderConnectionThread != null) {
            bluetoothSpiderConnectionThread.cancel();
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
        if(bluetoothSpiderConnectionThread != null) {
            bluetoothSpiderConnectionThread.writeBase64("Move Left");
        }
    }

    @Override
    public void send_moveRight() {
        if(bluetoothSpiderConnectionThread != null) {
            bluetoothSpiderConnectionThread.writeBase64("Ga dancen, bitch");
            bluetoothSpiderConnectionThread.sendSpiderInstruction(SpiderInstruction.move);
        }
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
