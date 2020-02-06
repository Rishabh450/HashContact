package com.example.litereria;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewDetails extends RecyclerView.Adapter<RecyclerViewDetails.DetailsViewHolder> {
    ArrayList<Map<String,String>> contacts=new ArrayList<Map<String, String>>();
    int lastPosition=-1;
    Context context;

    public RecyclerViewDetails(ArrayList<Map<String, String>> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.singledetail,parent,false);
        RecyclerViewDetails.DetailsViewHolder chatListViewHolder=new RecyclerViewDetails.DetailsViewHolder(v);
        Log.d("createhua", "here");

        return chatListViewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
        Log.d("bindme", String.valueOf(contacts));

            holder.detailname.setText(contacts.get(position).get("detailname"));
            holder.detail.setText(contacts.get(position).get("detail"));
            if (lastPosition == getItemCount() - 1)
                lastPosition = -1;
            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.layout_animation
                            : R.anim.item_animation_fall_down);
            holder.itemView.startAnimation(animation);
            lastPosition = position;


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class DetailsViewHolder extends RecyclerView.ViewHolder
    {
        TextView detailname,detail;

        public DetailsViewHolder(@NonNull View itemView) {

            super(itemView);
            detailname=itemView.findViewById(R.id.detailname);
            detail=itemView.findViewById(R.id.detail);
        }
    }
}
