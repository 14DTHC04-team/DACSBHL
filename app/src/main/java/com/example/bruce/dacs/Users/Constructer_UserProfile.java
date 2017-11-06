package com.example.bruce.dacs.Users;

/**
 * Created by BRUCE on 6/15/2017.
 */

public class Constructer_UserProfile {
    public String Email;
    public String Name;
    public String Image = "";
    public String Hobbie;


    public Constructer_UserProfile() {
    }

    public Constructer_UserProfile(String email, String name, String image, String hobbie) {
        Email = email;
        Name = name;
        Image = image;
        Hobbie = hobbie;
    }

    public Constructer_UserProfile(String email, String name, String image) {
        Email = email;
        Name = name;
        Image = image;
    }
}
