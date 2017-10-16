package com.example.bruce.dacs.Users;

/**
 * Created by BRUCE on 6/15/2017.
 */

public class Constructer_UserProfile {
    public String Email;
    public String Name;
    public String PhoneNumber;
    public String BirthDay;
    public String Image = "";
    public String UserLatitude = "";
    public String UserLongtitude = "";

    public Constructer_UserProfile() {
    }

    public Constructer_UserProfile(String email, String name, String phoneNumber, String birthDay) {
        Email = email;
        Name = name;
        PhoneNumber = phoneNumber;
        BirthDay = birthDay;
    }

    public Constructer_UserProfile(String image, String userLatitude, String userLongtitude) {
        Image = image;
        UserLatitude = userLatitude;
        UserLongtitude = userLongtitude;
    }
}
