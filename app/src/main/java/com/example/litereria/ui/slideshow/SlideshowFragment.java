package com.example.litereria.ui.slideshow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.litereria.MainActivity;
import com.example.litereria.R;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class SlideshowFragment extends Fragment {
    ImageView imageView; private FirebaseDatabase database ;
    private DatabaseReference myRef;String instagr;
   Map<String ,String> mp=new HashMap<>();
   DatabaseReference databaseReference;
    ImageView prof;
    TextView textView;


    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        imageView=root.findViewById(R.id.qrcode);
        database=FirebaseDatabase.getInstance();
        prof=root.findViewById(R.id.prof);
        textView=root.findViewById(R.id.textView2);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Generating your HashContact QR");
        progressDialog.show();
        progressDialog.setCancelable(false);

        String pro= FirebaseAuth.getInstance().getCurrentUser().getDisplayName().trim();
        textView.setText(pro);
        Uri profilePictureUri= Profile.getCurrentProfile().getProfilePictureUri(200 , 200);
        CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(100f) ;
        circularProgressDrawable.start();
        Glide.with(getContext())
                .load(profilePictureUri)
                .placeholder(circularProgressDrawable)
                .into(prof);

        String emailid= FirebaseAuth.getInstance().getCurrentUser().getEmail().trim();
        String email=Profile.getCurrentProfile().getId();

      myRef= database.getReference(email);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                    mp=(Map<String, String>) dataSnapshot1.getValue();




                       final Bitmap bitmap;

                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        try {
                            String query="Instagram:"+ mp.get("Instagram") +"\n"+ "Github:"+mp.get("github")+"\n"+"E"+mp.get("Email")+"\n"+"Codechef"+mp.get("Codechef")+"\n"+"Facebook"+mp.get("Facebook")+"\n"+"HackerEarth"+mp.get("HackerEarth")+"\n"+"HackerRank"+mp.get("HackerRank")+"\n"+"Linkedin"+mp.get("Linkedin")+"\n"+"Phone"+mp.get("PhoneNumber")+"\n"+"PhotoUrl"+mp.get("PhotoUrl")+"\n"+"Twitter"+mp.get("Twitter")+"\n"+"Name"+mp.get("Name");


                            //Toast.makeText(getContext(),lines[1],Toast.LENGTH_LONG).show();

                            BitMatrix bitMatrix = multiFormatWriter.encode(query, BarcodeFormat.QR_CODE, 350, 350);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                             bitmap = barcodeEncoder.createBitmap(bitMatrix);
                            imageView.setImageBitmap(bitmap);
                            progressDialog.dismiss();

                        }
                        catch (WriterException e)
                        {
                            e.printStackTrace();
                        }


                    }

                       // mp=(Map<String, String>) dataSnapshot1.getValue();





                    dataSnapshot.getKey();
                }
                //Log.e("ak47", list.get(0).get("Title") + "onDataChange: " + list.size());
                //adapterBooks.notifyDataSetChanged();


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        String insta=mp.get("Instagram");
        String github=mp.get("github");




        return root;
    }
}