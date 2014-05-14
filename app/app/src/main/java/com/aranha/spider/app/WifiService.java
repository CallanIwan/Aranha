package com.aranha.spider.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by Rutger on 13-05-14.
 */
public class WifiService extends Service implements SpiderController {


    /**
     * The binder which Activities (clients) use to communicate with this bluetooth service.
     */
    private final IBinder mBinder = new WifiBinder();
    public class WifiBinder extends Binder {
        WifiService getService() {
            return WifiService.this;
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



    // --------------------------------------------
    //    Implemented SpiderController Methods
    // --------------------------------------------

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send_move(int direction) {

    }

    @Override
    public void send_moveLeft() {

    }

    @Override
    public void send_moveRight() {

    }

    @Override
    public void send_moveForward() {

    }

    @Override
    public void send_moveBackwards() {

    }

    @Override
    public void send_moveUp() {

    }

    @Override
    public void send_moveDown() {

    }

    @Override
    public void send_dance() {

    }

    @Override
    public void send_resetToDefaultPosition() {

    }

    @Override
    public void send_executeScript(int scriptIndex) {

    }
}
