package com.aranha.spider.app;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handles all the communication between the Raspberry Pi and the App.
 */
public class BluetoothSpiderConnectionThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    private final Messenger mMessenger;

    /**
     * Gets an already connected socket and sets up the communication.
     * @param socket A connected socket.
     * @param messenger Message handler.
     */
    public BluetoothSpiderConnectionThread(BluetoothSocket socket, Messenger messenger) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mMessenger = messenger;

        try {
            // Get the input and output streams, using temp objects because member streams are final
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        try {
            Message msg = new Message();
            msg.what = BluetoothService.MSG_CONNECTED_TO_RASPBERRYPI;
            msg.obj = this;
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //
        //  !!!!!!!!!!!!!!!!!!!!!!!!!!! TEST TEST TEST
        //
        String test = "De app is connected. altijd connected. je moet gewoon connected zijn. ja toch? hallo hallo";
        writeBase64(test);
        //
        //  !!!!!!!!!!!!!!!!!!!!!!!!!!! TEST TEST TEST
        //
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);

                // Send the obtained bytes to the bluetooth service
                try {
                    mMessenger.send(Message.obtain(null, BluetoothService.MSG_READ, bytes, -1, buffer));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {

                try {
                    Log.d("BluetoothSpiderConnectionThread", "Connection lost to Raspberry Pi.");
                    mMessenger.send(Message.obtain(null, BluetoothService.MSG_CONNECTION_CLOSED, 0, 0));
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }

                break;
            }
        }
    }

    /**
     * Called from the main activity to send a string encoded with Base64.
     * @param input
     */
    public void writeBase64(String input) {
        write(Base64.encode(input.getBytes(), Base64.NO_PADDING));
    }

    /**
     * Write to the output stream.
     * @param bytes
     */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /**
     * Called from the main activity to close the connection.
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
