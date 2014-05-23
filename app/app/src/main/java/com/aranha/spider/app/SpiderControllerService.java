package com.aranha.spider.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * Created by Rutger on 20-05-14.
 */
public abstract class SpiderControllerService extends Service implements SpiderController {
    private static final String TAG = "SpiderControllerService";

    /**
     * Sends a message to the last activity which connected to this service
     * @param message a message related to the spider
     */
    public abstract void sendMessageToActivity(SpiderMessage message);


    public abstract void discoverDevices();

    /**
     * The activity which connects to this service can receive
     * messages by providing a Messenger as extra data when binding.
     */
    protected Messenger mActivityMessenger;

    public void setActivityMessenger(Messenger messenger) {
        this.mActivityMessenger = messenger;
    }

    /**
     * The binder which Activities (clients) use to communicate with this service.
     */
    protected final IBinder mBinder = new SpiderControllerServiceBinder();
    public class SpiderControllerServiceBinder extends Binder {
        SpiderControllerService getService() {
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
