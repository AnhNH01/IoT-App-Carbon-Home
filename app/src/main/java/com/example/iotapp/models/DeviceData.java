package com.example.iotapp.models;

import com.google.gson.annotations.SerializedName;

public class DeviceData {

    @SerializedName("_id")
    private String id;
    private String deviceId;
    private double value;
    private String time;
    private String name;

    public DeviceData(String id, String deviceId, double value, String time, String name) {
        this.id = id;
        this.deviceId = deviceId;
        this.value = value;
        this.time = time;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", value=" + value +
                ", time='" + time + '\'' +
                '}';
    }
}
