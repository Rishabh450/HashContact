package com.example.litereria.ui.home;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.litereria.R;
import com.example.litereria.RecyclerViewDetails;
import com.example.litereria.RecyclerViewFiled;
import com.example.litereria.Support.Details;
import com.example.litereria.Support.ProfilePhoto;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class HomeFragment extends Fragment {
    ImageView qr;ImageView imageView1;TextView textView;
    RecyclerViewDetails recyclerViewDetails;
    RecyclerViewFiled recyclerViewFiled;
    LinearLayout profile;boolean flag1=true;
    RecyclerView myprofile;Button showProfile;
    boolean flag=true;boolean first=true;
    Uri photoURL=null; Uri profilePictureUri;Button show;
    String emailids;String name;
    String nameuser;
    Button upload;String id;
    ImageView load;
    ProgressBar loader; ProgressDialog mProgress;


    RecyclerView fieldlist;Button add;
    private FirebaseDatabase database ;Drawable img;
    private DatabaseReference myRef,databaseReference;
    ArrayList<Map<String, String>> contacts = new ArrayList<Map<String, String>>();
    Uri  profilePictureUri1;String provider;

    Map<String,String > data1 = new HashMap<String, String>();

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            mProgress = new ProgressDialog(getContext());
            mProgress.setTitle("Initializing App");



            mProgress.setCancelable(false);



            //  mProgress.setInverseBackgroundForced(setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY));
            mProgress.show();


            emailids = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            for (UserInfo user:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com")) {
                    provider="facebook";
                }
                else
                    provider="google";
            }
            UserInfo user= FirebaseAuth.getInstance().getCurrentUser();
                if (provider.equals("facebook")) {
                    System.out.println("User is signed in with Facebook");
                    id= Profile.getCurrentProfile().getId();
                    name=Profile.getCurrentProfile().getName();
                    database = FirebaseDatabase.getInstance();
                    databaseReference=database.getReference();
                    OneSignal.startInit(getContext())


                            .unsubscribeWhenNotificationsAreDisabled(false)
                            .init();


                    OneSignal.setSubscription(true);
                    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                        @Override
                        public void idsAvailable(String userId, String registrationId) {
                            FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Notification").setValue(userId);
                        }
                    });
                    OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);



                }
                else {
                    id = user.getUid();
                    name=user.getDisplayName();
                    database = FirebaseDatabase.getInstance();
                    databaseReference=database.getReference();
                    OneSignal.startInit(getContext())


                            .unsubscribeWhenNotificationsAreDisabled(false)
                            .init();


                    OneSignal.setSubscription(true);
                    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                        @Override
                        public void idsAvailable(String userId, String registrationId) {
                            FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Notification").setValue(userId);
                        }
                    });
                    OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);


                }


        }
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child(id).child("Personal").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textView.setText(dataSnapshot.getValue(String.class));
                mProgress.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Photo");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profilePictureUri1= Uri.parse(dataSnapshot.getValue(String.class));
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(getContext());
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(50f) ;
                circularProgressDrawable.start();
                Glide.with(getContext())
                        .load(profilePictureUri1)
                        .placeholder(circularProgressDrawable)
                        .into(imageView1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       // DatabaseReference reference =FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Photo");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profilePictureUri1= Uri.parse(dataSnapshot.getValue(String.class));
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(getContext());
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(50f) ;
                circularProgressDrawable.start();
                Glide.with(getContext())
                        .load(profilePictureUri1)
                        .placeholder(circularProgressDrawable)
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add=root.findViewById(R.id.add);

        show=root.findViewById(R.id.show);
        ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(show, "scaleX", 0.85f);
        ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(show, "scaleY", 0.85f);
        scaleDownX1.setDuration(1500);
        scaleDownY1.setDuration(1500);

        ObjectAnimator moveUpY1 = ObjectAnimator.ofFloat(show, "translationY", -50);
        moveUpY1.setDuration(1500);

        AnimatorSet scaleDown1 = new AnimatorSet();
        AnimatorSet moveUp1 = new AnimatorSet();

        scaleDown1.play(scaleDownX1).with(scaleDownY1);
        moveUp1.play(moveUpY1);

        scaleDown1.start();
        moveUp1.start();

        profile=root.findViewById(R.id.profile);
        myprofile=root.findViewById(R.id.detaillist1);
        showProfile=root.findViewById(R.id.showprofile);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(showProfile, "scaleX", 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(showProfile, "scaleY", 0.9f);
        scaleDownX.setDuration(1500);
        scaleDownY.setDuration(1500);

        ObjectAnimator moveUpY = ObjectAnimator.ofFloat(showProfile, "translationY", -50);
        moveUpY.setDuration(1500);

        AnimatorSet scaleDown = new AnimatorSet();
        AnimatorSet moveUp = new AnimatorSet();

        scaleDown.play(scaleDownX).with(scaleDownY);
        moveUp.play(moveUpY);

        scaleDown.start();
        moveUp.start();
        fieldlist=root.findViewById(R.id.fieldlist);
        fieldlist.setLayoutManager(new LinearLayoutManager(getContext()));




        textView=root.findViewById(R.id.name3);
        imageView1=root.findViewById(R.id.profilePhoto3);
         photoURL=null;String userid=null,phone=null,useridd=null,url=null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("ak47","ghusa");

            photoURL = user.getPhotoUrl();
            userid=user.getDisplayName();
            phone=user.getPhoneNumber();
            useridd=user.getUid();





        }





        else
            Log.d("ak47","ni  ghusa");



        Log.d("chokaa", "onDataChange: " + contacts);
        Log.d("dada", String.valueOf(contacts));
        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldlist.setVisibility(View.GONE);
                upload.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                if(flag1) {
                    showProfile.setCompoundDrawablesWithIntrinsicBounds (getResources().getDrawable(R.drawable.profile),null,getResources().getDrawable(R.drawable.sign),null);

                    profile.animate()

                            .translationY(profile.getHeight())
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    profile.setVisibility(View.GONE);
                                }
                            });
