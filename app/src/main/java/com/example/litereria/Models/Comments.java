package com.example.litereria.Models;


public class Comments {

    public String sender;
    public String message;
    public String time;
    public String sender_image_url;

    public Comments(){

    }
    public Comments(String sender, String message,String time,String sender_pic_url) {
        this.sender = sender;
        this.message = message;
        this.time=time;
        this.sender_image_url=sender_pic_url;
    }

    public void setSender_image_url(String sender_image_url) {
        this.sender_image_url = sender_image_url;
    }

    public String getSender_image_url() {
        return sender_image_url;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
