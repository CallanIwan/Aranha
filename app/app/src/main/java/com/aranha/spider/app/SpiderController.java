package com.aranha.spider.app;

/**
 * Created by Rutger on 13-05-14.
 */
public interface SpiderController {

    public enum SocketState {
        INIT,
        CONNECTED,
        LISTENING,
        CLOSED,
    }

    public void connect();
    public void disconnect();

    public void send_moveLeft();
    public void send_moveRight();
    public void send_moveForward();
    public void send_moveBackwards();

    public void send_moveUp();
    public void send_moveDown();

    public void send_dance();
    public void send_resetToDefaultPosition();

    public void send_executeScript(int scriptIndex);
}
