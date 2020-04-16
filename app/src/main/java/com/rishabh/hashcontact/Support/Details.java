package com.rishabh.hashcontact.Support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rishabh.hashcontact.R;
import com.rishabh.hashcontact.RecyclerViewDetails;
import com.rishabh.hashcontact.TouchImageView;
import com.facebook.Profile;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Details extends AppCompatActivity {
    ImageView imageView;
Button contact;
    LinearLayout linearLayout;
    String uri=null;Drawable img;


    RecyclerViewDetails recyclerViewDetails;
    ArrayList<Map<String, String>> contacts = new ArrayList<Map<String, String>>();
    HashMap<String,String > data;
   String key;String provider;
   String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        imageView=findViewById(R.id.profilePhotoDetails);
        linearLayout=findViewById(R.id.zoom);
        contact=findViewById(R.id.contact);
        contact.getBackground().setAlpha(50);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
              ref.child(key).child("Contact").addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if(dataSnapshot.hasChild(currentUser))
                      {
                          Intent intent =new Intent(Details.this,ChatBox.class);
                          intent.putExtra("id",key);
                          startActivity(intent);

                      }
                      else
                      {
                          Toast.makeText(Details.this,"This user has not added you in their contact, So you can't chat.",Toast.LENGTH_LONG).show();
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });


            }
        });
        for (UserInfo user:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                provider="facebook";
                currentUser=Profile.getCurrentProfile().getId();
            }
            else {
                provider = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser=userInfo.getUid();
            }
        }




        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();


        RecyclerView recyclerView = findViewById(R.id.detaillist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("choka", "onDataChange: " + contacts);
        getdata();
        recyclerViewDetails = new RecyclerViewDetails(contacts,this);
        Log.d("dad", String.valueOf(contacts));

        recyclerView.setAdapter(recyclerViewDetails);

        if(intent.hasExtra("Data"))
        {
            data=(HashMap<String,String >)intent.getSerializableExtra("Data");
            //Intent intent=getIntent();
             key= data.get("key");
            Log.d("makapyaar", key);

            uri=data.get("Photo");
            CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(this );
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f) ;
            circularProgressDrawable.start();

            Glide.with(this)
                    .load(uri)
                    .placeholder(circularProgressDrawable).into(imageView);
            Glide.with(this)
                    .load(uri)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            img=resource;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });







        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Details.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageview1);
                photoView.setImageDrawable(img);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        TouchImageView iv = new TouchImageView(getApplicationContext());
        iv.setImageBitmap(drawableToBitmap(imageView.getDrawable()) );

        linearLayout.addView(iv);


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
                               FirebaseDatabase.getInstance().getReference().child(currentUser).child("Contact").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(Details.this,"Contact deleted successfully",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });

                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();




        }

        return super.onOptionsItemSelected(item);
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    private void getdata() {
        Intent intent=getIntent();

        if(intent.hasExtra("Data")) {
            data = (HashMap<String, String>) intent.getSerializableExtra("Data");
        }

                contacts.clear();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if(entry.getValue().equals("")||entry.getKey().equals("key")||entry.getKey().equals("Photo")||entry.getKey().equals("PhotoUrl")) {

            }
            else {
                Map<String, String> mp1 = new HashMap<>();
                mp1.put("detailname", entry.getKey());
                mp1.put("detail", entry.getValue());
                if(!mp1.get("detailname").equals("Notification")){
                contacts.add(mp1);
                Log.d("theos", String.valueOf(mp1));}
            }


        }
        Log.d("theo", String.valueOf(contacts));

    }

}
