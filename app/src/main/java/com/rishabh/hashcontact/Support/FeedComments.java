package com.rishabh.hashcontact.Support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.rishabh.hashcontact.CommentsAdapter;
import com.rishabh.hashcontact.R;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedComments extends AppCompatActivity {
    String feedid;
    DatabaseReference reference;
    RecyclerView commentlist;
    ArrayList<String> contacts=new ArrayList<String>();
    CommentsAdapter commentsAdapter;
    ImageView commenterpic;
    EditText mycomment;
    TextView send;
    String provider,currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_comments);
        Bundle bundle = getIntent().getExtras();
        feedid = bundle.getString("feedid");
        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                provider="facebook";
                currentUser= Profile.getCurrentProfile().getId();
            }
            else {
                provider = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser=userInfo.getUid();
            }
        }

        Log.d("feedcomment",feedid);
        init();
        getdata();
        Thread thread = new Thread() {
            @Override
            public void run() {

                    while (true) {
                        if (mycomment.getText().toString().equals("")) {
                            Log.d("uithreadwaala", "fade");
                            send.setTextColor(getResources().getColor(R.color.fadeblue));
                        } else {
                            send.setTextColor(getResources().getColor(R.color.blue));
                            Log.d("uithreadwaala", "bright");
                        }

                    }

            }
        };

        thread.start();
       /* runOnUiThread(new Runnable(){
            @Override
            public void run(){
                while(true) {

                }
            }
        });*/
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mycomment.getText().toString().equals(""))
                send();
            }
        });
    }

    public void send()
    {
        String ts= String.valueOf(System.currentTimeMillis());
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Feeds").child(feedid).child("comments").child(ts);
        reference.child("content").setValue(mycomment.getText().toString());
        reference.child("id").setValue(currentUser);
        reference.child("time").setValue(ts);
        mycomment.setText("");


    }
    public void getdata()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contacts.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    String id=dataSnapshot1.getKey();
                    contacts.add(id);

                }
                Log.d("feedcomment", String.valueOf(contacts));
                commentsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void init()
    {
        send=findViewById(R.id.sendcomment);
        mycomment=findViewById(R.id.mycomment);
        commenterpic=findViewById(R.id.commenter_pic);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        ref.child(currentUser).child("Personal").child("Photo").addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url  =dataSnapshot.getValue(String.class);
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(getApplicationContext());
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(10f) ;
                circularProgressDrawable.start();
                Glide.with(getApplicationContext())
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
                        .into(commenterpic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child(currentUser).child("Personal").child("Name").addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url  =dataSnapshot.getValue(String.class);
                mycomment.setHint("Comment as "+url);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference= FirebaseDatabase.getInstance().getReference().child("Feeds").child(feedid).child("comments");
       commentlist =findViewById(R.id.commentList);
        commentlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Log.d("choka", "onDataChange: " + contacts);


        commentsAdapter=new CommentsAdapter(contacts,this,feedid,currentUser);

        commentlist.setAdapter(commentsAdapter);

    }

}
