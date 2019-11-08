package com.example.litereria.ui.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.litereria.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class HomeFragment extends Fragment {
    ImageView qr;
    EditText insta,github,emailid,twitter,phonenumber,facebookid,hackerearthid,hackerrankid,codechefid,linkedinid,title;
    ImageView imageView;
    Uri photoURL=null; Uri profilePictureUri;
    String emailids; String uri=null;String name;
    Button generate,clear;Button upload;String id;
    private StorageReference mStorageRef;
    private FirebaseDatabase database ;
    private DatabaseReference myRef,databaseReference;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        insta=root.findViewById(R.id.insta);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            emailids = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
           id= Profile.getCurrentProfile().getId();
        }
        imageView=root.findViewById(R.id.profilePhoto1);
        String id= Profile.getCurrentProfile().getId();
         photoURL=null;String userid=null,phone=null,useridd=null,url=null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("ak47","ghusa");

            photoURL = user.getPhotoUrl();
            userid=user.getDisplayName();
            phone=user.getPhoneNumber();
            useridd=user.getUid();


            CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(getContext());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(100f) ;
            circularProgressDrawable.start();
            int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
             profilePictureUri= Profile.getCurrentProfile().getProfilePictureUri(500 , 500);
            url="http://graph.facebook.com/"+useridd+"/picture?type=square&type=large&redirect=false";
            Glide.with(getContext())
                    .load(profilePictureUri)
                    .placeholder(circularProgressDrawable)
                    .into(imageView);


        }





        else
            Log.d("ak47","ni  ghusa");




        title=root.findViewById(R.id.nametitle);
        title.setText(name);



        emailid=root.findViewById(R.id.emailtedittext);
        emailid.setText(emailids);
        twitter=root.findViewById(R.id.tweet);

        phonenumber=root.findViewById(R.id.Phonenumber);
        phonenumber.setText(phone);
        facebookid=root.findViewById(R.id.facebookid);

        hackerearthid=root.findViewById(R.id.hackerearthid);
        hackerrankid=root.findViewById(R.id.hackerrank);

        codechefid=root.findViewById(R.id.codechef);

        linkedinid=root.findViewById(R.id.linkedin);


        upload=root.findViewById(R.id.upload);

        github=root.findViewById(R.id.github);





       upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog alertDialog = new AlertDialog.Builder(getContext())
//set icon
                       .setIcon(R.drawable.connectlogo)
//set title
                       .setTitle("Update HashContact Profile")
//set message
                       .setMessage("Existing profile information will be replaced by data on screen.Are you sure you want to continue?")
//set positive button
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               //set what would happen when positive button is clicked
                               upload();
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





               //mStorageRef= FirebaseStorage.getInstance().getReference().child(key);

              // Toast.makeText(getContext(),"Profile Updated",Toast.LENGTH_LONG).show();

           }
       });
        return root;
    }
    public void upload()
    {

        String emailidss= FirebaseAuth.getInstance().getCurrentUser().getEmail();

        final Map<String,Object> bookdata=new HashMap<>();
        bookdata.put("Name",name);
        bookdata.put("Email",emailid.getText().toString());
        bookdata.put("Instagram",insta.getText().toString());
        bookdata.put("github",github.getText().toString());
        bookdata.put("Twitter",twitter.getText().toString());
        bookdata.put("PhoneNumber",phonenumber.getText().toString());
        bookdata.put("Facebook",facebookid.getText().toString());
        bookdata.put("HackerEarth",hackerearthid.getText().toString());
        bookdata.put("HackerRank",hackerrankid.getText().toString());
        bookdata.put("Codechef",codechefid.getText().toString());
        bookdata.put("Linkedin",linkedinid.getText().toString());
        bookdata.put("PhotoUrl",profilePictureUri.toString());

        String key=String.valueOf(System.currentTimeMillis());

        database = FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        databaseReference.child(id).child("Personal").setValue(bookdata);

        Toast.makeText(getContext(),"Profile Updated Successfully",Toast.LENGTH_LONG).show();


    }



}