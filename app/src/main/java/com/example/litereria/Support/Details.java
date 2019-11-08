package com.example.litereria.Support;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.litereria.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Details extends AppCompatActivity {
    ImageView qr;
    EditText insta,github,emailid,twitter,phonenumber,facebookid,hackerearthid,hackerrankid,codechefid,linkedinid,title,nameer;
    ImageView imageView;
    Uri photoURL=null; Uri profilePictureUri;
    String emailids; String uri=null;String name;
    Button generate,clear;Button upload;
    private StorageReference mStorageRef;
    HashMap<String,String > data;
    private FirebaseDatabase database ;
    private DatabaseReference myRef,databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_details);
        Intent intent=getIntent();
        insta=findViewById(R.id.instaDetails);
        facebookid=findViewById(R.id.facebookidDetails);
        codechefid=findViewById(R.id.codechefDetails);
        hackerearthid=findViewById(R.id.hackerearthidDetails);
        emailid=findViewById(R.id.emailtedittextDetails);
        linkedinid=findViewById(R.id.linkedinDetails);
        hackerrankid=findViewById(R.id.hackerrankDetails);
        nameer=findViewById(R.id.nametitle);
        phonenumber=findViewById(R.id.PhonenumberDetails);
        github=findViewById(R.id.githubDetails);
        twitter=findViewById(R.id.twitter);
        if(intent.hasExtra("Data"))
        {
            data=(HashMap<String,String >)intent.getSerializableExtra("Data");
           // Toast.makeText(Details.this,data.get("Instagram"),Toast.LENGTH_LONG).show();
            String owner=data.get("Instagram");
            insta.setText(owner);
            codechefid.setText(data.get("Codechef"));
            emailid.setText(data.get("Email"));
            facebookid.setText(data.get("Facebook"));
            imageView=findViewById(R.id.profilePhotoDetails);
            nameer=findViewById(R.id.nametitleDetails);
            hackerearthid.setText(data.get("HackerEarth"));
            hackerrankid.setText(data.get("HackerRank"));
            linkedinid.setText(data.get("Linkedin"));
            twitter=findViewById(R.id.tweet);
            github=findViewById(R.id.githubDetails);
            nameer.setText(data.get("Name"));
            phonenumber.setText(data.get("PhoneNumber"));
            twitter.setText(data.get("Twitter"));
            github.setText(data.get("github"));

            uri=data.get("photo");
            CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(this );
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f) ;
            circularProgressDrawable.start();

            Glide.with(this)
                    .load(uri)
                    .placeholder(circularProgressDrawable).into(imageView);







        }


    }
}
