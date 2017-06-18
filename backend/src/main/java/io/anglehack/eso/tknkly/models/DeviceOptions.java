package io.anglehack.eso.tknkly.models;

/**
 * Created by root on 6/18/17.
 */
public enum DeviceOptions {

    SEND_TYPE("sendType", "Which system the data is sent through."),
    SATORI_CONFIG("satori", "The satori config file."),
    DATA_SEND("send", "Boolean if the data should be sent default is false."),
    USER_ID("userId", "User id that the data read from device is associated with."),
    DEVICE_HAND_OVERWRITE("deviceHandOverwrite", "Devices has a default but overwrite is easier to have in java.");

    private String param;
    private String description;
    DeviceOptions(String param, String description) {
        this.description = description;
    }

    public String getParam() {
        return param;
    }

    public String getDescription() {
        return description;
    }
}
