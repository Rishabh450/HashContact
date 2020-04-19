package com.rishabh.hashcontact.Support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.ChildEventListener;
import com.rishabh.hashcontact.FeedAdapter;
import com.rishabh.hashcontact.Models.Comments;
import com.rishabh.hashcontact.Models.FeedPost;
import com.rishabh.hashcontact.Models.Likes;
import com.rishabh.hashcontact.Models.Seen;
import com.rishabh.hashcontact.Models.Status;
import com.rishabh.hashcontact.R;
import com.facebook.Profile;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavigationDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView mPullUp;
    private AlertDialog.Builder builder;
    private TranslateAnimation mAnimation;
    private ProgressBar recyclerview_progress;
    RecyclerView status;
    private SwipeRefreshLayout refreshLayout;

    //Poster shit
    private static final int BANNER_DELAY_TIME = 5 * 1000;
    private static final int BANNER_TRANSITION_DELAY = 1200;
    private Runnable runnable;
    private Handler handler;
    private boolean firstScroll = true;

    private RecyclerView mRecyclerView;
    private FeedAdapter mFeedAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final int ACCESSIBILITY_ENABLED = 1;

    private DatabaseReference dref;
    private FirebaseAuth mauth;
    private ArrayList<FeedPost> listposts;
    private ArrayList<Status> statusArrayList = new ArrayList<>();
    StatusAdapter statusAdapter;

    private String currentuid;
    ImageView statusmypic;

    private FrameLayout homeContainer;
    private ViewPager viewPager;
    ValueEventListener valueEventListener;
    String providerr, currentUser;
    ValueEventListener listener;
    int flag = 1;

    private NestedScrollView scrollView;
    private boolean mScrollDown = false;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        getWindow().setStatusBarColor(R.color.darkgray);
        Log.d("taskremoved",isAccessibilitySettingsOn(this)+"thisone");

        // recyclerview_progress = findViewById(R.id.recycler_view_progress);
        mRecyclerView = findViewById(R.id.feed_recycler_view);
        status = findViewById(R.id.statusRecycler);
        statusmypic = findViewById(R.id.statusmypic);

        refreshLayout=findViewById(R.id.swipe_refresh);
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                providerr = "facebook";
                currentUser = Profile.getCurrentProfile().getId();
            } else {
                providerr = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser = userInfo.getUid();
            }
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(currentUser).child("Personal").child("Photo");
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(Feed.this);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(10f);
                circularProgressDrawable.start();
                Glide.with(Feed.this)
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
                        .into(statusmypic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        statusmypic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Feed.this, UploadStatus.class));


            }
        });

        listposts = new ArrayList<>();
/*
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PostNo", Integer.toString(0));*/
        /*editor.apply();*/
        fetchFeedsDataFromFirebase();
        getData();
        refresh();


    }
    void refresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                fetchFeedsDataFromFirebase();
