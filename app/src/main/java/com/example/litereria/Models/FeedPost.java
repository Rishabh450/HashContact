package com.example.litereria.Models;


import android.widget.LinearLayout;

import java.util.ArrayList;

public class FeedPost {

    public String event;
    public String subEvent;
    public String content;
    public String imageURL;
    public ArrayList<Likes> likes;
    public ArrayList<Comments> comments;
    public String postid;
    public String senderId;
    public  String sender_url;
    public boolean is_already_liked;

    public FeedPost(){

    }

    public FeedPost(boolean is_liked,String mpostid, String content,String event,  String imageURL, String subEvent,
                    ArrayList<Likes> mlikes,ArrayList<Comments> mcomments,String sender_url,String  senderid) {
        this.is_already_liked=is_liked;
        this.event = event;
        this.subEvent = subEvent;
        this.content = content;
        this.senderId=senderid;
        this.imageURL = imageURL;
        this.postid=mpostid;
        this.sender_url=sender_url;

        likes=new ArrayList<>();
        comments=new ArrayList<>();
        this.likes=mlikes;
        this.comments=mcomments;

    }

    public void setIs_already_liked(boolean is_already_liked) {
        this.is_already_liked = is_already_liked;
    }

    public boolean isIs_already_liked() {
        return is_already_liked;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostid() {
        return postid;
    }
    public String getSender_url() {
        return sender_url;
    }
    public String getSenderId() {
        return senderId;
    }



    public void setLikes(ArrayList<Likes> likes) {
        this.likes = likes;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
    }

    public ArrayList<Likes> getLikes() {
        return likes;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public String getEvent() {
        return event;
    }

    public String getSubEvent() {
        return subEvent;
    }

    public String getContent() {
        return content;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setSubEvent(String subEvent) {
        this.subEvent = subEvent;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}

