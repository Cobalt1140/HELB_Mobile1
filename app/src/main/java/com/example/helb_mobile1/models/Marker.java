package com.example.helb_mobile1.models;

public class Marker {
    //TODO idk about this I dont really wanna deal with this
    public Double lat;
    public Double lng;
    public String username;
    public long timestamp;

    public Marker(Double lat, Double lng, String username, long timestamp){
        this.lat = lat;
        this.lng = lng;
        this.username = username;
        this.timestamp = timestamp;
    }
}
