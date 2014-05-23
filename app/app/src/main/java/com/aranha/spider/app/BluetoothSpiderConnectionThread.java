package com.aranha.spider.app;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
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
        String test = "De app is connected.";
        writeBase64(test);
        //
        //  !!!!!!!!!!!!!!!!!!!!!!!!!!! TEST TEST TEST
        //
    }


    public static final int PACKET_ID_IMAGE = 1;
    public static final int PACKET_ID_SENSOR = 2;
    public static final int PACKET_ID_VISIONSCRIPTS = 3;

    public static int PACKET_SIZE = 990;
    public void run() {

        byte[] buffer = new byte[PACKET_SIZE];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    int messageType = buffer[0];
                    int packetCount = buffer[1];

                    if(bytes == 2)
                        handleMessage(mmInStream, messageType, packetCount);

                } catch (IOException e) {

                    if(isDisconnectedByUser) {
                        Log.e(TAG, "Disconnected from the Raspberry Pi.");
                        mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.CONNECTION_CLOSED.ordinal(), 0, 0));
                    }
                    else { // Connection lost by some other circumstance.
                        Log.e(TAG, "Connection lost to Raspberry Pi.");
                        mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.CONNECTION_LOST.ordinal(), 0, 0));
                    }
                    break;
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
                Log.d(TAG, "RemoteException: Connection lost to Raspberry Pi." + e1.getMessage());
            }
        }
    }

    public void handleMessage(InputStream mmInStream, int messageType, int packetCount) throws IOException, RemoteException {
        byte[] buffer = new byte[PACKET_SIZE];
        int bytes;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);

        for(int i = 0; i < packetCount; i++) {
            bytes = mmInStream.read(buffer);
            byteArrayOutputStream.write(buffer, 0, bytes);
        }

        try {
            switch (messageType) {
                case PACKET_ID_IMAGE:
                    byte[] decodedString = Base64.decode(byteArrayOutputStream.toString(), Base64.NO_PADDING);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.READ_IMAGE.ordinal(), bitmap));
                    break;
                case PACKET_ID_VISIONSCRIPTS:
                    String[] scriptList = Base64.decode(byteArrayOutputStream.toString(), Base64.NO_PADDING).toString().split(";");
                    mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.READ_SCRIPT_LIST.ordinal(), scriptList));
                    break;
                case PACKET_ID_SENSOR:
                    // TODO: Write code
                    break;
            }
        } catch (IllegalArgumentException iae) {
            Log.e(TAG, "Unable to decompile message with base64. " + iae.getMessage());
        }
    }


    /**
     * Send an instruction to the spider
     * @param instruction The instruction to send.
     */
    public void sendSpiderInstruction(SpiderInstruction instruction) {
        write(Base64.encode(instruction.toString().getBytes(), Base64.DEFAULT));
    }

    /**
     * Called from the main activity to send a string encoded with Base64.
     * @param input string
     */
    public void writeBase64(String input) {
        write(Base64.encode(input.getBytes(), Base64.DEFAULT));
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
