package com.aranha.spider.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

/**
 * Tries to establish a connection via bluetooth.
 * If successful it will start another thread which handles all the messages.
 */
public class ConnectToSpiderThread extends Thread {
    private static final String TAG = "BluetoothThread";

    private UUID MY_UUID = UUID.randomUUID();
    private final BluetoothSocket mBluetoothSocket;
    private final Socket mWifiSocket;

    private final Messenger mMessenger;

    public ConnectToSpiderThread(BluetoothDevice device, Messenger messenger) {
        BluetoothSocket tmp = null;
        mMessenger = messenger;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("9d7debbc-c85d-11d1-9eb4-006008c3a19a"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBluetoothSocket = tmp;
        mWifiSocket = null;
    }

    public ConnectToSpiderThread(String hostName, int port, Messenger messenger) {
        Socket tmp = null;
        mMessenger = messenger;

        try {
            tmp = new Socket(hostName, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBluetoothSocket = null;
        mWifiSocket = tmp;
    }


    public void run() {

        try {
            mBluetoothSocket.connect();

            // Do work to manage the connection (in a separate thread)
            Thread bluetoothToSpiderThread = new BluetoothSpiderConnectionThread(mBluetoothSocket, mMessenger);
            bluetoothToSpiderThread.start();
            System.out.println("Starting spin connection thread.");

        } catch (IOException closeException) {

            Log.d(TAG, "!!! Unable to connect to socket." + closeException.getMessage());
            try {
                mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.CONNECTING_FAILED.ordinal()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            if(mBluetoothSocket != null)
                mBluetoothSocket.close();
            if(mWifiSocket != null)
                mWifiSocket.close();

        } catch (IOException e) { }
    }
}
