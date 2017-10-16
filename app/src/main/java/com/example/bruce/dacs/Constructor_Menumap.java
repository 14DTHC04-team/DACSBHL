package com.example.bruce.dacs;

/**
 * Created by BRUCE on 5/12/2017.
 */

public class Constructor_Menumap {
    public String LocationName;

    public String Address;
    public String LocationImg;

    public String Distance;
    public Constructor_Menumap() {
    }

    public Constructor_Menumap(String locationName, String address, String locationImg, String distance) {
        LocationName = locationName;
        Address = address;
        LocationImg = locationImg;
        Distance = distance;
    }
}
