package com.example.litereria.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Seen implements Parcelable {
    String userid,time;

    public Seen(String userid, String time) {
        this.userid = userid;
        this.time = time;
    }

    protected Seen(Parcel in) {
        userid = in.readString();
        time = in.readString();
    }

    public static final Creator<Seen> CREATOR = new Creator<Seen>() {
        @Override
        public Seen createFromParcel(Parcel in) {
            return new Seen(in);
        }

        @Override
        public Seen[] newArray(int size) {
            return new Seen[size];
        }
    };

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(time);

    }
}
