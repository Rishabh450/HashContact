package com.example.litereria.Support;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.litereria.MainActivity;
import com.example.litereria.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Details extends AppCompatActivity {
    ImageView qr;
    EditText insta,github,emailid,twitter,phonenumber,facebookid,hackerearthid,hackerrankid,codechefid,linkedinid,title,nameer;
    ImageView imageView;
    Uri photoURL=null; Uri profilePictureUri;
    String emailids; String uri=null;String name;
    Button generate,clear;Button update;String photo;
    private StorageReference mStorageRef;String id;
    HashMap<String,String > data;
    private FirebaseDatabase database ;String key;
    private DatabaseReference myRef,databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        insta=findViewById(R.id.instaDetails);
        update=findViewById(R.id.updatecontact);
        facebookid=findViewById(R.id.facebookidDetails);
        codechefid=findViewById(R.id.codechefDetails);
        hackerearthid=findViewById(R.id.hackerearthidDetails);
        emailid=findViewById(R.id.emailtedittextDetails);
        linkedinid=findViewById(R.id.linkedinDetails);
        hackerrankid=findViewById(R.id.hackerrankDetails);
        nameer=findViewById(R.id.nametitle);
        id= Profile.getCurrentProfile().getId();
        phonenumber=findViewById(R.id.PhonenumberDetails);
        github=findViewById(R.id.githubDetails);
        twitter=findViewById(R.id.twitter);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Details.this)
//set icon
                        .setIcon(R.drawable.connectlogo)
//set title
                        .setTitle("Update Contact")
//set message
                        .setMessage("Existing information will be replaced.Are you sure you want to continue?")
//set positive button
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what would happen when positive button is clicked
                                update();
                            }
                        })
//set negative button
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what should happen when negative button is clicked

                            }
                        })
                        .show();

            }
        });
        if(intent.hasExtra("Data"))
        {
            data=(HashMap<String,String >)intent.getSerializableExtra("Data");
           // Toast.makeText(Details.this,data.get("Instagram"),Toast.LENGTH_LONG).show();
            String owner=data.get("Instagram");
            insta.setText(owner);
            codechefid.setText(data.get("Codechef"));
            key=data.get("Key");
            emailid.setText(data.get("Email"));
            facebookid.setText(data.get("Facebook"));
            imageView=findViewById(R.id.profilePhotoDetails);
            nameer=findViewById(R.id.nametitleDetails);
            hackerearthid.setText(data.get("HackerEarth"));
            hackerrankid.setText(data.get("HackerRank"));
            linkedinid.setText(data.get("Linkedin"));
            twitter=findViewById(R.id.tweet);
            photo=data.get("photo");
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
    public void update()
    {
        final Map<String,Object> bookdata=new HashMap<>();
        bookdata.put("Email",emailid.getText().toString());

        bookdata.put("Instagram",insta.getText().toString());
        bookdata.put("github",github.getText().toString());
        bookdata.put("Twitter",twitter.getText().toString());
        bookdata.put("PhoneNumber",phonenumber.getText().toString());
        bookdata.put("Facebook",facebookid.getText().toString());
        bookdata.put("photo",photo);
        bookdata.put("HackerEarth",hackerearthid.getText().toString());
        bookdata.put("HackerRank",hackerrankid.getText().toString());
        bookdata.put("Codechef",codechefid.getText().toString());
        bookdata.put("Linkedin",linkedinid.getText().toString());
        //  String key=String.valueOf(System.currentTimeMillis());
        bookdata.put("key",key);
        bookdata.put("Name",nameer.getText().toString());
        database = FirebaseDatabase.getInstance();

        databaseReference=database.getReference();
        String  emailids= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String id= Profile.getCurrentProfile().getId();

        databaseReference.child(Profile.getCurrentProfile().getId()).child("Contact").child(key).setValue(bookdata);

        Toast.makeText(this,"Contact uploaded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {

            case R.id.remove:
                AlertDialog alertDialog = new AlertDialog.Builder(this)
//set icon
                        .setIcon(R.drawable.remove)
//set title
                        .setTitle("Remove Contact")
//set message
                        .setMessage("This contact will be deleted permanently.Are you sure you want to continue?")
//set positive button
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what would happen when positive button is clicked
                                FirebaseDatabase.getInstance().getReference().child(id).child("Contact").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(Details.this,"Contact deleted successfully",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });

                            }
                        })
//set negative button
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what should happen when negative button is clicked

                            }
                        })
                        .show();




        }

        return super.onOptionsItemSelected(item);
    }
}
