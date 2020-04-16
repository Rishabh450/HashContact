package com.rishabh.hashcontact;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rishabh.hashcontact.Support.Details;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    public void onBindViewHolder(@NonNull final ChatListViewHolder chatListViewHolder, final int i) {
        lastPosition=-1;
        Log.d("bhai", "onBindViewHolder: "+i+" "+contacts);
        final HashMap<String, String>[] data = new HashMap[]{new HashMap<String, String>()};
        String email = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        final FirebaseDatabase[] database = {FirebaseDatabase.getInstance()};
       DatabaseReference databaseReference= database[0].getReference();
       databaseReference.child(contacts.get(i).get("key")).child("Personal").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               HashMap<String ,String> mp1=new HashMap<>();
               mp1=(HashMap<String, String>) dataSnapshot.getValue();
               Log.d("babes", String.valueOf(mp1));
               data[0] =mp1;


           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
        databaseReference.child(contacts.get(i).get("key")).child("Personal").child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String namer =dataSnapshot.getValue(String.class);
                chatListViewHolder.title.setText(namer);
                Log.d("stringwa",namer);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String[] uri = new String[1];
        databaseReference.child(contacts.get(i).get("key")).child("Personal").child("Photo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String namer =dataSnapshot.getValue(String.class);
                uri[0] =namer;
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context );
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f) ;
                circularProgressDrawable.start();
                // chatListViewHolder.title.setText(name[0]);

               // Log.e("ak47", "onBindViewHolder: "+contacts.get(i).get("Name") );
                Glide.with(context)
                        .load(namer)
                        .placeholder(R.mipmap.placeholder).into(chatListViewHolder.cover);

             //   Log.d("stringwa",namer);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        data[0].put("Key",contacts.get(i).get("key"));


        final Drawable[] img = new Drawable[1];
        chatListViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context );
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(100f) ;
                circularProgressDrawable.start();

                Glide.with(context)
                        .load(uri[0])
                        .placeholder(circularProgressDrawable)

                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                img[0] =resource;
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageview1);
                photoView.setImageDrawable(img[0]);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
      //  data.put("Source","1");

        chatListViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Details.class);
                data[0].put("key",contacts.get(i).get("key"));
            intent.putExtra("Data",data[0]);
                context.startActivity(intent);
            }
        });



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
