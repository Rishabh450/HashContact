package com.rishabh.hashcontact.Support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import com.rishabh.hashcontact.MapsFragment;
import com.rishabh.hashcontact.MediaAdapter;
import com.rishabh.hashcontact.Messege;
import com.rishabh.hashcontact.R;
import com.rishabh.hashcontact.RecyclerViewChatAdapter;
import com.rishabh.hashcontact.SendNotification;
import com.facebook.Profile;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class ChatBox extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseDatabase database ;
    private DatabaseReference myRefCurrent,myRefUser2,databaseReference;
    FirebaseAuth mAuth;ImageView calluser;
    TextView name;
    FloatingActionButton sendimage;Uri outuri=null;int sflag=0;
    double lat,lng;int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    MapView mapView;
    RecyclerView mMedia;TextView seen;DateFormat df;
RecyclerView.LayoutManager mMediaLayoutManager;
    TextView lastseen;Location lastKnown;
    Fragment maper;SupportMapFragment mapFragment;
    ConstraintLayout rootView;
    String msgseen="false";int f=0;
Drawable img;int flag=0;

LinearLayout composer,blockmsg,tool;
TextView changeback,mute,block,defaul,custom;
ImageView dp;int sentflag=0;
    String currentUser,user2;
    int floc=0;
    private LocationManager locationManager;
    private String provider;
    Button back;
    LinearLayout frag;
    Uri  lo;
    Animation fabopen,fabclose,fabclock,fabanticlock;
    EmojiconEditText messegeComposer;
    private GoogleMap mMap;
    private ProgressBar recyclerview_progress;
    Button send;FloatingActionButton loc;
    FloatingActionButton location;FloatingActionButton voice;
    String filename;
    LinearLayout maplay;
    Button shareloc;
    ImageView b;RecyclerView recyclerView;
    int emojif=0;
    ArrayList<Messege> messeges=new ArrayList<Messege>();
    int fopen=1;FloatingActionButton clip;

    RecyclerViewChatAdapter chatAdapter;
    MediaAdapter mMediaAdapter;
    Button cross;
    String providerr;

    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_chat_box);
        /* final String APP_ID = "1688";
         final String AUTH_KEY = "C3DBrqhVjVEdqkH";
         final String AUTH_SECRET = "ywTXBNSn36N3kAH";
         final String ACCOUNT_KEY = "Wxjjy5q_ZVYynFNJwtyP";
//
        ConnectycubeSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        ConnectycubeSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        ConnectycubeSettings.getInstance().setLogLevel(LogLevel.NOTHING);*/
        /*ConnectycubeChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new VideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(Signaling signaling, boolean createdLocally) {
                if (!createdLocally) {
                    RTCClient.getInstance(ChatBox.this).addSignaling((WebRTCSignaling) signaling);
                }
            }
        });*/

        b=findViewById(R.id.emoji);

        recyclerView=findViewById(R.id.chatbox);
        for (UserInfo user:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                providerr="facebook";
                currentUser=Profile.getCurrentProfile().getId();
            }
            else {
                providerr = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser=userInfo.getUid();
            }
        }



      /*  final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(messegeComposer);
        emojiPopup.toggle(); // Toggles visibility of the Popup.
       // emojiPopup.dismiss(); // Dismisses the Popup.
        emojiPopup.isShowing(); // Returns true when Popup is showing.*/

        fabopen=AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabclose=AnimationUtils.loadAnimation(this,R.anim.fab_close);
        fabclock=AnimationUtils.loadAnimation(this,R.anim.rotate_clockwise);
        fabanticlock=AnimationUtils.loadAnimation(this,R.anim.rotate_anticlockwise);
        rootView = findViewById(R.id.rootview);
        calluser = findViewById(R.id.call);
        tool=findViewById(R.id.tool);
        tool.getBackground().setAlpha(99);
        frag=findViewById(R.id.frag);
        clip=findViewById(R.id.clip);


        cross=findViewById(R.id.cross);
       // mapView=findViewById(R.id.mapView);
        maplay=findViewById(R.id.maplay);
        maplay.animate()

                .translationY(maplay.getHeight())

                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        maplay.setVisibility(View.GONE);
                    }
                });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maplay.animate()

                        .translationY(maplay.getHeight())

                        .setDuration(600)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                maplay.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });

            }
        });
        recyclerview_progress=findViewById(R.id.recycler_view_progress);

        voice=findViewById(R.id.voice_search);
        loc=findViewById(R.id.loc);
