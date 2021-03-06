package com.aranha.spider.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by Rutger on 20-05-14.
 */
public abstract class SpiderControllerService extends Service implements SpiderController {
    private static final String TAG = "SpiderControllerService";

    public enum SocketState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    public abstract void discoverDevices();
    public abstract void setCameraEnabled(MainActivity mainActivity, boolean value);

    /**
     * The activity which connects to this service can receive
     * messages by providing a Messenger as extra data when binding.
     */
    protected Messenger mActivityMessenger;

    public void setActivityMessenger(Messenger messenger) {
        this.mActivityMessenger = messenger;
    }

    /**
     * Sends a message to the last activity which connected to this service
     * @param message a message related to the spider
     */
    public void sendMessageToActivity(SpiderMessage message) {
        if (mActivityMessenger != null) {
            //Log.d(TAG, "Sending message to activity: " + message);
            try {
                mActivityMessenger.send(android.os.Message.obtain(null, message.ordinal(), 0, 0));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Cannot send MSG to activity. Activity did not provide a Messenger! " + message.toString());
        }
    }


    public void sendMessageToActivity(SpiderMessage message, Object obj) {
        if (mActivityMessenger != null) {
            Log.d(TAG, "Sending Object message to activity: " + message);
            try {
                mActivityMessenger.send(android.os.Message.obtain(null, message.ordinal(), obj));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Cannot send MSG to activity. Activity did not provide a Messenger!");
        }
    }


    /**
     * The binder which Activities (clients) use to communicate with this service.
     */
    protected final IBinder mBinder = new SpiderControllerServiceBinder();
    public class SpiderControllerServiceBinder extends Binder {
        public SpiderControllerService getService() {
            return SpiderControllerService.this;
        }
    }

    /**
     * When an activity binds to this service this gets called.
     * @return The this instance.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
