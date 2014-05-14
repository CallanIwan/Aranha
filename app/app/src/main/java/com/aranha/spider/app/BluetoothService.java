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

    /**
     * The activity which connects to this service can receive
     * messages by providing a Messenger as extra data when binding.
     */
    private Messenger mMessageReceiver;


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
        this.mMessageReceiver = intent.getParcelableExtra("messageReceiver");
        if(mMessageReceiver == null) {
            Log.d("BluetoothService", "Activity bound to the service but did not send a MessageReceiver");
        }

        Log.d("BluetoothService", "New activity bound to this service");

        return mBinder;
    }


    // --------------------------------------------
    //    Set up bluetooth
    // --------------------------------------------

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mRaspberryPiBluetoothDevice;
    private BluetoothSpiderConnectionThread bluetoothSpiderConnectionThread;
    private String mRaspberryPiName = "raspberrypi-0";

    @Override
    public void onCreate() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Register bluetooth device-discovery.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBluetoothReceiver, filter); // Don't forget to unregister in onDestroy()
        Log.d("BluetoothService","Bluetooth receiver registered");
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
                Log.d("BluetoothService", "New Device: " + device.getName() + " - " + device.getAddress() + " Looking for " + mRaspberryPiName);

                if(device.getName()!= null && device.getName().equals(mRaspberryPiName)) {
                    onRaspberryPiFound(device);
                }
            }
        }
    };

    public void discoverBluetoothDevices() {
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
        Log.d("BluetoothService", "Discovering bluetooth devices!");
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
                        Log.d("BluetoothService", "Bluetooth connection thread estblished");
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
                    String in = new String(Base64.decode((byte[]) msg.obj, Base64.NO_PADDING));
                    System.out.println("Received: " + in);
                    break;
            }
        }
    }

    private void sendMessageToActivity(SpiderMessage message) {

        if (mMessageReceiver != null) {
            Log.d("BluetoothService", "Sending message to activity: " + message);
            try {
                mMessageReceiver.send(android.os.Message.obtain(null, message.ordinal(), 0, 0));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("BluetoothService", "Cannot send MSG to activity. Activity did not provide a Messenger!");
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
        BluetoothThread btThread = new BluetoothThread(mRaspberryPiBluetoothDevice, mBluetoothConnectorMessenger);
        btThread.start();
    }

    @Override
    public void disconnect() {
        if(bluetoothSpiderConnectionThread != null) {
            bluetoothSpiderConnectionThread.cancel();
        }
    }

    @Override
    public void send_move(int direction) {

    }

    @Override
    public void send_moveLeft() {
        if(bluetoothSpiderConnectionThread != null) {
            Log.d("BluetoothService -> bluetooth -> spider",  " moveLeft");
            bluetoothSpiderConnectionThread.writeBase64("Move Left");
        }
    }

    @Override
    public void send_moveRight() {
        if(bluetoothSpiderConnectionThread != null) {
            Log.d("BluetoothService -> bluetooth -> spider",  " dance");
            bluetoothSpiderConnectionThread.writeBase64("Ga dancen, bitch");
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
