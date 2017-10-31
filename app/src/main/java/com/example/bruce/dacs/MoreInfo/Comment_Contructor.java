package com.example.bruce.dacs.MoreInfo;

import java.util.ArrayList;

/**
 * Created by BRUCE on 8/31/2017.
 */

public class Comment_Contructor {
    public    String userID;
    public String locationID;
    public String userName;
    public String comment;
    public   String date;
    public   String like;
    public ArrayList<String> commentImages;

    public Comment_Contructor() {

    }

    public Comment_Contructor(String userID, String locationID, String userName, String comment, String date, String like, ArrayList<String> commentImages) {
        this.userID = userID;
        this.locationID = locationID;
        this.userName = userName;
        this.comment = comment;
        this.date = date;
        this.like = like;
        this.commentImages = commentImages;
    }
}
