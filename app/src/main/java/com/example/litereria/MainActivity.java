package com.example.litereria;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.litereria.Models.Post;
import com.example.litereria.Support.ChatBox;
import com.example.litereria.Support.Chats;
import com.example.litereria.Support.Feed;
import com.example.litereria.Support.Login;
import com.example.litereria.Support.QRcode;
import com.example.litereria.Support.Status;
import com.example.litereria.ui.gallery.GalleryFragment;
import com.example.litereria.ui.home.HomeFragment;
import com.example.litereria.ui.slideshow.SlideshowFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import com.onesignal.shortcutbadger.ShortcutBadger;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                            .placeholder(circularProgressDrawable)
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
                            Chats booksAvailable = new Chats();
                            fragmentTransaction.replace(R.id.fragment_container, booksAvailable);
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

                //do somthing
                GalleryFragment booksAvailable=new GalleryFragment();
                fragmentTransaction.replace(R.id.fragment_container,booksAvailable);
                fragmentTransaction.commit();

                break;
                case R.id.nav_home:
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
