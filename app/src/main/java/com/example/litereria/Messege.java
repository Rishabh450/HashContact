package com.example.litereria;

public class Messege implements Comparable<Messege>{

    public String messege;
    public boolean sent;
    public long time;
    public Messege(String messege, long time, boolean sent){
        this.messege=messege;
        this.sent=sent;
        this.time=time;
    }
    @Override
    public int compareTo(Messege o) {
        if(this.time<o.time)
            return 1;
        else if(this.time>o.time)
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {

        Messege messege=(Messege) obj;
        return this.messege.equals(messege.messege)&&this.sent==messege.sent&&this.time==messege.time;
    }
}