shareloc=findViewById(R.id.shareloc);
        cross.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
        shareloc.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);


        composer=findViewById(R.id.composer);
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* mapFragment = new MapsFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.map, mapFragment);
                transaction.commit();*/
                 if(ContextCompat.checkSelfPermission(ChatBox.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(ChatBox.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);


                }
                 else{
                     clip.callOnClick();
                     maplay.animate()

                             .translationY(0)

                             .setDuration(0)
                             .setListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                     super.onAnimationEnd(animation);
                                     maplay.setVisibility(View.VISIBLE);
                                     recyclerView.setVisibility(View.GONE);
                                 }
                             });
                    // maplay.setVisibility(View.VISIBLE);
                      mapFragment = (SupportMapFragment) getSupportFragmentManager()
                             .findFragmentById(R.id.map);



                     mapFragment.getMapAsync(ChatBox.this);


                 }


            }
        });

        /*if(composer!=null)
            composer.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);*/
        blockmsg=findViewById(R.id.blockmsg);
        OneSignal.pauseInAppMessages(true);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference isOnlineRef = rootRef.child(currentUser).child("isOnline");
        isOnlineRef.setValue("true");
        final String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Bitmap bit = BitmapFactory.decodeResource(getResources(), R.mipmap.chatba);
        FileOutputStream outStream = null;

        // Write to SD Card
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/HashContact");
            dir.mkdirs();
            long t = System.currentTimeMillis();
            String time = String.valueOf(t);

            String fileName = "default.jpg";
            File outFile = new File(dir, fileName);

            outStream = new FileOutputStream(outFile);
            bit.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

            Log.d("pathsss", "onPictureTaken - wrote to " + outFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            String photoPath = Environment.getExternalStorageDirectory() + "/HashContact/background.jpg";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            final Bitmap b = BitmapFactory.decodeFile(photoPath, options);
            Drawable mDrawable = new BitmapDrawable(getResources(), b);
            if (b != null)
                rootView.setBackground(mDrawable);
            else {
                Log.d("nhimilaimage", "nhi");
                rootView.setBackground(getDrawable(R.mipmap.chatba));
            }

        } catch (Exception e) {
            e.printStackTrace();

            //rootView.setBackgroundColor(getColor(android.R.color.black));
        }


       /* File directory = new File (sdCard.getAbsolutePath() + "/hashcontact");

        File file = new File(directory, "background.jpg"); //or any other format supported

        FileInputStream streamIn = null;
        try {
            streamIn = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
            rootView.setBackground(mDrawable);
            if(bitmap!=null)
            Log.d("sethogya","true");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("sethogya","false");
        }

        Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
       // Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
        rootView.setBackground(mDrawable);

        try {
            streamIn.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("sethogya","falseband");
        }*/
        seen = findViewById(R.id.seen);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // onDestroy();
                finish();
            }
        });
        init();
        getmesseges();
        final DatabaseReference refe=FirebaseDatabase.getInstance().getReference().child("Communication");
        refe.child(user2).child("Messege").child(currentUser).child("seen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String x=dataSnapshot.getValue(String.class);
                if(x.equals("1"))
                    OneSignal.clearOneSignalNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // if( seen.getText().toString().equals("seen"))

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clip.callOnClick();
                promptSpeechInput();
            }
        });

        final long delay = 2000; // 1 seconds after user stops typing
        final long last_text_edit = 0;
        DatabaseReference ref=  FirebaseDatabase.getInstance().getReference().child("Communication").child(user2).child("Messege").child(currentUser);
        ref.child("Block").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String x= String.valueOf(dataSnapshot.getValue());
                if(x.equals("Yes")){
                    composer.setVisibility(View.GONE);
                    blockmsg.setVisibility(View.VISIBLE);

                }
                else {
                    composer.setVisibility(View.VISIBLE);
                    blockmsg.setVisibility(View.GONE);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messegeComposer.addTextChangedListener(new TextWatcher() {
            boolean isTyping = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {




            }


            Timer timer = new Timer();
            final long DELAY = 2500;


            @Override
            public void afterTextChanged(Editable editable) {

                if (!isTyping) {
                    Log.d("maiji", "started typing");
                    // Send notification for start typing event
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Communication");
                    ref.child(currentUser).child("Messege").child(user2).child("isTyping").setValue("true");
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                isTyping = false;
                                Log.d("maiji", "stopped typing");
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Communication");
                                ref.child(currentUser).child("Messege").child(user2).child("isTyping").setValue("false");
                                //send notification for stopped typing event
                            }
                        },
                        DELAY
                );


                // handler.postDelayed(input_finish_checker, delay);


            }
        });
        final int[] number_of_clicks = {0};
        final boolean[] thread_started = {false};
        final int DELAY_BETWEEN_CLICKS_IN_MILLISECONDS = 250;
        clip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fopen==1)
                {
                    location.startAnimation(fabopen);
                    voice.startAnimation(fabopen);
                    clip.startAnimation(fabclock);
                    sendimage.startAnimation(fabopen);
                    loc.startAnimation(fabopen);
                    voice.setClickable(true);
                    clip.setClickable(true);
                    sendimage.setClickable(true);
                    location.setClickable(true);
                    loc.setClickable(true);

                    fopen=0;

                }
                else
                {
                    location.startAnimation(fabclose);
                    voice.startAnimation(fabclose);
                    clip.startAnimation(fabanticlock);
                    sendimage.startAnimation(fabclose);
                    loc.startAnimation(fabclose);
                    voice.setClickable(false);
                    clip.setClickable(true);
                    sendimage.setClickable(false);
                    location.setClickable(false);
                    loc.setClickable(false);

                    fopen=1;


                }

            }
        });

