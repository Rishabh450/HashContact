package com.example.litereria;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.litereria.Support.Details;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class RecyclerViewChatList extends RecyclerView.Adapter<RecyclerViewChatList.ChatListViewHolder>{
    @NonNull
    ArrayList<Map<String,String>> contacts=new ArrayList<Map<String, String>>();
    Context context;int lastPosition;
    public RecyclerViewChatList(ArrayList<Map<String,String>> contacts, Context context)
    {
        this.contacts=contacts;
        this.context=context;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ChatListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.singlecontact,viewGroup,false);
        ChatListViewHolder chatListViewHolder=new ChatListViewHolder(v);
        return chatListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder chatListViewHolder, int i) {
        lastPosition=-1;
        Log.d("bhai", "onBindViewHolder: "+i+" "+contacts);
        final HashMap<String,String> data=new HashMap<String,String>();
        String email = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getEmail());

       // FirebaseDatabase database = FirebaseDatabase.getInstance();
      //  DatabaseReference databaseReference=database.getReference();

//        databaseReference.child(email.substring(0,email.indexOf('@'))).child("Contact").child(String.valueOf(contacts.get(i)));

        data.put("Name",contacts.get(i).get("Name"));
        data.put("photo",contacts.get(i).get("photo"));
        data.put("Key",contacts.get(i).get("key"));
        data.put("Codechef",contacts.get(i).get("Codechef"));
        data.put("Email",contacts.get(i).get("Email"));
        data.put("Facebook",contacts.get(i).get("Facebook"));
        data.put("HackerEarth",contacts.get(i).get("HackerEarth"));
        data.put("HackerRank",contacts.get(i).get("HackerRank"));
        data.put("Instagram",contacts.get(i).get("Instagram"));
        data.put("Twitter",contacts.get(i).get("Twitter"));
        data.put("github",contacts.get(i).get("github"));
        data.put("PhoneNumber",contacts.get(i).get("PhoneNumber"));
        data.put("Linkedin",contacts.get(i).get("Linkedin"));
      //  data.put("Source","1");
        CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context );
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f) ;
        circularProgressDrawable.start();
        chatListViewHolder.title.setText(contacts.get(i).get("Name"));
        Log.e("ak47", "onBindViewHolder: "+contacts.get(i).get("Name") );
        Glide.with(context)
                .load(contacts.get(i).get("photo"))
    .placeholder(circularProgressDrawable).into(chatListViewHolder.cover);
        chatListViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Details.class);
                intent.putExtra("Data",data);
                context.startActivity(intent);
            }
        });
        if(lastPosition==getItemCount()-1)
            lastPosition=-1;
        Animation animation = AnimationUtils.loadAnimation(context,
                (i > lastPosition) ? R.anim.layout_animation
                        : R.anim.item_animation_fall_down);
        chatListViewHolder.itemView.startAnimation(animation);
        lastPosition = i;


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        ImageView cover;
        TextView title;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView.findViewById(R.id.single_contact_item);
            cover=itemView.findViewById(R.id.sing);
            title=itemView.findViewById(R.id.singlecontactbookname);
        }
    }

}
