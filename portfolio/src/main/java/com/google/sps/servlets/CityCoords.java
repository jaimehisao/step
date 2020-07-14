package com.google.sps.servlets;

public class CityCoords {
    private final String city;
    private final double latitude;
    private final double longitude;

    // Constructor to use when comment is created
    public CityCoords(String city, double latitude, double longitude){
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCity(){
        return city;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

}