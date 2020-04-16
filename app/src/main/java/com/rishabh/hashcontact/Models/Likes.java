package com.rishabh.hashcontact.Models;

public class Likes {
    public String user_id;

    public Likes(){

    }
    public Likes(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getUser_id() {
        return user_id;
    }

}
