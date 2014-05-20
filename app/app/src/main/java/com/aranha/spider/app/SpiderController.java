package com.aranha.spider.app;

/**
 * Created by Rutger on 13-05-14.
 */
public interface SpiderController {

    public static final SpiderMessage[] SpiderMessages = SpiderMessage.values();
    public enum SpiderMessage {
        BLUETOOTH_DEVICE_FOUND,
        RASPBERRYPI_FOUND,
        CONNECTING_TO_RASPBERRYPI,
        CONNECTING_FAILED,
        CONNECTED_TO_RASPBERRYPI,
        CONNECTION_LOST,
        CONNECTION_CLOSED,
        READ_MSG_FROM_RASPBERRYPI
    }

    public enum SocketState {
        INIT,
        CONNECTED,
        LISTENING,
        CLOSED,
    }

    public void connect();
    public void disconnect();

    public void sendMessageToActivity(SpiderMessage message);

    /**
     *
     */
    public void send_getSpiderInfo();

    public void send(SpiderInstruction instruction);

    public void send_move(int direction);
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
