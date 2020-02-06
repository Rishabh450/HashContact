package com.example.litereria.ui.slideshow;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.litereria.MainActivity;
import com.example.litereria.R;
import com.example.litereria.Support.QRcode;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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
    Button send; String imgBitmapPath;
    TextView textView;
    Uri imgUri;
    String userno;String provider;


    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        imageView=root.findViewById(R.id.qrcode);
        database=FirebaseDatabase.getInstance();
        prof=root.findViewById(R.id.prof);
        textView=root.findViewById(R.id.textView2);
        send=root.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);


                }
                else {



                    Drawable dr = ((ImageView) imageView).getDrawable();
                    if(dr!=null) {
                        Bitmap imgBitmap = drawableToBitmap(dr);
                        imgBitmapPath = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imgBitmap, "title", null);
                        imgUri = Uri.parse(imgBitmapPath);

                       /* Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,feedPosts.get(position).getEvent()+"\n"+
                                feedPosts.get(position).getSubEvent()+"\n"+
                                feedPosts.get(position).getContent() );
                        sendIntent.setType("text/plain");
                        context.startActivity(Intent.createChooser(sendIntent,"Share this article via:"));*/

                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("*/*");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT,"Scan this QR to add contact");

                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);

                       // whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                            startActivity(Intent.createChooser(whatsappIntent, "Share image via:"));


                    }
                    else
                        Toast.makeText(getContext(),"Please wait while your QR is being generated",Toast.LENGTH_SHORT).show();
                }
            }
        });


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Generating your HashContact QR");
        progressDialog.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;
        progressDialog.show();
        progressDialog.setCancelable(true);
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            for (UserInfo use:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (use.getProviderId().equals("facebook.com")) {
                    provider="facebook";
                }
                else
                    provider="google";
            }

            FirebaseAuth.getInstance().getCurrentUser().getProviderId();
            UserInfo userr = FirebaseAuth.getInstance().getCurrentUser();
            if (provider.equals("facebook")) {
                userno = Profile.getCurrentProfile().getId();

            } else {
                userno = user.getUid();
            }
        }




            rootRef.child(userno).child("Personal").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textView.setText(dataSnapshot.getValue(String.class));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


            rootRef.child(userno).child("Personal").child("Photo").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(getContext());
                    circularProgressDrawable.setStrokeWidth(5f);
                    circularProgressDrawable.setCenterRadius(100f) ;
                    circularProgressDrawable.start();
                    Glide.with(getContext())
                            .load(dataSnapshot.getValue(String.class))
                            .placeholder(circularProgressDrawable)
                            .into(prof);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

       // String emailid= FirebaseAuth.getInstance().getCurrentUser().getEmail().trim();
        final Bitmap bitmap;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {



            BitMatrix bitMatrix = multiFormatWriter.encode(userno, BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
            progressDialog.dismiss();


        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }









        return root;
    }
    public  Bitmap drawableToBitmap (Drawable drawable) {
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
}