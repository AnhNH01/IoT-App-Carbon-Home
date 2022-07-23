package com.example.iotapp.models;

import java.util.List;

public class DeviceDataResult {
    private List<DeviceData> result;

    public DeviceDataResult(List<DeviceData> result) {
        this.result = result;
    }

    public List<DeviceData> getResult() {
        return result;
    }

    public void setResult(List<DeviceData> result) {
        this.result = result;
    }
}
