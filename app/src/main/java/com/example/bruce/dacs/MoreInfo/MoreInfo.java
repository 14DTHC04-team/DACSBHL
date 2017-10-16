package com.example.bruce.dacs.MoreInfo;

/**
 * Created by BRUCE on 5/16/2017.
 */

public class MoreInfo {
    public int infomation_ID;
    public int location_id;
    public String Image;
    public String Info;
    public MoreInfo() {
    }

    public MoreInfo(int infomation_ID, int location_id, String image, String info) {
        this.infomation_ID = infomation_ID;
        this.location_id = location_id;
        Image = image;
        Info = info;
    }
}
