package com.aranha.spider.app;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Tries to establish a connection via bluetooth.
 * If successful it will start another thread which handles all the messages.
 */
public class BluetoothConnectToDeviceThread extends Thread {
    private static final String TAG = "BluetoothThread";

    private UUID MY_UUID = UUID.randomUUID();
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    private final Messenger mMessenger;

    public BluetoothConnectToDeviceThread(BluetoothDevice device, Messenger messenger) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        mMessenger = messenger;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // "9d7debbc-c85d-11d1-9eb4-006008c3a19a"
            // a8d11090-d9ad-11e3-9c1a-0800200c9a66
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("9d7debbc-c85d-11d1-9eb4-006008c3a19a"));

        } catch (IOException e) { }

        mmSocket = tmp;
    }

    public void run() {

        try {
            mmSocket.connect();

            // Do work to manage the connection (in a separate thread)
            Thread bluetoothToSpiderThread = new BluetoothSpiderConnectionThread(mmSocket, mMessenger);
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
            mmSocket.close();
        } catch (IOException e) { }
    }
}
