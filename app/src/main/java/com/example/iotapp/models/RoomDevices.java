package com.example.iotapp.models;

import java.util.List;

public class RoomDevices {
    private List<Device> sensors;
    private List<Device> leds;

    public RoomDevices(List<Device> sensors, List<Device> leds) {
        this.sensors = sensors;
        this.leds = leds;
    }

    public List<Device> getSensors() {
        return sensors;
    }

    public void setSensors(List<Device> sensors) {
        this.sensors = sensors;
    }

    public List<Device> getLeds() {
        return leds;
    }

    public void setLeds(List<Device> leds) {
        this.leds = leds;
    }
}
