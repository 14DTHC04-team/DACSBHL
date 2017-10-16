package com.example.bruce.dacs.BigMap;

/**
 * Created by BRUCE on 5/5/2017.
 */

public class Tourist_Location {

    public int location_ID;

    public String LocationName;
    public double Latitude;
    public double Longtitude;

    public String LocationImg;
    public String Address;
    public String BasicInfo;

    public int province_ID;

    public double Distance = 0.0;
    public Tourist_Location() {
    }

    public Tourist_Location(int location_ID, String locationName, double latitude, double longtitude, String locationImg, String address, String basicInfo, int province_ID, double distance) {
        this.location_ID = location_ID;
        LocationName = locationName;
        Latitude = latitude;
        Longtitude = longtitude;
        LocationImg = locationImg;
        Address = address;
        BasicInfo = basicInfo;
        this.province_ID = province_ID;
        Distance = distance;
    }
}

