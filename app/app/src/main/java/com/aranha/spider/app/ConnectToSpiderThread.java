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

    private final Messenger mMessenger;

    private BluetoothSocket mBluetoothSocket;
    private Socket mWifiSocket;

    BluetoothDevice mBluetoothDevice;
    private String mHostName;
    private int mPort;

    public ConnectToSpiderThread(BluetoothDevice device, Messenger messenger) {
        mMessenger = messenger;
        mBluetoothDevice = device;
    }

    public ConnectToSpiderThread(String hostName, int port, Messenger messenger) {
        mMessenger = messenger;
        mHostName = hostName;
        mPort = port;
    }

    @Override
    public void run() {

        try {

            if(mBluetoothDevice != null) {
                // Get a BluetoothSocket to connect with the given BluetoothDevice
                try {
                    mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("9d7debbc-c85d-11d1-9eb4-006008c3a19a"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mBluetoothSocket.connect();
                Thread spiderConnectionThread = new SpiderConnectionThread(mBluetoothSocket, mMessenger);
                spiderConnectionThread.start();
            }
            else if(mHostName != null) {
                try {
                    mWifiSocket = new Socket(mHostName, mPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread spiderConnectionThread = new SpiderConnectionThread(mWifiSocket, mMessenger);
                spiderConnectionThread.start();
            }
            // Do work to manage the connection (in a separate thread)
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
