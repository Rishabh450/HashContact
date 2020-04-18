package com.rishabh.hashcontact;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.rishabh.hashcontact.Models.Post;
import com.rishabh.hashcontact.Support.Chats;
import com.rishabh.hashcontact.Support.Feed;
import com.rishabh.hashcontact.Support.LiveLocationService;
import com.rishabh.hashcontact.Support.Login;
import com.rishabh.hashcontact.Support.QRcode;
import com.rishabh.hashcontact.ui.gallery.GalleryFragment;
import com.rishabh.hashcontact.ui.home.HomeFragment;
import com.rishabh.hashcontact.ui.slideshow.SlideshowFragment;
import com.facebook.Profile;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth.AuthStateListener authStateListener;String userno;
    FirebaseAuth mAuth;String email,name;private ProgressDialog mProgress;
    Uri photoUrl;TextView name1,email1;NavigationView navigationView;
    private Context context=MainActivity.this; String currentUser1;
    ImageView profilePhoto;DrawerLayout drawer;Uri profilePictureUri;
    BottomNavigationView bott;TextView userid;
    GoogleSignInClient mGoogleSignInClient;String provider;
    AppBarLayout root;




    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.e("ak47","on Start");
        super.onStart();
        Log.e("ak47","on Start after super");
      mAuth.addAuthStateListener(authStateListener);
        Log.e("ak47","on Start Ends");
    }


    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

       // SHA1: FD:BA:5A:0A:0C:9F:66:3A:8C:D0:87:AF:26:D2:77:E9:9B:6C:E1:F5