flag1=!flag1;
                    myprofile.setVisibility(View.VISIBLE);
                }
                else
                {
                    add.setVisibility(View.GONE);
                    upload.setVisibility(View.GONE);
                    showProfile.setCompoundDrawablesWithIntrinsicBounds (getResources().getDrawable(R.drawable.profile),null,getResources().getDrawable(R.drawable.signright),null);
                    //scrollView.setVisibility(View.GONE);
                    profile.animate()

                            .alpha(1f)
                            .translationY(-80)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    profile.setVisibility(View.VISIBLE);
                                }
                            });
                    myprofile.setVisibility(View.GONE);


                    flag1=!flag1;
                }
            }
        });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageview1);
                photoView.setImageDrawable(img);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        getdata();

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myprofile.setVisibility(View.GONE);
                if(flag) {



                    show.setCompoundDrawablesWithIntrinsicBounds (getResources().getDrawable(R.drawable.profile),null,getResources().getDrawable(R.drawable.sign),null);
                    profile.animate()
                            .translationY(profile.getHeight())
                            .alpha(0.0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    profile.setVisibility(View.GONE);
                                }
                            });
                    upload.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);

                    if(first) {
                        ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(upload, "scaleX", 0.7f);
                        ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(upload, "scaleY", 0.7f);
                        scaleDownX1.setDuration(100);
                        scaleDownY1.setDuration(1000);

                        ObjectAnimator moveUpY1 = ObjectAnimator.ofFloat(upload, "translationY", 50);
                        moveUpY1.setDuration(1000);

                        AnimatorSet scaleDown1 = new AnimatorSet();
                        AnimatorSet moveUp1 = new AnimatorSet();

                        scaleDown1.play(scaleDownX1).with(scaleDownY1);
                        moveUp1.play(moveUpY1);

                        scaleDown1.start();
                        moveUp1.start();
                        first=!first;
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(add, "scaleX", 0.7f);
                        ObjectAnimator scaleDownY= ObjectAnimator.ofFloat(add, "scaleY", 0.7f);
                        scaleDownX.setDuration(100);
                        scaleDownY.setDuration(1000);

                        ObjectAnimator moveUpY = ObjectAnimator.ofFloat(add, "translationY", 70);
                        moveUpY.setDuration(1000);

                        AnimatorSet scaleDown = new AnimatorSet();
                        AnimatorSet moveUp = new AnimatorSet();

                        scaleDown.play(scaleDownX).with(scaleDownY);
                        moveUp.play(moveUpY);

                        scaleDown.start();
                        moveUp.start();

                    }


                    fieldlist.setVisibility(View.VISIBLE);
                    fieldlist.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);

                                }
                            });

                    flag=!flag;
                }
                else
                {
                    show.setCompoundDrawablesWithIntrinsicBounds (getResources().getDrawable(R.drawable.profile),null,getResources().getDrawable(R.drawable.signright),null);
                    fieldlist.animate()
                            .alpha(0.0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    fieldlist.setVisibility(View.GONE);
                                }
                            });
                    profile.setTranslationY(80);
                    profile.animate()
                            .translationY(-80)
                            .alpha(1.0f)

                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    profile.setVisibility(View.VISIBLE);
                                }
                            });
                    upload.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);



                    flag=!flag;
                }


            }
        });


        upload=root.findViewById(R.id.upload);

       /* github=root.findViewById(R.id.github);*/
        add.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmergency();
            }
        });





       upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
               LayoutInflater inflater = getActivity().getLayoutInflater();



               final View view = inflater.inflate(R.layout.update, null);
               builder.setView(view);
               final Dialog dialog=builder.create();

               dialog.setContentView(R.layout.update);
               dialog.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;
               dialog.getWindow().setBackgroundDrawableResource(R.color.trans);
               // (0x80000000, PorterDuff.Mode.MULTIPLY);
               dialog.show();

               TextView addButton = (TextView) dialog.findViewById(R.id.update);
               TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel);

               addButton.setOnClickListener(



                       new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                      upload();
                      dialog.dismiss();
                   }
               });

               cancelButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       dialog.cancel();
                   }
               });


           }
       });
        return root;
    }
    public void upload()
    {
        database = FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

         Log.d("pops", String.valueOf(recyclerViewFiled.retrieveData()));

        databaseReference.child(id).child("Personal").setValue(recyclerViewFiled.retrieveData());
        //databaseReference.child(id).child("Personal").child("Name").setValue(name);

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Notification").setValue(userId);
            }
        });







        Log.d("joker", String.valueOf(data1));

        Toast.makeText(getContext(),"Profile Updated Successfully",Toast.LENGTH_LONG).show();


    }
    public void addEmergency()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.adddialog, null);
        builder.setView(v);
        final Dialog dialog=builder.create();

        dialog.setContentView(R.layout.adddialog);
        dialog.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;
        dialog.getWindow().setBackgroundDrawableResource(R.color.trans);
       // (0x80000000, PorterDuff.Mode.MULTIPLY);
        dialog.show();

       final EditText nameEdit = (EditText) dialog.findViewById(R.id.detailtype);
       final EditText contactEdit = (EditText) dialog.findViewById(R.id.detail);
         TextView addButton = (TextView) dialog.findViewById(R.id.add);
        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String name = nameEdit.getText().toString();
                String contact =contactEdit.getText().toString();
                if(name.equals("")) {
                    Toast.makeText(getContext(),"Type Field cannot be empty !",Toast.LENGTH_SHORT).show();


                }
                else if(name.trim().equalsIgnoreCase("Name")||name.trim().equalsIgnoreCase("Notification")||name.trim().equalsIgnoreCase("Photo"))
                {
                    Toast.makeText(getContext(),"Invalid Field",Toast.LENGTH_SHORT).show();
                }
                else

                {
                    Map<String, String> map = new HashMap<>();
                    map.put("detailname", name);
                    map.put("detail", contact);
                    if(!map.get("detailname").equals("Notification"))
                    contacts.add(map);

                    recyclerViewDetails.notifyDataSetChanged();
                    recyclerViewFiled.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Field added successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Long press to delete field", Toast.LENGTH_LONG).show();
                    dialog.dismiss();


                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


    }

    private void getdata() {
       // Intent intent=getIntent();
        myprofile.setLayoutManager(new LinearLayoutManager(getContext()));

       // if(intent.hasExtra("Data")) {
          //HashMap<String,String>  data = (HashMap<String, String>)
       // }
        final HashMap<String, String>[] data = new HashMap[]{new HashMap<String, String>()};
        String currentUser = id;
        database = FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);
        myRef = database.getReference().child(currentUser).child("Personal");
        myRef.keepSynced(true);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contacts.clear();


                    Map<String ,String> mp1=new HashMap<>();

                mp1=(Map<String, String>) dataSnapshot.getValue();
                Log.d("therem", String.valueOf(mp1));
                data1=mp1;

                data[0] = (HashMap<String, String>) mp1;
                Log.d("therems", String.valueOf(data[0]));
                Log.d("theremss", String.valueOf(data[0]));
                for (Map.Entry<String, String> entry : data[0].entrySet()) {
                    Map<String ,String> mp2=new HashMap<>();
                    mp2.put("detailname",entry.getKey());
                    mp2.put("detail",entry.getValue());
                    if(!mp2.get("detailname").equals("Notification"))

                        contacts.add(mp2);
                    Log.d("pens", String.valueOf(contacts));
                    recyclerViewFiled=new RecyclerViewFiled(contacts,getContext());
                    fieldlist.setAdapter(recyclerViewFiled);

                    recyclerViewDetails = new RecyclerViewDetails(contacts,getContext());
                    Log.d("tutua", String.valueOf(contacts));
                    myprofile.setAdapter(recyclerViewDetails);




                }





                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        Log.d("mamas", String.valueOf(data[0]));



        Log.d("mama", String.valueOf(contacts));

    }



}