calluser.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        AlertDialog.Builder builder=new AlertDialog.Builder(ChatBox.this);
        LayoutInflater inflater = ChatBox.this.getLayoutInflater();



        final View view = inflater.inflate(R.layout.backgrounddialog, null);
        builder.setView(view);
        final Dialog dialog=builder.create();

        dialog.setContentView(R.layout.backgrounddialog);
        dialog.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;
        dialog.getWindow().setBackgroundDrawableResource(R.color.trans);
        // (0x80000000, PorterDuff.Mode.MULTIPLY);
        dialog.show();
        final TextView[] clear = new TextView[1];
        final int[] cleared = {0};
        clear[0] =dialog.findViewById(R.id.clear);
        clear[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleared[0] =1;

                DatabaseReference referencee=FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("chat");
                referencee.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                        {
                           String ke=dataSnapshot1.getKey();
                           if(!ke.equals("chattingAt")&&!ke.equals("Notification")&&!ke.equals("Block")&&!ke.equals("isTyping")&&!ke.equals("lastMessege")&&!ke.equals("lastMessegee")&&!ke.equals("seen")&&cleared[0]==1)
                               FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("chat").child(ke).removeValue();


                        }
                       // Toast.makeText(ChatBox.this,"Chat Cleared",Toast.LENGTH_SHORT).show();
                        cleared[0]=0;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
              /*  FirebaseDatabase.getInstance().getReference().child("Communication").child(Profile.getCurrentProfile().getId()).child("Messege").child(user2).child("isTyping").setValue("false");
                FirebaseDatabase.getInstance().getReference().child("Communication").child(Profile.getCurrentProfile().getId()).child("Messege").child(user2).child("lastMessege").setValue("0");
                final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
                ref.child(Profile.getCurrentProfile().getId()).child("Messege").child(user2).child("chattingAt").setValue("0");

                ref.child(Profile.getCurrentProfile().getId()).child("Messege").child(user2).child("Notification").setValue("Yes");
                ref.child(Profile.getCurrentProfile().getId()).child("Messege").child(user2).child("Block").setValue("No");
*/
            }
        });
        mute   =dialog.findViewById(R.id.mute);
        block=dialog.findViewById(R.id.block);
        changeback=dialog.findViewById(R.id.changeback);
        defaul=dialog.findViewById(R.id.original);
        custom=dialog.findViewById(R.id.fromgallery);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("Block");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String x= String.valueOf(dataSnapshot.getValue());
                if(x.equals("Yes")){
                    block.setText("Unblock");
                    block.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("Block").setValue("No");
                            block.setText("Block");

                        }
                    });

                }
                else
                {
                    block.setText("Block");
                    block.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("Block").setValue("Yes");
                            block.setText("Unblock");

                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("Notification");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String x= String.valueOf(dataSnapshot.getValue());
                if(x.equals("Yes")){
                    mute.setText("Mute Notification");
                    mute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("Notification").setValue("No");
                            mute.setText("Unmute Notification");
                        }
                    });

                }
                else{
                    mute.setText("Unmute Notification");
                    mute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("Notification").setValue("Yes");
                            mute.setText("Mute Notification");
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(f==0){
                    defaul.setVisibility(View.VISIBLE);
                    f=1;
                    custom.setVisibility(View.VISIBLE);}
                else
                {
                    defaul.setVisibility(View.GONE);
                    custom.setVisibility(View.GONE);
                    f=0;
                }
            }
        });

        TextView addButton = (TextView) dialog.findViewById(R.id.original);
        TextView cancelButton = (TextView) dialog.findViewById(R.id.fromgallery);

        addButton.setOnClickListener(



                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doubleClickBack();
                        dialog.dismiss();
                    }
                });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleClickback();
                dialog.dismiss();
            }
        });

    }
});






      //  final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
    FirebaseDatabase.getInstance().getReference().child(currentUser).child("ChattingWith").setValue(user2);
              // runInBackground();
              // OneSignal.sendTag(user2,"disabled");

seen();
        final DatabaseReference myref=FirebaseDatabase.getInstance().getReference();
        myref.child(user2).child("ChattingWith").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {

                //
                myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessege").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String lastSent=dataSnapshot.getValue(String.class);
                        Log.d("sentme",lastSent);
                        final String lastRec=dataSnapshot.getValue(String.class);
                        Log.d("recme",lastRec);
                        myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessegee").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                FirebaseDatabase.getInstance().getReference().child(user2).child("ChattingWith").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final String chattingWith=dataSnapshot.getValue(String.class);
                                        Log.d("chatwithme",chattingWith);
                                        if((Long.parseLong( lastSent)>Long.parseLong(lastRec)))
                                            Log.d("seenhonachahiye","true");
                                        if((Long.parseLong( lastSent)>Long.parseLong(lastRec) )&&chattingWith.equals(currentUser)){
                                            seen.setText("seen");
                                            final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
                                            ref.child(currentUser).child("Messege").child(user2).child("chattingAt").setValue(String.valueOf(System.currentTimeMillis()));
                                            // if( seen.getText().toString().equals("seen"))
                                            ref.child(currentUser).child("Messege").child(user2).child("seen").setValue("1");
                                            Log.d("yahakaam3","seen"+chattingWith+" "+currentUser);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });





                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessege").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastSent=dataSnapshot.getValue(String.class);
                final long sentmsg= Long.parseLong(lastSent);
                myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessegee").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String lastRec=dataSnapshot.getValue(String.class);
                        long rectmsg= Long.parseLong(lastRec);
                        if(sentmsg<rectmsg)
                        {
                            FirebaseDatabase.getInstance().getReference().child("Communication").child(user2).child("Messege").child(currentUser).child("seen").setValue("1");
                            /*if(val.equals("1"))
                                seen.setText("seen");*/
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            shareloc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            FileOutputStream outStream = null;
                            try {
                                File sdCard = Environment.getExternalStorageDirectory();
                                File dir = new File(sdCard.getAbsolutePath() + "/HashContact/Map");
                                dir.mkdirs();
                                long t = System.currentTimeMillis();
                                String time = String.valueOf(t);
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                String imageFileName = timeStamp + "."+"jpeg";
                                 filename = timeStamp+".jpg";
                                File filee = new File(dir, filename);

                                outStream = new FileOutputStream(filee);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                                outStream.flush();
                                outStream.close();
                              lo = Uri.fromFile(filee);

                               // Log.d("pathsss", "onPictureTaken - wrote to " + outF.getAbsolutePath());
                                mediaUriList.clear();
                                mediaUriList.add(String.valueOf(lo));
                                if(!mediaUriList.isEmpty()) {
                                    sendFile();
                                    floc=1;
                                   // Toast.makeText(ChatBox.this,"sending location",Toast.LENGTH_SHORT).show();
                                }

                                maplay.animate()

                                        .translationY(maplay.getHeight())

                                        .setDuration(500)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                maplay.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                            }
                                        });



                                // Log.d("makama", String.valueOf(outuri));


                                   /* messegeComposer.setText("LOCATION:-"+lastKnown.getLatitude()+"||"+lastKnown.getLongitude());
                                    send.callOnClick();*/



                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    });

                }
            });
        myref.child("Communication").child(currentUser).child("Messege").child(user2).child("seen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String val=dataSnapshot.getValue(String.class);
                Log.d("seenwa",val);
               /* if(val.equals("1"))
                    seen.setText("seen");
                else
                    seen.setText("");*/
                myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessege").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String lastSent=dataSnapshot.getValue(String.class);
                        final long sentmsg= Long.parseLong(lastSent);
                        myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessegee").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String lastRec=dataSnapshot.getValue(String.class);
                                long rectmsg= Long.parseLong(lastRec);
                                if(sentmsg>rectmsg)
                                {

                                    if(val.equals("1")){
                                        Log.d("yahakaam1","seen");
                                        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
                                        ref.child(currentUser).child("Messege").child(user2).child("chattingAt").setValue(String.valueOf(System.currentTimeMillis()));
                                        // if( seen.getText().toString().equals("seen"))
                                        ref.child(currentUser).child("Messege").child(user2).child("seen").setValue("1");
                                        seen.setText("seen");}
                                    else {
                                        Log.d("yahakaam1","blank");
                                        seen.setText("");}
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       // b.callOnClick();
dp.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        if(img!=null){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ChatBox.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
        PhotoView photoView = mView.findViewById(R.id.imageview1);
        photoView.setImageDrawable(img);
        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();}
        else
            Log.d("photonahiaaya","haha");

    }
});


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MapsFragment.MY_PERMISSIONS_REQUEST_LOCATION){
            this.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void selectImage() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, 200);

    }
    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                ActivityCompat.requestPermissions(ChatBox.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);


            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    public class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        public Context context;
        public String phno;

        public MyGestureDetector(Context con) {
            this.context=con;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            doubleClickBack();
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            doubleClickBack();

            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            System.out.println("in single tap up");
            //put your second activity.



            return super.onSingleTapUp(e);
        }
    }




    public static boolean isRecursionEnable = true;

    void runInBackground() {
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ref.child(currentUser).child("Messege").child(user2).child("chattingAt").setValue(String.valueOf(System.currentTimeMillis()));
                runInBackground();
            }
        });
                 /*   ref.child(currentUser).child("Messege").child(user2).child("chattingAt").setValue(String.valueOf(System.currentTimeMillis()));
                    runInBackground();
*/



    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {



            mMap.setBuildingsEnabled(true);
            mMap.clear();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LatLng sydney = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Your current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,17));


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 20, locationListener);
             lastKnown=getLastKnownLocation();
            recyclerview_progress.setVisibility(View.GONE);
            shareloc.setVisibility(View.VISIBLE);

            mMap.setBuildingsEnabled(true);
            mMap.clear();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LatLng sydney = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Your current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,17));

        }


        // Add a marker in Sydney and move the camera

    }
    int REQ_CODE_SPEECH_INPUT=1000;
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.enter_journal_message));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (Exception e) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
        ref.child(currentUser).child("Messege").child(user2).child("chattingAt").setValue(String.valueOf(System.currentTimeMillis()));
       if( seen.getText().toString().equals("seen"))
        ref.child(currentUser).child("Messege").child(user2).child("seen").setValue("1");
       else
           ref.child(currentUser).child("Messege").child(user2).child("seen").setValue("0");
        final FirebaseDatabase[] database2 = {FirebaseDatabase.getInstance()};
        DatabaseReference databaseReference2= database2[0].getReference().child(currentUser).child("ChattingWith");
        int rand_int1 = Math.abs(ThreadLocalRandom.current().nextInt()) ;