// GOOGLE PLAY APP SIGNING SHA-1 KEY:- 65:5D:66:A1:C9:31:85:AB:92:C6:A2:60:87:5B:1A:DA:45:6E:97:EA
        byte[] sha1 = {
                (byte) 0xFD, (byte) 0xBA ,0x5A,0x0A, 0x0C, (byte)0x9F, (byte)0x66, 0x3A, (byte) 0x8C, (byte)0xD0, (byte)0x87, (byte)0xAF, (byte)0x26, (byte) 0xD2, (byte) 0x77, (byte) 0xE9, (byte) 0x9B, (byte)0x6C, (byte) 0xE1, (byte) 0xF5
        };
        Log.d( "shar","keyhashGooglePlaySignIn:"+ Base64.encodeToString(sha1, Base64.NO_WRAP));
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.e("ak47","user null");

       authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Log.e("ak47","user null");
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
                else
                {


                    Log.e("ak47","user not null");
                }

            }
        };
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent =new Intent();
            //name=intent.getStringExtra("avatar");
            ConnectivityBroadcastReceiver connectivityBroadcastReceiver= new ConnectivityBroadcastReceiver();
            connectivityBroadcastReceiver.onReceive(this,new Intent(this,ConnectivityBroadcastReceiver.class));
            Log.d("checknet", String.valueOf(isNetworkAvailable()));
           // photoUrl= Uri.parse(intent.getStringExtra("url"));
            // Name, email address, and profile photo Url
            root=findViewById(R.id.root);
             email = user.getEmail();
            for (UserInfo use:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (use.getProviderId().equals("facebook.com")) {
                    provider="facebook";
                }
                else
                    provider="google";
            }
            FirebaseAuth.getInstance().getCurrentUser().getProviderId();
            UserInfo userr= FirebaseAuth.getInstance().getCurrentUser();
                if (provider.equals("facebook")) {
                    userno=Profile.getCurrentProfile().getId();

                }
                else{
                    userno=user.getUid();
                }
            LiveLocationService mYourService = new LiveLocationService();
          // Intent userService = new Intent(MainActivity.this, userLocationService.getClass());
           Intent mServiceIntent = new Intent(MainActivity.this, mYourService.getClass());

            if (!isMyServiceRunning(mYourService.getClass())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    startForegroundService(mServiceIntent);


                } else {

                    startService(mServiceIntent);

                }
            }
            Toolbar toolbar = findViewById(R.id.toolbar);

            setSupportActionBar(toolbar);
            bott=findViewById(R.id.navigationView);
            bott.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);


            toolbar.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference isOnlineRef = rootRef.child(userno).child("isOnline");
            isOnlineRef.setValue("true");
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());


            isOnlineRef.onDisconnect().setValue("Last seen "+currentDate+"\n"+currentTime);


            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
            navigationView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
            headerView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);

            name1=headerView.findViewById(R.id.name);
            rootRef.child(userno).child("Personal").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name1.setText(dataSnapshot.getValue(String.class));


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            email1=headerView.findViewById(R.id.email);
            email1.setText(email);
            userid=headerView.findViewById(R.id.userid);
            userid.setText(userno);

           profilePhoto=headerView.findViewById(R.id.profilePhoto);
            int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
            rootRef.child(userno).child("Personal").child("Photo").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //name1.setText(dataSnapshot.getValue(String.class));
                    CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(MainActivity.this);
                    circularProgressDrawable.setStrokeWidth(5f);
                    circularProgressDrawable.setCenterRadius(100f) ;
                    circularProgressDrawable.start();
                    Glide.with(context)
                            .load(dataSnapshot.getValue(String.class))
                            .placeholder(R.mipmap.placeholder)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePhoto);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
            drawer.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            HomeFragment booksAvailable=new HomeFragment();
            fragmentTransaction.add(R.id.fragment_container,booksAvailable);
            fragmentTransaction.commit();
            bott.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    FragmentManager fragmentManager=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();

                    switch (menuItem.getItemId()) {
                        case R.id.chats:

                            root.setBackgroundResource(R.mipmap.background);
                            //do somthing
                            GalleryFragment booksAvailable=new GalleryFragment();
                            fragmentTransaction.replace(R.id.fragment_container,booksAvailable);
                            fragmentTransaction.commit();
                            break;
                        case R.id.status:
                           startActivity(new Intent(MainActivity.this, Feed.class));
                           break;
                        case R.id.post:
                           startActivity(new Intent(MainActivity.this, Post.class));
                            break;

                    }
                    return true ;
                }
            });
        }


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            // Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            // Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);


        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            // Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},0);


        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            // Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET},0);


        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.VIBRATE)!= PackageManager.PERMISSION_GRANTED)
        {
            // Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.VIBRATE},0);


        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {
            // Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},0);


        }


    }



    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {

            case R.id.qractivity:
                Intent intent=new Intent(MainActivity.this, QRcode.class);
                startActivity(intent);



        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {
        OneSignal.setSubscription(false);
        mAuth.signOut();
        //mGoogleSignInClient.signOut();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_gallery:
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                root.setBackgroundResource(R.mipmap.background);
                //do somthing
                GalleryFragment booksAvailable=new GalleryFragment();
                fragmentTransaction.replace(R.id.fragment_container,booksAvailable);
                fragmentTransaction.commit();

                break;
                case R.id.nav_home:
                    root=findViewById(R.id.root);
                    root.setBackgroundResource(R.mipmap.profile_background);
                    FragmentManager fragmentManager1=getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction1=fragmentManager1.beginTransaction();

                    //do somthing
                    HomeFragment books=new HomeFragment();
                    fragmentTransaction1.replace(R.id.fragment_container,books);
                    fragmentTransaction1.commit();
                    break;
            case R.id.nav_qr:
                FragmentManager fragmentManager2=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction2=fragmentManager2.beginTransaction();
                root.setBackgroundResource(R.mipmap.background);

                //do somthing
                SlideshowFragment slideshowFragment=new SlideshowFragment();
                fragmentTransaction2.replace(R.id.fragment_container,slideshowFragment);
                fragmentTransaction2.commit();


                    break;
            case R.id.signout:
                signOut();

        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
        private boolean isMyServiceRunning(Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.i("Service status", "Running");
                    return true;
                }
            }
            Log.i("Service status", "Not running");
            return false;
        }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
