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
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rutger on 10-05-2014.
 */
public class BluetoothService extends Service {

    public static final int MSG_RASPBERRYPI_FOUND = 1;
    public static final int MSG_CONNECTED_TO_RASPBERRYPI = 2;
    public static final int MSG_CONNECTING_FAILED = 3;
    public static final int MSG_CONNECTION_CLOSED = 4;

    public static final int MSG_READ = 5;

    /**
     * The activity which connects to this service can receive
     * messages by providing a Messenger as extra data when binding.
     */
    private Messenger mMessageReceiver;


    /**
     * The binder which clients use to communicate with this bluetooth service.
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

//----------------------------------------------------------------
//-------------Set up bluetooth-----------------------------------
//----------------------------------------------------------------

    private enum SocketState {
        INIT,
        CONNECTED,
        LISTENING,
        CLOSED,
    }
    private SocketState mState = SocketState.CLOSED;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mRaspberryPiBluetoothDevice;
    private BluetoothSpiderConnectionThread bluetoothSpiderConnectionThread;

    private String mRaspberryPiName = "raspberrypi-0";

    @Override
    public void onCreate() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBluetoothReceiver, filter); // Don't forget to unregister in onDestroy()
        System.out.println("Bluetooth receiver registered.");
        Log.d("BluetoothService","Bluetooth receiver registered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);
    }

    public void setRaspberryPiName(String newName) {
        mRaspberryPiName = newName;
    }

    /**
     * Every time a bluetooth mBluetoothDevice is found the onReceive() function gets executed.
     */
    final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        final List<String> ss = new ArrayList<String>();
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // Get the BluetoothDevice object from the Intent
                ss.add(device.getName() + "\n" + device.getAddress()); // Add the name and address to an array adapter to show in a ListView
                Log.d("BluetoothService", device.getName() + " - " + device.getAddress() + " Looking for " + mRaspberryPiName);

                if(device.getName()!= null && device.getName().equals(mRaspberryPiName)) {
                    onRaspberryPiFound(device);
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
        mState = SocketState.INIT;
        mBluetoothAdapter.cancelDiscovery();
        sendMessageToActivity(MSG_RASPBERRYPI_FOUND);
    }

    /**
     * The message handler. This receives all the incoming messages from the Raspberry Pi.
     */
    final Messenger mBluetoothConnectorMessenger = new Messenger(new BluetoothConnectionMessenger());
    class BluetoothConnectionMessenger extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {

                case BluetoothService.MSG_CONNECTED_TO_RASPBERRYPI:

                    if(msg.obj != null && msg.obj.getClass() == BluetoothSpiderConnectionThread.class) {
                        bluetoothSpiderConnectionThread = (BluetoothSpiderConnectionThread)msg.obj;
                        Log.d("BluetoothService", "Bluetooth connection thread estblished");
                    }
                    mState = SocketState.CONNECTED;
                    sendMessageToActivity(MSG_CONNECTED_TO_RASPBERRYPI);
                    Toast.makeText(BluetoothService.this, "Connected to the spider!", Toast.LENGTH_LONG).show();
                    Intent intentMain =  new Intent(BluetoothService.this, MainActivity.class);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentMain);
                    break;

                case BluetoothService.MSG_READ:
                    String in = new String(Base64.decode((byte[]) msg.obj, Base64.NO_PADDING));
                    System.out.println("Received: " + in);
                    break;

                case BluetoothService.MSG_CONNECTING_FAILED:
                    Toast.makeText(BluetoothService.this, "Failed to connect to the spider!", Toast.LENGTH_LONG).show();
                    mRaspberryPiBluetoothDevice = null;
                    bluetoothSpiderConnectionThread = null;
                    mState = SocketState.CLOSED;
                    sendMessageToActivity(MSG_CONNECTING_FAILED);
                    break;

                case BluetoothService.MSG_CONNECTION_CLOSED:

                    mRaspberryPiBluetoothDevice = null;
                    bluetoothSpiderConnectionThread = null;
                    mState = SocketState.CLOSED;
                    // Return to the first activity
                    Intent intentConnect =  new Intent(BluetoothService.this, ConnectActivity.class);
                    intentConnect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentConnect);
                    break;
            }
        }
    }

    private void sendMessageToActivity(int message) {

        if(mMessageReceiver != null) {
            Log.d("BluetoothService", "Sending message to activity: " + message);
            try {
                mMessageReceiver.send(Message.obtain(null, message, 0,0 ));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("BluetoothService", "Cannot send MSG to activity. Activity did not provide a Messenger!");
        }
    }




//----------------------------------------------------------------
//-------------Spider functies------------------------------------
//----------------------------------------------------------------

    public void discoverBluetoothDevices() {
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
        Log.d("BluetoothService", "Discovering bluetooth devices!");
    }

    /**
     * Connect to the Raspberry Pi with a known MAC address.
     * @param MACAddress string
     */
    public void manualConnect(String MACAddress) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MACAddress);
        onRaspberryPiFound(device);
    }

    public void connect() {
        BluetoothThread btThread = new BluetoothThread(mRaspberryPiBluetoothDevice, mBluetoothConnectorMessenger);
        btThread.start();
    }


    /**
     * VOORBEELD FUNCTIE!!!!!!!!!!!
     */
    public void spider_MoveLeft() {
        if(bluetoothSpiderConnectionThread != null) {
            Log.d("BluetoothService -> bluetooth -> spider",  " moveLeft");
            bluetoothSpiderConnectionThread.writeBase64("Move Left");
        }
    }
    public void spider_Dance() {
        if(bluetoothSpiderConnectionThread != null) {
            Log.d("BluetoothService -> bluetooth -> spider",  " dance");
            bluetoothSpiderConnectionThread.writeBase64("Ga dancen, bitch");
        }
    }

}
