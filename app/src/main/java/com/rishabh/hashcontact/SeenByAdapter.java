package com.rishabh.hashcontact;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.rishabh.hashcontact.Models.Seen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class SeenByAdapter extends RecyclerView.Adapter<SeenByAdapter.SeenHolder> {
    ArrayList<Seen> seen=new ArrayList<>();
    Context context;

    public SeenByAdapter(ArrayList<Seen> seen, Context context) {
        this.seen = seen;
        this.context = context;
    }

    @NonNull
    @Override
    public SeenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.seenby_item,parent,false);
        SeenHolder seenHolder=new SeenHolder(v);
        return seenHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final SeenHolder holder, int position) {
        DatabaseReference reference= FirebaseDatabase.getInstance() .getReference().child(seen.get(position).getUserid()).child("Personal");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("Name").getValue(String.class);
                String url=dataSnapshot.child("Photo").getValue(String.class);
                holder.seenname.setText(name);
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(10f) ;
                circularProgressDrawable.start();
                Glide.with(context)
                        .load(url)
                        .placeholder(circularProgressDrawable)
                        .apply(RequestOptions.circleCropTransform())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                return false;
                            }
                        })
                        .into(holder.seenimg);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.timeseen.setText(getTimeAgo(Long.parseLong(seen.get(position).getTime())));


    }

    @Override
    public int getItemCount() {
        return seen.size();
    }

    public class SeenHolder extends RecyclerView.ViewHolder{
        ImageView seenimg;
        TextView seenname,timeseen;

        public SeenHolder(@NonNull View itemView) {
            super(itemView);
            seenimg=itemView.findViewById(R.id.seen_pic);
            seenname=itemView.findViewById(R.id.seenname);
            timeseen=itemView.findViewById(R.id.timeseen);
        }
    }
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS ;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now =System.currentTimeMillis();;


        long diff = now - time;
        if(diff>0) {


            if (diff < MINUTE_MILLIS) {
                return (diff/SECOND_MILLIS)+"s";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "1m";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "1h";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS +"h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + "d";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "1w";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + "w";
            } else {
                java.util.Date date = new java.util.Date((long) time);
                return date.toString();
            }

        }
        else {

            diff = time - now;
            if (diff < MINUTE_MILLIS) {
                return "this minute";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute later";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes later";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour later";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours later";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "tomorrow";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + " days later";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "a week later";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + " weeks later";
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));

                return dateString;
            }
        }}
}
