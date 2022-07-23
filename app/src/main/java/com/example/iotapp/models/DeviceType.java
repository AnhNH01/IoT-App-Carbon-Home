package com.example.iotapp.models;

import com.google.gson.annotations.SerializedName;

public class DeviceType {
    private String name;
    private String description;

    public DeviceType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
