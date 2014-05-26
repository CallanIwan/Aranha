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
        CONNECTION_LOST, // Most of the time because of outside issues
        CONNECTION_CLOSED, // By user
        READ_MSG_FROM_RASPBERRYPI,
        READ_SCRIPT_LIST,
        READ_IMAGE,
    }

    public void setRaspberryPiName(String name);

    public void connect();
    public void disconnect();

    public void send_getSpiderInfo();
    public void send_requestCameraImage();

    public void send(SpiderInstruction instruction);
    public void send_move(int rotation);
    public void send_executeScript(int scriptIndex);
}
