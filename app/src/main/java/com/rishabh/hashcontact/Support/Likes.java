package com.rishabh.hashcontact.Support;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.rishabh.hashcontact.PostLikeAdapter;
import com.rishabh.hashcontact.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Likes extends AppCompatActivity {
    String feedid;
    DatabaseReference reference;
    RecyclerView likeList;
    PostLikeAdapter recyclerViewChatList;
    ArrayList<String> contacts=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        Intent intent=new Intent();
        Bundle bundle = getIntent().getExtras();
         feedid = bundle.getString("feedid");
        init();
        getdata();

    }
    public void init()
    {
        reference=FirebaseDatabase.getInstance().getReference().child("Feeds").child(feedid).child("likes");
        likeList=findViewById(R.id.likeList);
        likeList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Log.d("choka", "onDataChange: " + contacts);



        recyclerViewChatList = new PostLikeAdapter(contacts, getApplicationContext());
        likeList.setAdapter(recyclerViewChatList);

    }
    public void getdata()
    {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    contacts.clear();
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                        String id=dataSnapshot1.getKey();
                        contacts.add(id);

                    }
                    recyclerViewChatList.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
}
