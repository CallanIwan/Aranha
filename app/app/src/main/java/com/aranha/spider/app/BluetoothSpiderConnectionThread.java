package com.aranha.spider.app;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Handles all the communication between the Raspberry Pi and the App.
 */
public class BluetoothSpiderConnectionThread extends Thread {
    private static final String TAG = "BluetoothSpiderConnectionThread";

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    private final Messenger mMessenger;
    private boolean isDisconnectedByUser = false;

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
            msg.what = SpiderController.SpiderMessage.CONNECTED_TO_RASPBERRYPI.ordinal();
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
                    mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.READ_MSG_FROM_RASPBERRYPI.ordinal(), bytes, -1, buffer));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {

                try {
                    if(isDisconnectedByUser) {
                        Log.d(TAG, "Disconnected from the Raspberry Pi.");
                        mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.CONNECTION_CLOSED.ordinal(), 0, 0));
                    }
                    else { // Connection lost by some other circumstance.
                        Log.d(TAG, "Connection lost to Raspberry Pi.");
                        mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.CONNECTION_LOST.ordinal(), 0, 0));
                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }

                break;
            }
        }
    }

    /**
     * Send an instruction to the spider
     * @param instruction The instruction to send.
     */
    public void sendSpiderInstruction(SpiderInstruction instruction) {
        write(Base64.encode(instruction.toString().getBytes(), Base64.NO_PADDING));
    }

    /**
     * Called from the main activity to send a string encoded with Base64.
     * @param input string
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
            isDisconnectedByUser = true;
            mmSocket.close();
        } catch (IOException e) { }
    }
}
