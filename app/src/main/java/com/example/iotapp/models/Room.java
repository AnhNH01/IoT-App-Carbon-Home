package com.example.iotapp.models;

import com.google.gson.annotations.SerializedName;


public class Room {
    @SerializedName("_id")
    private String id;
    private String homeId;
    private String name;


    public Room(String id, String homeId, String name) {
        this.id = id;
        this.homeId = homeId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", homeId='" + homeId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
