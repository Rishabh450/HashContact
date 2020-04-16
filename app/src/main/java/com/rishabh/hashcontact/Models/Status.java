package com.rishabh.hashcontact.Models;

import java.util.ArrayList;

public class Status {
   public String status_id,stext,url,user;
   boolean isSeen;
   ArrayList<Seen> seen=new ArrayList<>();

    public Status(String status_id,String user,boolean isSeen,ArrayList<Seen> seen,String stext,String url) {
        this.status_id = status_id;
        this.stext=stext;
        this.url=url;

        this.user=user;
        this.isSeen=isSeen;
        this.seen=seen;

    }


    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getStext() {
        return stext;
    }

    public void setStext(String stext) {
        this.stext = stext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public void setSeen(ArrayList<Seen> seen) {
        this.seen = seen;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public ArrayList<Seen> getSeen() {
        return seen;
    }



    public String getUser() {
        return user;
    }
}
