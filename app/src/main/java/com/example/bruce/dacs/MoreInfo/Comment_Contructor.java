package com.example.bruce.dacs.MoreInfo;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by BRUCE on 8/31/2017.
 */

public class Comment_Contructor {
    public    String userID;
    public int locationID;
    public String userName;
    public String userImage;
    public String comment;
    public   String date;
    public   int like = 0;
    public ArrayList<String> commentImages;



    public Comment_Contructor() {

    }

    public Comment_Contructor(String userID, int locationID, String userName, String userImage, String comment, String date, int like) {
        this.userID = userID;
        this.locationID = locationID;
        this.userName = userName;
        this.userImage = userImage;
        this.comment = comment;
        this.date = date;
        this.like = like;
    }

    public Comment_Contructor(ArrayList<String> commentImages) {
        this.commentImages = commentImages;
    }

    public Comment_Contructor(String userID, int locationID, String userName, String userImage, String comment, String date, int like, ArrayList<String> commentImages) {
        this.userID = userID;
        this.locationID = locationID;
        this.userName = userName;
        this.userImage = userImage;
        this.comment = comment;
        this.date = date;
        this.like = like;
        this.commentImages = commentImages;
    }
}