String rand= String.valueOf(rand_int1);
        databaseReference2.setValue(rand);




    }
    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void locate() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            Toast.makeText(this, "Location permission not given", Toast.LENGTH_LONG).show();
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);


        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            //onLocationChanged(location);
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {
            Toast.makeText(this, "location not available", Toast.LENGTH_LONG).show();
        }


    }
    public void seen()
    {
        final DatabaseReference myref=FirebaseDatabase.getInstance().getReference();
       /* myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessege").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastmsg= (String) dataSnapshot.getValue();
                final long lastmsgtime= Long.parseLong(lastmsg);
                myref.child("Communication").child(user2).child("Messege").child(currentUser).child("chattingAt").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String lastch= (String) dataSnapshot.getValue();
                        long lastchat= Long.parseLong(lastch);
                        if(lastmsgtime<lastchat){
                            msgseen="true";
                        seen.setText("seen");}
                        else
                        {
                            seen.setText("");
                            msgseen="false";}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessegee").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastmsgRec= (String) dataSnapshot.getValue();
                final long lastmsgtimeRec= Long.parseLong(lastmsgRec);
                myref.child("Communication").child(currentUser).child("Messege").child(user2).child("lastMessege").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String lastchSent= (String) dataSnapshot.getValue();
                        long lastchatSent= Long.parseLong(lastchSent);
                        if(lastmsgtimeRec>lastchatSent){
                            msgseen="false";
                            seen.setText("");
                        }
                        else{
                            myref.child(user2).child("ChattingWith").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String chatwith=dataSnapshot.getValue(String.class);
                                    //long chatWithNum= Long.parseLong(chatwith);
                                    if(chatwith.equals(currentUser)){
                                        seen.setText("seen");
                                        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
                                        ref.child(currentUser).child("Messege").child(user2).child("chattingAt").setValue(String.valueOf(System.currentTimeMillis()));
                                       // if( seen.getText().toString().equals("seen"))
                                            ref.child(currentUser).child("Messege").child(user2).child("seen").setValue("1");
                                        sentflag=0;
                                        //FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("seen").setValue("1");

                                        Log.d("yahakaam2","seen");}
                                    else if(sentflag==1){Log.d("yahakaam2","blank");
                                        seen.setText("");}
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void singleClickback()
    {
        Snackbar.make(rootView, "Change chat background", Snackbar.LENGTH_LONG)

                .setActionTextColor(getResources().getColor(android.R.color.white ))
                .show();
        //Toast.makeText(ChatBox.this,"Changing Background..Please wait",Toast.LENGTH_LONG).show();
        if(ContextCompat.checkSelfPermission(ChatBox.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            // Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(ChatBox.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);


        }
        else

            selectImage();

    }

    public void doubleClickBack(){
        if (ContextCompat.checkSelfPermission(ChatBox.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Log.e(TAG, "setxml: peremission prob");
            ActivityCompat.requestPermissions(ChatBox.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);


        } else {
            Bitmap bit = BitmapFactory.decodeResource(getResources(), R.mipmap.chatba);

            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/HashContact");
                dir.mkdirs();
                long t = System.currentTimeMillis();
                String time = String.valueOf(t);

                String fileName = "background.jpg";
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                bit.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();

                Log.d("pathsss", "onPictureTaken - wrote to " + outFile.getAbsolutePath());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
rootView.setBackground(getDrawable(R.mipmap.chatba));
          //  recreate();


        }

    }

    private void getmesseges() {
        final ArrayList<Messege> sent=new ArrayList<Messege>();
        final ArrayList<Messege> recive=new ArrayList<Messege>();



        myRefCurrent = database.getReference().child("Communication").child(currentUser).child("Messege").child(user2).child("chat");
       myRefCurrent.keepSynced(true);
        myRefUser2 = database.getReference().child("Communication").child(user2).child("Messege").child(currentUser).child("chat");
        myRefUser2.keepSynced(true);
        myRefCurrent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sent.clear();
                messeges.clear();
                long size=chatAdapter.getItemCount();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Log.e("ak471",dataSnapshot1.getValue()+" "+dataSnapshot1.getKey());
                    if(!dataSnapshot1.getKey().equals("isTyping")&&!dataSnapshot1.getKey().equals("chattingAt")&&!dataSnapshot1.getKey().equals("lastMessege")&&!dataSnapshot1.getKey().equals("seen")&&!dataSnapshot1.getKey().equals("lastMessegee")&&!dataSnapshot1.getKey().equals("Notification")&&!dataSnapshot1.getKey().equals("Block")){
                    Messege m=new Messege((String) dataSnapshot1.getValue(),Long.parseLong(dataSnapshot1.getKey()) ,true);

                    sent.add(m);
                    }
                }

                messeges.addAll(sent);
                messeges.addAll(recive);
                long newsize=chatAdapter.getItemCount();
                if(newsize>size)
                    recyclerView.scrollToPosition(0);

                Collections.sort(messeges);

                chatAdapter.notifyDataSetChanged();






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRefUser2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recive.clear();
                messeges.clear();

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    if(!dataSnapshot1.getKey().equals("isTyping")&&!dataSnapshot1.getKey().equals("chattingAt")&&!dataSnapshot1.getKey().equals("lastMessege")&&!dataSnapshot1.getKey().equals("lastMessegee")&&!dataSnapshot1.getKey().equals("seen")&&!dataSnapshot1.getKey().equals("Notification")&&!dataSnapshot1.getKey().equals("Block")){
                    Messege m=new Messege((String) dataSnapshot1.getValue(),Long.valueOf(dataSnapshot1.getKey()),false);
                    recive.add(m);
                    recyclerView.scrollToPosition(0);}
                }
                if(!recive.isEmpty()){
               Messege lastRec =recive.get(recive.size() - 1);
                String timelast= String.valueOf(lastRec.time);
                    databaseReference.child(currentUser).child("Messege").child(user2).child("lastMessegee").setValue(timelast);}
                else
                {
                    Log.d("khaalihai","Nomsgs");
                }
              //  databaseReference.child(currentUser).child("Messege").child(user2).child("lastMessege").setValue(timz);

               // Log.d("milnekatime",timelast);
                messeges.addAll(sent);
                messeges.addAll(recive);
                Collections.sort(messeges);
                chatAdapter.notifyDataSetChanged();
                RecyclerView recyclerView=findViewById(R.id.chatbox);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void init() {

        sendimage=findViewById(R.id.sendimage);
        sendimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ChatBox.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(ChatBox.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);


                }
                else{
                    clip.callOnClick();
                openGallery();}
            }
        });
        location=findViewById(R.id.location);
        final RecyclerView recyclerView=findViewById(R.id.chatbox);

       // recyclerView.smoothScrollToPosition(0);


        Intent intent=getIntent();
        mAuth=FirebaseAuth.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference isOnlineRef = rootRef.child(currentUser).child(uid).child("isOnline");
        isOnlineRef.setValue(true);
        isOnlineRef.onDisconnect().setValue(false);



        messegeComposer=findViewById(R.id.MessgeContent);

        send=findViewById(R.id.sendMessege);
        /*Thread thread = new Thread() {
            @Override
            public void run() {

                while (true) {
                    if (messegeComposer.getText().toString().equals("")) {
                        Log.d("uithreadwaala", "fade");
                        send.setBackgroundResource(R.drawable.fadedsend);
                    } else {
                        Log.d("uithreadwaala", "bright");
                        send.setBackgroundResource(R.drawable.send);
                    }

                }

            }
        };

        thread.start();*/
       location.setBackgroundResource(R.drawable.location);
        database = FirebaseDatabase.getInstance();
        databaseReference=database.getReference().child("Communication");
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ChatBox.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(ChatBox.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


                }
                else
                {
                    if(ContextCompat.checkSelfPermission(ChatBox.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                    {
                        // Log.e(TAG, "setxml: peremission prob");
                        ActivityCompat.requestPermissions(ChatBox.this,new String[]{Manifest.permission.CAMERA},0);


                    }
                    else{
                        clip.callOnClick();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                try {
                    Date date = new Date();
                    df = new SimpleDateFormat("-mm-ss");


                    sflag = 1;
                    FileOutputStream outStream = null;

                    // Write to SD Card

                    File sdCard1 = Environment.getExternalStorageDirectory();
                    // String outPath = sdCard.getAbsolutePath() + "/hashcontact/"+"Camera/"+df.format(date) + ".jpg";

                    File dir = new File(sdCard1.getAbsolutePath() + "/HashContact/" + "Camera/");
                    dir.mkdirs();
                    long t = System.currentTimeMillis();
                    String time = String.valueOf(t);

                    String fileName = df.format(date) + ".jpg";
                    filename = fileName;
                    File outFile1 = new File(dir, fileName);

                    //outStream = new FileOutputStream(outFile);
                    outuri = Uri.fromFile(outFile1);

                    // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                  /*  outStream.flush();
                    outStream.close();*/

                    // Log.d("pathsss", "onPictureTaken - wrote to " + outFile.getAbsolutePath());


                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);


                    // intent.putExtra(MediaStore.EXTRA_OUTPUT, createImageFile());

              /*  try {
                  //  intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(ChatBox.this, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/


                    if (intent.resolveActivity(getPackageManager()) != null)
                        startActivityForResult(intent, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }}

            }}
        });
        messegeComposer.setEmojiconSize(100);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EmojIconActions emojIcon=new EmojIconActions(ChatBox.this,rootView,messegeComposer,b);
                emojIcon.ShowEmojIcon();
                emojIcon.setUseSystemEmoji(true);

                emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
                    @Override
                    public void onKeyboardOpen() {
                        Log.e("Keyboard","open");
                       // recyclerView.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onKeyboardClose() {
                        Log.e("Keyboard","close");
                       // recyclerView.smoothScrollToPosition(0);
                    }
                });

            }
        });




    /*    b.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                location.setBackground(null);

                EmojIconActions emojIcon=new EmojIconActions(ChatBox.this,rootView,messegeComposer,location);
                emojIcon.ShowEmojIcon();

                emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
                    @Override
                    public void onKeyboardOpen() {
                        Log.e("Keyboard","open");
                        recyclerView.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onKeyboardClose() {
                        Log.e("Keyboard","close");
                        recyclerView.smoothScrollToPosition(0);
                    }
                });
                emojIcon.setUseSystemEmoji(true);
                messegeComposer.setEmojiconSize(100);
              *//*  if(ContextCompat.checkSelfPermission(ChatBox.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(ChatBox.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
                }else {
                    locate();
                    String messege="LOCATION:-"+lat+"||"+lng;
                    long ts=(long)System.currentTimeMillis();
                    databaseReference.child(currentUser).child("Messege").child(user2).child(String.valueOf(ts)).setValue(messege);
                    messegeComposer.setText("");
                    locate();
                    String messege2="Tap on the above text to locate the person";
                    long t2s=(long)System.currentTimeMillis();
                    databaseReference.child(currentUser).child("Messege").child(user2).child(String.valueOf(t2s)).setValue(messege2);
                    messegeComposer.setText("");

                }
*//*
                // String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng );


            }
        });*/
        messegeComposer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
               // recyclerView.smoothScrollToPosition(0);
                return true;


            }
        });
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

                if (heightDiff > 500) {
                    //recyclerView.smoothScrollToPosition(0);
                    Log.e("MyActivityyy", "keyboard opened");
                } else {
                    Log.e("MyActivityyy", "keyboard closed");
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                if(!messegeComposer.getText().toString().trim().equals("")) {
                    sentflag=1;

                    final String[] notificationKey = new String[1];
                    final FirebaseDatabase[] database1 = {FirebaseDatabase.getInstance()};
                    DatabaseReference databaseReference1= database1[0].getReference();
                    databaseReference1.child(user2).child("Personal").child("Notification").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String messege=messegeComposer.getText().toString().trim();
                            messegeComposer.setText("");
                            long ts = (long) System.currentTimeMillis();
                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            try {

                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                                Date dt = sdf.parse(currentTime);

                                SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                                currentTime = sdfs.format(dt);



                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            messege=messege+"\n"+currentTime;

                            notificationKey[0] =dataSnapshot.getValue(String.class);
                            //.title.setText(namer);
                            Log.d("stringwa", notificationKey[0]);
                            databaseReference.child(currentUser).child("Messege").child(user2).child("chat").child(String.valueOf(ts)).setValue(messege);
                            databaseReference.child(currentUser).child("Messege").child(user2).child("lastMessege").setValue(String.valueOf(ts));

                            final FirebaseDatabase[] database2 = {FirebaseDatabase.getInstance()};
                            final DatabaseReference databaseReference2= database2[0].getReference().child(user2).child("ChattingWith");
                            final String finalMessege = messege;
                            databaseReference2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                   //String chattingwith= dataSnapshot2.getValue(String.class);

                                    final boolean se;
                                    String with= String.valueOf(dataSnapshot2.getValue());

                                    if(with.equals(currentUser))
                                    {
                                        se=false;
                                    notificationKey[0]=null;}
                                    else
                                        se=true;
                                    DatabaseReference databaseReferencee2= FirebaseDatabase.getInstance().getReference().child("Communication").child(user2).child("Messege").child(currentUser).child("Notification");
                                    databaseReferencee2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String xyz= String.valueOf(dataSnapshot.getValue());
                                            if(xyz.equals("No"))
                                                notificationKey[0]=null;
                                            DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
                                            rootRef.child(currentUser).child("Personal").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    SendNotification sendNotification=new SendNotification(finalMessege,dataSnapshot.getValue(String.class),notificationKey[0]);



                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });



                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });




                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                   // Date currentTime = Calendar.getInstance().getTime();
                   // Toast.makeText(ChatBox.this,currentTime,Toast.LENGTH_SHORT).show();

                }
               // recyclerView.smoothScrollToPosition(0);
            }
        });





        Log.e("ak47","ChatBoxinit");

        if(intent.hasExtra("id"))
        {

            String owner=intent.getStringExtra("id");
            Log.d("chaterror",owner);
            user2=owner;
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ChatBox.this);
            linearLayoutManager.setReverseLayout(true);


            recyclerView.setLayoutManager(linearLayoutManager);
            chatAdapter=new RecyclerViewChatAdapter(ChatBox.this,messeges,currentUser,user2);
            recyclerView.setAdapter(chatAdapter);
            dp=findViewById(R.id.dp);
            name=findViewById(R.id.username);
            lastseen=findViewById(R.id.lastseen);
            FirebaseDatabase databa=FirebaseDatabase.getInstance();
            DatabaseReference dataref=databa.getReference();
            dataref.child(user2).child("Personal").child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    String namer =dataSnapshot.getValue(String.class);
                    if(namer.indexOf(" ")!=-1)
                        namer=namer.substring(0,namer.indexOf(" "));
                   name.setText(namer);
                    Log.d("stringwa",namer);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            dataref.child(user2).child("isOnline").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication").child(user2).child("Messege").child(currentUser).child("isTyping");

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            String namer1 =dataSnapshot.getValue(String.class);






                            String namer =dataSnapshot1.getValue(String.class);

                            Log.d("stringwa",namer);
                            if(namer.equals("true"))
                            {

                                lastseen.setText("Typing...");
                                flag=0;
                            }

                            else  if (namer1.equals("true")){

                                lastseen.setText("Online");
                                flag=0;
                            }

                            else {
                                String[] lines = namer1.split(System.getProperty("line.separator"));
                                String currentTime = lines[1];
                                String dat=lines[0].substring(10);
                                Log.d("datewa",dat);
                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                int curday= Integer.parseInt(currentDate.substring(0,2));
                                int activeday= Integer.parseInt(dat.substring(0,2));
                                if(dat.equals(currentDate))
                                    lines[0]="Today";
                                else if(dat.substring(2).equals(currentDate.substring(2))&&(curday-activeday)==1)
                                    lines[0]="Yesterday";

                                try {

                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                                    Date dt = sdf.parse(currentTime);

                                    SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                                    currentTime = sdfs.format(dt);



                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                lastseen.setText(lines[0]+" at "+currentTime);

                                flag=1;

                            }




                            Log.d("stringwa",namer);



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                   // Log.d("stringwa",namer);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            dataref.child(user2).child("Personal").child("Photo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    String namer =dataSnapshot.getValue(String.class);
                    CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(getApplicationContext() );
                    circularProgressDrawable.setStrokeWidth(5f);
                    circularProgressDrawable.setCenterRadius(30f) ;
                    circularProgressDrawable.start();
                    // chatListViewHolder.title.setText(name[0]);

                    // Log.e("ak47", "onBindViewHolder: "+contacts.get(i).get("Name") );
                    Glide.with(getApplicationContext())
                            .load(namer)
                            .placeholder(circularProgressDrawable).into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            img=resource;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                    Glide.with(getApplicationContext())
                            .load(namer)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(circularProgressDrawable).
                    into(dp);
                   // dp.setImageDrawable(img);


                    Log.d("stringwa",namer);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
        initializeMedia();

    }



    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    @SuppressLint("WrongConstant")
    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        mMedia= findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }
    private static final int FILE_SELECT_CODE = 0;

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private File createImageFile() throws IOException {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/HashContact"+"/CameraImages");
        dir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
       // File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName, /* prefix */ ".jpg", /* suffix */ dir /* directory */ );

        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE_SPEECH_INPUT)
        {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                messegeComposer.setText(result.get(0));
               send.callOnClick();
            }
        }
        if(requestCode==100)
        {
            //Log.d("makamaa", String.valueOf(outuri));

            if(sflag==1) {

                mediaUriList.add(String.valueOf(outuri));
               // Log.d("makama", String.valueOf(outuri));
                if(outuri!=null)
                sendFile();
                sflag=0;
               /* FileOutputStream outStream = null;
                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/hashcontact");
                    dir.mkdirs();
                    long t = System.currentTimeMillis();
                    String time = String.valueOf(t);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = timeStamp + "."+"jpeg";
                    String fileName = "background.jpg";
                    File outFile = new File(dir, fileName);

                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();

                    Log.d("pathsss", "onPictureTaken - wrote to " + outFile.getAbsolutePath());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }*/
            }

        }
        if(resultCode == RESULT_OK&&(requestCode==200||requestCode==FILE_SELECT_CODE)){
            if(requestCode == FILE_SELECT_CODE){
                if(data.getClipData() == null){
                    Uri selectedImage = data.getData();
                    Log.d("pather", String.valueOf(selectedImage));
                   String picturePath=getRealPathFromURI_API19(this,selectedImage);
                    Log.d("picturePath",picturePath);
                    File file = new File(picturePath);
                     filename = file.getName();
                    Log.d("pather", "File Path: " + picturePath);
                    // File file = new File(pathname);
                    //   filename = file.getName();



                    mediaUriList.add(data.getData().toString());

                    sendFile();

                }else{
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
            else{
                Uri pickedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
                FileOutputStream outStream = null;

                // Write to SD Card
                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/HashContact");
                    dir.mkdirs();
                    long t=System.currentTimeMillis();
                    String time= String.valueOf(t);

                    String fileName = "background.jpg";
                    File outFile = new File(dir, fileName);

                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();

                    Log.d("pathsss", "onPictureTaken - wrote to " + outFile.getAbsolutePath());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }


                rootView.setBackground(mDrawable);


                cursor.close();
            }
        }
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";

        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else {

                if (Build.VERSION.SDK_INT > 20) {
                    //getExternalMediaDirs() added in API 21
                    File extenal[] = context.getExternalMediaDirs();
                    if (extenal.length > 1) {
                        filePath = extenal[1].getAbsolutePath();
                        filePath = filePath.substring(0, filePath.indexOf("Android")) + split[1];
                    }
                }else{
                    filePath = "/storage/" + type + "/" + split[1];
                }
                return filePath;
            }

        } else if (isDownloadsDocument(uri)) {
            // DownloadsProvider
            final String id = DocumentsContract.getDocumentId(uri);
            //final Uri contentUri = ContentUris.withAppendedId(
            // Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    String result = cursor.getString(index);
                    cursor.close();
                    return result;
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if (DocumentsContract.isDocumentUri(context, uri)) {
            // MediaProvider
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String[] ids = wholeID.split(":");
            String id;
            String type;
            if (ids.length > 1) {
                id = ids[1];
                type = ids[0];
            } else {
                id = ids[0];
                type = ids[0];
            }

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{id};
            final String column = "_data";
            final String[] projection = {column};
            Cursor cursor = context.getContentResolver().query(contentUri,
                    projection, selection, selectionArgs, null);

            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            return filePath;
        } else {
            String[] proj = {MediaStore.Audio.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                if (cursor.moveToFirst())
                    filePath = cursor.getString(column_index);
                cursor.close();
            }


            return filePath;
        }
        return null;
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference isOnlineRef = rootRef.child(currentUser).child("isOnline");
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());


        isOnlineRef.setValue("Last seen " + currentDate + "\n" + currentTime);
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
        ref.child(currentUser).child("Messege").child(user2).child("chattingAt").setValue(String.valueOf(System.currentTimeMillis()));
        final FirebaseDatabase[] database2 = {FirebaseDatabase.getInstance()};
        DatabaseReference databaseReference2= database2[0].getReference().child(currentUser).child("ChattingWith");
        int rand_int1 = Math.abs(ThreadLocalRandom.current().nextInt()) ;
        String rand= String.valueOf(rand_int1);
        databaseReference2.setValue(rand);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference isOnlineRef = rootRef.child(currentUser).child("isOnline");
        isOnlineRef.setValue("true");
        FirebaseDatabase.getInstance().getReference().child(currentUser).child("ChattingWith").setValue(user2);


    }

    public void sendFile()
    {

        final Map newMessageMap = new HashMap<>();

        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
        if(!messegeComposer.getText().toString().trim().equals(""))
            newMessageMap.put("text", messegeComposer.getText().toString());


        if(!mediaUriList.isEmpty()){
            sentflag=1;


            for (String mediaUri : mediaUriList){
                final FirebaseDatabase[] database1 = {FirebaseDatabase.getInstance()};
                DatabaseReference databaseReference1= database1[0].getReference();
                final String mediaId = databaseReference1.child("Communication").child(currentUser).push().getKey();
                mediaIdList.add(mediaId);
                final UploadTask uploadTask;Bitmap bitmap = null;
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child(mediaId);

                try {
                    if (filename.substring(filename.lastIndexOf('.') + 1).equalsIgnoreCase("jpeg") || filename.substring(filename.lastIndexOf('.') + 1).equalsIgnoreCase("png") || filename.substring(filename.lastIndexOf('.') + 1).equalsIgnoreCase("jpg") || filename.substring(filename.lastIndexOf('.') + 1).equalsIgnoreCase("tif")) {
                        try {
                            Log.d("kamwa kiya", "dad");
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mediaUri));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("kamwa kiya", "ad");
                        }


                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        byte[] data = baos.toByteArray();
                        Log.d("kamwa kiya", "true");
                        uploadTask = filePath.putBytes(data);
                    } else {
                        Log.d("ka kiya", filename.substring(filename.lastIndexOf('.')));
                        Log.d("kam kiya", "false");
                        uploadTask = filePath.putFile(Uri.parse(mediaUri));
                    }

                final ProgressDialog  mProgress = new ProgressDialog(ChatBox.this);
                mProgress.setTitle("Sending...");


                mProgress.setCancelable(true);
                mProgress.setIcon(R.drawable.connectlogo);
                mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadTask.cancel();
                    }
                });

                //  mProgress.setInverseBackgroundForced(setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY));
                mProgress.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;
                mProgress.show();




                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                long ts = (long) System.currentTimeMillis();

                                databaseReference.child(currentUser).child("Messege").child(user2).child("chat").child(String.valueOf(ts)).setValue(uri.toString()+" "+filename);

                                databaseReference.child(currentUser).child("Messege").child(user2).child("lastMessege").setValue(String.valueOf(ts));
                                newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());

                                totalMediaUploaded++;

                                    mProgress.dismiss();
                                    if(floc==1)
                                    {
                                        messegeComposer.setText("LOCATION:-"+lastKnown.getLatitude()+"||"+lastKnown.getLongitude());
                                        send.callOnClick();
                                        floc=0;
                                    }

                                    mediaUriList.clear();
                                    final String[] notificationKey = new String[1];
                                    final FirebaseDatabase[] database1 = {FirebaseDatabase.getInstance()};
                                    DatabaseReference databaseReference1= database1[0].getReference();
                                    databaseReference1.child(user2).child("Personal").child("Notification").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                            final String[] notification = {dataSnapshot.getValue(String.class)};
                                            final FirebaseDatabase[] database2 = {FirebaseDatabase.getInstance()};
                                            DatabaseReference databaseReference2= database2[0].getReference().child(user2).child("ChattingWith");
                                            databaseReference2.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                    boolean se;
                                                    String with= String.valueOf(dataSnapshot1.getValue());

                                                    if(with.equals(currentUser))
                                                    {
                                                        notification[0] =null;
                                                       se=false;}
                                                    else{
                                                        se=true;}
                                                    DatabaseReference databaseReferencee2= FirebaseDatabase.getInstance().getReference().child("Communication").child(user2).child("Messege").child(currentUser).child("Notification");
                                                    databaseReferencee2.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            String xyz= String.valueOf(dataSnapshot.getValue());
                                                            if(xyz.equals("Yes"))
                                                                notification[0]=null;
                                                            DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
                                                            rootRef.child(currentUser).child("Personal").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    new SendNotification("sent you a file \n"+"Tap on the link to open the file",dataSnapshot.getValue(String.class), notification[0]);



                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });



                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    Toast.makeText(ChatBox.this, "sent", Toast.LENGTH_SHORT).show();




                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress =(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        mProgress.setMessage("Uploaded: "+(int)progress+"%");
                        mProgress.setProgress((int) progress);

                    }
                });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("except krro","kaka");
                }


            }
        }else{

        }
    }




}
