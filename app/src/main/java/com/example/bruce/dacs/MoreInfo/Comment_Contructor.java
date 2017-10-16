package com.example.bruce.dacs.MoreInfo;

/**
 * Created by BRUCE on 8/31/2017.
 */

public class Comment_Contructor {
    public   String userID;
    public String locationID;
    public String userName;
    public String comment;
    public   String date;
    public   String like;

    public Comment_Contructor() {

    }

    public Comment_Contructor(String user_ID, String locationID, String userName, String comment, String date, String like) {
        this.userID = user_ID;
        this.locationID = locationID;
        this.userName = userName;
        this.comment = comment;
        this.date = date;
        this.like = like;
    }
}
