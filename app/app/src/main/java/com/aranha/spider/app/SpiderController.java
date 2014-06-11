package com.aranha.spider.app;

/**
 * Created by Rutger on 13-05-14.
 */
public interface SpiderController {

    public static final SpiderMessage[] SpiderMessages = SpiderMessage.values();

    /**
     * The messages to send to the Connected Activity.
     */
    public enum SpiderMessage {
        BLUETOOTH_DEVICE_FOUND,
        RASPBERRYPI_FOUND,
        CONNECTING_TO_RASPBERRYPI,
        CONNECTING_FAILED,
        CONNECTED_TO_RASPBERRYPI,
        CONNECTION_LOST,
        CONNECTION_CLOSED, // Usually closed by the user
        READ_MSG_FROM_RASPBERRYPI,
        READ_SCRIPT_LIST, // The A.I. scripts like searching for balloons.
        READ_IMAGE,
    }

    public void setRaspberryPiName(String name);

    public void connect();
    public void disconnect();

    public void send(SpiderInstruction instruction);
    public void send(SpiderInstruction instruction, String extraData);
}
