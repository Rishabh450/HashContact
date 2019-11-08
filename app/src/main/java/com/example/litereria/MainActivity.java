package com.example.litereria;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.litereria.Support.Login;
import com.example.litereria.Support.QRcode;
import com.example.litereria.ui.gallery.GalleryFragment;
import com.example.litereria.ui.home.HomeFragment;
import com.example.litereria.ui.slideshow.SlideshowFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth mAuth;String email,name;
    Uri photoUrl;TextView name1,email1;NavigationView navigationView;
    private Context context=MainActivity.this;
    ImageView profilePhoto;DrawerLayout drawer;
    GoogleSignInClient mGoogleSignInClient;
    /*class MyAsyncTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream inputStream = null;String name;
        String result = "";String url_select;

        @Override
        protected Void doInBackground(String... strings) {
            url_select = "https://graph.facebook.com/1342147085963751/picture?type=square&type=large&redirect=false";

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

            try {
                // Set up HTTP post

                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(url_select);
                httpPost.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedEn", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
                Log.e("StringBuilding", "Error converting result " + e.toString());
            }
            return null;
        }
        protected void onPreExecute() {
            AlertDialog.Builder progressDialog = null;
            progressDialog.setMessage("Downloading your data...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    MyAsyncTask.this.cancel(true);
                }
            });
        }
        protected void onPostExecute(Void v) {
            //parse JSON data
            try {
                JSONArray jArray = new JSONArray(result);
                int i;
                for(i=0; i < jArray.length(); i++) {

                    JSONObject jObject = jArray.getJSONObject(i);

                    name = jObject.getString("url");
                    Toast.makeText(MainActivity.this,name,Toast.LENGTH_LONG).show();
                    String tab1_text = jObject.getString("tab1_text");
                    int active = jObject.getInt("active");

                } // End Loop
                this.progressDialog.dismiss();
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
        } // protected void onPostExecute(Void v)
        }*/


    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Build a GoogleSignInClient with the options specified by gso.

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
        //startActivity(new Intent(this,Login.class));
        //Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        //vibrator.vibrate(1000);

//        mAuth.addAuthStateListener(authStateListener);

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
            // Name, email address, and profile photo Url
            name = user.getDisplayName();
             email = user.getEmail();
             photoUrl = user.getPhotoUrl();
            Toast.makeText(this,name+" is Logged in",Toast.LENGTH_LONG).show();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);

         setSupportActionBar(toolbar);

         drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        /*View headerLayout = navigationView.getHeaderView(0);
        headerLayout =
                navigationView.inflateHeaderView(R.layout.nav_header_main);
        View panel = headerLayout.findViewById(R.id.nav_head);*/
         name1=headerView.findViewById(R.id.name);
        name1.setText(name);
        email1=headerView.findViewById(R.id.email);
        email1.setText(email);
        profilePhoto=headerView.findViewById(R.id.profilePhoto);
        int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
        Uri profilePictureUri= Profile.getCurrentProfile().getProfilePictureUri(200 , 200);
        CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(MainActivity.this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(100f) ;
        circularProgressDrawable.start();
      Glide.with(context)
                .load(profilePictureUri)
              .placeholder(circularProgressDrawable)
                .into(profilePhoto);

        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        HomeFragment booksAvailable=new HomeFragment();
        fragmentTransaction.add(R.id.fragment_container,booksAvailable);
        fragmentTransaction.commit();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


// panel won't be null
       /* NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/
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
    //Next lines are Strings used as params
    public static String FACEBOOK_FIELD_PROFILE_IMAGE = "picture.type(large)";
    public static String FACEBOOK_FIELDS = "fields";

    //A function that can be accessed from OnCreate (Or a similar function)
    private void setImageProfileFacebook(){

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn) {
            //If the user is LoggedIn then continue
            Bundle parameters = new Bundle();
            parameters.putString(FACEBOOK_FIELDS, FACEBOOK_FIELD_PROFILE_IMAGE);
            /* make the API call */
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "me",
                    parameters,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            /* handle the result */
                            if (response != null) {
                                try {
                                    JSONObject data = response.getJSONObject();
                                    //Log.w(TAG, "Data: " + response.toString());

                                    if (data.has("picture")) {
                                        boolean is_silhouette = data.getJSONObject("picture").getJSONObject("data").getBoolean("is_silhouette");
                                        if (!is_silhouette) {
                                            //Silhouette is used when the FB user has no upload any profile image
                                            URL profilePicUrl = new URL(data.getJSONObject("picture").getJSONObject("data").getString("url"));
                                            InputStream in = (InputStream) profilePicUrl.getContent();
                                            Bitmap bitmap = BitmapFactory.decodeStream(in);
                                            profilePhoto.setImageBitmap(bitmap);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.w("TAG", "Response null");
                            }
                        }
                    }
            ).executeAsync();
        }
    }
    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
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
            case R.id.signout:signOut();

        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
