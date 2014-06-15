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
import java.net.Socket;


/**
 * Handles all the communication between the Raspberry Pi and the App.
 */
public class SpiderConnectionThread extends Thread {
    private static final String TAG = "SpiderConnectionThread";

    private final BluetoothSocket mBluetoothSocket;
    private final Socket mWifiSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    private final Messenger mMessenger;
    private boolean isDisconnectedByUser = false;

    /**
     * Gets an already connected socket and sets up the communication.
     * @param socket A connected socket.
     * @param messenger Message handler.
     */
    public SpiderConnectionThread(BluetoothSocket socket, Messenger messenger) {
        mMessenger = messenger;
        mBluetoothSocket = socket;
        mWifiSocket = null;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            // Get the input and output streams, using temp objects because member streams are final
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        onConnected();

    }
    public SpiderConnectionThread(Socket socket, Messenger messenger) {
        mMessenger = messenger;
        mBluetoothSocket = null;
        mWifiSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        onConnected();
    }



    public void onConnected() {

        try {
            Message msg = new Message();
            msg.what = SpiderController.SpiderMessage.CONNECTED_TO_RASPBERRYPI.ordinal();
            msg.obj = this;
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //  !!!!!!!!!!!!!!!!!!!!!!!!!!! TEST TEST TEST
        String test = "De app is connected.";
        writeBase64(test);
    }

    public static final int PACKET_ID_IMAGE = 1;
    public static final int PACKET_ID_SENSOR = 2;
    public static final int PACKET_ID_VISION_SCRIPTS = 3;

    public static int PACKET_SIZE = 1024;

    boolean once = true;

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
                    int packetSize;

                    for(int i = 0; i < bytes; i++) {
                        if((buffer[i] & 0xff) == ((char) 0))  {

                            try {
                                packetSize = Integer.parseInt(new String(buffer, "UTF-8").substring(1, i));
                                handleMessage(mmInStream, messageType, packetSize);
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "NumberformatException");
                            }
                        }
                    }

                    if(once) {
                        sendSpiderInstruction(SpiderInstruction.move);
                        once=false;
                    }

                } catch (IOException e) {

                    if(isDisconnectedByUser) {
                        Log.e(TAG, "Disconnected from the Raspberry Pi.");
                        mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.CONNECTION_CLOSED.ordinal(), 0, 0));
                    }
                    else { // Connection lost by some other circumstance.
                        Log.e(TAG, "Connection lost to Raspberry Pi." + e.getMessage());
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

    public void handleMessage(InputStream mmInStream, int messageType, int packetSize) throws IOException, RemoteException {
        byte[] buffer = new byte[PACKET_SIZE];
        int bytes;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);

        Log.d(TAG, "Receiving a message: " + messageType + " - Packetsize: " + packetSize);

        while(byteArrayOutputStream.size() < packetSize) {
            bytes = mmInStream.read(buffer);
            byteArrayOutputStream.write(buffer, 0, bytes);
            //Log.d(TAG, " ?? " + byteArrayOutputStream.size() + " " + bytes + " - " + new String(buffer, "UTF-8").substring(0, bytes));
        }

        try {
            byte[] decodedByteString = Base64.decode(byteArrayOutputStream.toByteArray(), Base64.NO_PADDING);

            switch (messageType) {
                case PACKET_ID_IMAGE:
                    mMessenger.send(Message.obtain(null, SpiderController.SpiderMessage.READ_IMAGE.ordinal(), decodedByteString));
                    break;
                case PACKET_ID_VISION_SCRIPTS:
                    String[] scriptList = new String(decodedByteString).split(";");
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
    public void sendSpiderInstruction(SpiderInstruction instruction, String extraData) {

        Log.d(TAG, "Sending instruction: " + instruction.toString());
        String finalString;
        if(extraData.length() == 0)
            finalString = instruction.toString();
        else
            finalString = instruction + ";" + extraData;

        write(Base64.encode(finalString.getBytes(), Base64.DEFAULT));
    }
    public void sendSpiderInstruction(SpiderInstruction instruction) {
        //Log.d(TAG, "Sending instruction: " + instruction.toString().getBytes());
        write(Base64.encode(instruction.toString().getBytes(), Base64.DEFAULT));
    }

    /**
     * Send a string encoded with Base64.
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
            if(mBluetoothSocket != null)
                mBluetoothSocket.close();
            if(mWifiSocket != null)
                mWifiSocket.close();

        } catch (IOException e) { }
    }
}