//                if (mFeedAdapter != null)
//                    mFeedAdapter.notifyDataSetChanged();
                if (mFeedAdapter != null)
                    mFeedAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }
    public void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Status");
        reference.keepSynced(false);
        reference.addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statusArrayList.clear();
                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                    final boolean[] flagseen = {false};
                    final String statusid = ds.getKey();

                    //String attr=child.getKey();
                    final String userid = ds.child("userid").getValue(String.class);
                    readData(new MyCallback(){

                        @Override
                        public void onCallback(boolean value) {
                            if(value) {
                                String text = ds.child("text").getValue(String.class);
                                String url = ds.child("url").getValue(String.class);
                                Log.d("userstatusid", userid + " " + statusid);

                                ArrayList<Seen> seen = new ArrayList<>();
                                for (DataSnapshot dsseen : ds.child("seen").getChildren()) {
                                    //Likes like=dslike.getValue(Likes.class);
                                    String temp = dsseen.getKey();
                                    Seen sea = new Seen(temp, dsseen.child("time").getValue(String.class));
                                    Log.d("userstatusi", sea.getUserid() + " " + sea.getTime());
                                    seen.add(sea);
                                    if (temp.equals(currentUser)) {
                                        flagseen[0] = true;
                                    }

                                }
                                Status status = new Status(statusid, userid, flagseen[0], seen, text, url);
                                statusArrayList.add(status);
                                setupStatus();
                                statusAdapter.notifyDataSetChanged();
                            }
                        }
                    },userid);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void readData(final MyCallback myCallback,String userID) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child(userID).child("Contact").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                myCallback.onCallback(dataSnapshot.hasChild(currentUser));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void fetchFeedsDataFromFirebase() {

        dref = FirebaseDatabase.getInstance().getReference().child("Feeds");
       /* dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot ds, @Nullable String s) {

                    final String post_id_temp = ds.getKey();
                    String is = "";
                    if (true) {
                        final String sender_id = ds.child("senderID").getValue().toString();
                        readData(new MyCallback() {
                            @Override
                            public void onCallback(boolean value) {
                                if (value) {
                                    boolean flag = false;

                                    ArrayList<Likes> mlikes = new ArrayList<>();
                                    for (DataSnapshot dslike : ds.child("likes").getChildren()) {

                                        String temp = dslike.getValue().toString();
                                        Likes like = new Likes(temp);
                                        mlikes.add(like);
                                        if (temp.equals(currentUser)) {
                                            flag = true;
                                        }
                                    }

                                    ArrayList<Comments> mcomments = new ArrayList<>();

                                    String content = ds.child("content").getValue().toString();
                                    String event_name = ds.child("event").getValue().toString();
                                    String subevent_name = ds.child("subEvent").getValue().toString();
                                    String image_url = ds.child("imageURL").getValue().toString();
                                    String sender_url = ds.child("senderURL").getValue(String.class);

                                    FeedPost post = new FeedPost(flag, post_id_temp, content, event_name, image_url, subevent_name, mlikes, mcomments, sender_url, sender_id);

                                    Log.e("vila", post.getImageURL());
                                    listposts.add(post);
                                    if (flag == true)
                                        setUpfirstRecyclerView();
                                    else
                                        setUpRecyclerView();
                                    Log.e("VIVZ", "onDataChange: listposts count = " + listposts.size());
                                    mFeedAdapter.notifyItemInserted(0);
                                }

                            }

                        },sender_id);

                    }



            }

            @Override
            public void onChildChanged(@NonNull final DataSnapshot ds, @Nullable final String s) {

                final String post_id_temp = ds.getKey();
                String is = "";
                if (true) {
                    final String sender_id = ds.child("senderID").getValue().toString();
                    readData(new MyCallback() {
                        @Override
                        public void onCallback(boolean value) {
                            if (value) {
                                boolean flag = false;

                                ArrayList<Likes> mlikes = new ArrayList<>();
                                for (DataSnapshot dslike : ds.child("likes").getChildren()) {

                                    String temp = dslike.getValue().toString();
                                    Likes like = new Likes(temp);
                                    mlikes.add(like);
                                    if (temp.equals(currentUser)) {
                                        flag = true;
                                    }
                                }

                                ArrayList<Comments> mcomments = new ArrayList<>();

                                String content = ds.child("content").getValue().toString();
                                String event_name = ds.child("event").getValue().toString();
                                String subevent_name = ds.child("subEvent").getValue().toString();
                                String image_url = ds.child("imageURL").getValue().toString();
                                String sender_url = ds.child("senderURL").getValue(String.class);

                                FeedPost post = new FeedPost(flag, post_id_temp, content, event_name, image_url, subevent_name, mlikes, mcomments, sender_url, sender_id);

                                Log.e("vila", post.getImageURL());
                                listposts.remove(mFeedAdapter.apos);
                                listposts.add(mFeedAdapter.apos,post);
                                if (flag == true)
                                    setUpfirstRecyclerView();
                                else
                                    setUpRecyclerView();
                                Log.e("VIVZ", "onDataChange: listposts count = " + listposts.size());
                                Log.d("prevposs", String.valueOf(mFeedAdapter.apos));
                                mFeedAdapter.notifyDataSetChanged();
                               // mFeedAdapter.notifyItemInserted(mFeedAdapter.getItemCount()-1- mFeedAdapter.apos);
                            }

                        }

                    },sender_id);

                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                mFeedAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
        dref.keepSynced(true);
        dref.addListenerForSingleValueEvent(valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // recyclerview_progress.setVisibility(View.VISIBLE);
                listposts.clear();
                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                    //FeedPost post = ds.getValue(FeedPost.class);
                    final String post_id_temp = ds.getKey();
                    String is = "";
                    if (true) {
                        final String sender_id = ds.child("senderID").getValue().toString();
                        readData(new MyCallback() {
                            @Override
                            public void onCallback(boolean value) {
                                if (value) {
                                    boolean flag = false;

                                    ArrayList<Likes> mlikes = new ArrayList<>();
                                    for (DataSnapshot dslike : ds.child("likes").getChildren()) {

                                        String temp = dslike.getValue().toString();
                                        Likes like = new Likes(temp);
                                        mlikes.add(like);
                                        if (temp.equals(currentUser)) {
                                            flag = true;
                                        }
                                    }

                                    ArrayList<Comments> mcomments = new ArrayList<>();

                                    String content = ds.child("content").getValue().toString();
                                    String event_name = ds.child("event").getValue().toString();
                                    String subevent_name = ds.child("subEvent").getValue().toString();
                                    String image_url = ds.child("imageURL").getValue().toString();
                                    String sender_url = ds.child("senderURL").getValue(String.class);

                                    FeedPost post = new FeedPost(flag, post_id_temp, content, event_name, image_url, subevent_name, mlikes, mcomments, sender_url, sender_id);

                                    Log.e("vila", post.getImageURL());
                                    listposts.add(post);
                                    if (flag == true)
                                        setUpfirstRecyclerView();
                                    else
                                        setUpRecyclerView();
                                    Log.e("VIVZ", "onDataChange: listposts count = " + listposts.size());

                                }

                            }

                        },sender_id);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void setupStatus() {
        status.setHasFixedSize(true);

        status.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        statusAdapter = new StatusAdapter(this, statusArrayList, currentUser);
        status.scrollToPosition(statusAdapter.getItemCount() - 1);

        status.setAdapter(statusAdapter);

    }

    private void setUpfirstRecyclerView() {

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mFeedAdapter = new FeedAdapter(this, getSupportFragmentManager(), listposts, currentUser);
        mRecyclerView.setAdapter(mFeedAdapter);
        //recyclerview_progress.setVisibility(View.GONE);

/*
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
*/

      /*  String numpost = preferences.getString("PostNo", "");
        mRecyclerView.scrollToPosition(mFeedAdapter.getItemCount() - 1);*/
        flag = 0;
    }

    /*CallBackk callback;

    void onEvent(String userr) {
        callback.onComplete(userr);
    }*/
    private void setUpRecyclerView() {

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        mFeedAdapter = new FeedAdapter(this, getSupportFragmentManager(), listposts, currentUser);
        mRecyclerView.setAdapter(mFeedAdapter);
        //recyclerview_progress.setVisibility(View.GONE);

       /* SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String numpost = preferences.getString("PostNo", "");*/
        /*mRecyclerView.scrollToPosition(Integer.parseInt(numpost));*/
    }
    public static boolean isAccessibilitySettingsOn(Context context) {
        Log.d("contname",context.getPackageName());
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + LiveLocationService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("AU", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == ACCESSIBILITY_ENABLED) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}



