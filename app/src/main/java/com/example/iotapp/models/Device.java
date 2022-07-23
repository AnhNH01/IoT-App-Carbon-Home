package com.example.iotapp.models;

import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("_id")
    private String id;
    private DeviceType deviceType;
    private String roomId;

    public Device(String id, DeviceType deviceType, String roomId) {
        this.id = id;
        this.deviceType = deviceType;
        this.roomId = roomId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
