package com.example.litereria.Support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.ZoomListener;
import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.litereria.Models.Seen;
import com.example.litereria.R;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SeeStatus extends AppCompatActivity {
    ImageView userpic,userpi,content,see;
    TextView usernm,ago,text;
    EditText reply;
    TextView send;
    CountDownTimer countDownTimer;
    TextView seennumber;
    LinearLayout whoseen;
String provider,currentUser;
    String textr,url,id,statusid;
    ProgressBar progressBar;
    LinearLayout  seestatus;
    long timeleft;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS ;


    public void Tpause()
    {
        countDownTimer.cancel();

    }
    public void Tresume(long timelef)
    {
        countDownTimer=new CountDownTimer(timelef,50) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeleft=millisUntilFinished;


                progressBar.setProgress((int) (100-millisUntilFinished/50));

            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_status);
        progressBar=findViewById(R.id.progress);
        seestatus=findViewById(R.id.seeroot);
        countDownTimer=new CountDownTimer(10000,50) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeleft=millisUntilFinished;


                progressBar.setProgress((int) (100-millisUntilFinished/50));

            }

            @Override
            public void onFinish() {
                finish();

            }
        }.start();
        LinearLayout linearLayo=findViewById(R.id.stopper);


        linearLayo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        Tpause();
                        Log.d("presshuastop","stop");
                        return true;

                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        Log.d("presshuastop","start");
                        Tresume(timeleft);
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                provider="facebook";
                currentUser= Profile.getCurrentProfile().getId();
            }
            else {
                provider = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser=userInfo.getUid();
            }
        }

        init();







        Intent intent=getIntent();
        textr=intent.getStringExtra("text");
        url=intent.getStringExtra("url");
        id=intent.getStringExtra("id");
        statusid=intent.getStringExtra("statusid");
        final ArrayList<Seen>  seen=intent.getParcelableArrayListExtra("seenlist");
        seennumber=findViewById(R.id.kitnaseen);
        if(!currentUser.equals(id))
            whoseen.setVisibility(View.GONE);


            String l= String.valueOf(seen.size());
        Log.d("listwa", seen.get(0).getTime()+" "+seen.get(0).getUserid()+" "+l);

        seennumber.setText(l);
        whoseen=findViewById(R.id.whoseen);
        see=findViewById(R.id.see);
        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("heybabaji","tat");
                Intent intent1 =new Intent(SeeStatus.this,SeenBy.class);
                intent1.putParcelableArrayListExtra("seenList",seen);
                startActivity(intent1);
            }
        });
        seennumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("heybabaji","tat");
                Intent intent1 =new Intent(SeeStatus.this,SeenBy.class);
                intent1.putParcelableArrayListExtra("seenList",seen);
                startActivity(intent1);
            }
        });


        Log.d("fethchedma",text+" "+url+" "+id);
        final Thread thread = new Thread() {
            @Override
            public void run() {

                while (true) {
                    if (reply.getText().toString().equals("")) {
                      //  Tresume(timeleft);

                        Log.d("uithreadwaala", "fade");
                        send.setTextColor(getResources().getColor(R.color.fadeblue));
                    } else {

                        Tpause();
                        send.setTextColor(getResources().getColor(R.color.blue));
                        Log.d("uithreadwaala", "bright");
                    }

                }

            }
        };

        thread.start();

        setData();

        Zoomy.Builder builder = new Zoomy.Builder(this).zoomListener(new ZoomListener() {
            @Override
            public void onViewStartedZooming(View view) {
                Tpause();

            }

            @Override
            public void onViewEndedZooming(View view) {
                Tresume(timeleft);


            }
        }).target(content);
        builder.register();

        whoseen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(SeeStatus.this,SeenBy.class);
              //  intent.putParcelableArrayListExtra()
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!reply.getText().toString().equals(""))
                {
                    String ts= String.valueOf(System.currentTimeMillis());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    try {

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                        Date dt = sdf.parse(currentTime);

                        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                        currentTime = sdfs.format(dt);



                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String msg="reply on status: "+reply.getText().toString()+"\n"+currentTime;

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Communication").child(currentUser).child("Messege").child(id).child("chat").child(ts);
                    reference.setValue(msg);
                    Toast.makeText(SeeStatus.this,"Reply sent",Toast.LENGTH_SHORT).show();
                    reply.setText("");

                }
            }
        });

    }
    public void setData()
    {
        // seennumber.setText(seen.size());

        ago.setText(getTimeAgo(Long.parseLong(statusid)));
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child(currentUser).child("Personal").child("Photo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url=dataSnapshot.getValue(String.class);
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(SeeStatus.this);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(10f) ;
                circularProgressDrawable.start();
                Glide.with(SeeStatus.this)
                        .load(url)
                        .placeholder(circularProgressDrawable)
                        .apply(RequestOptions.circleCropTransform())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                return false;
                            }
                        })
                        .into(userpi)
                ;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
        reference.child(id).child("Personal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              String name=dataSnapshot.child("Name").getValue(String.class);
              usernm.setText(name);
              String url=dataSnapshot.child("Photo").getValue(String.class);
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(SeeStatus.this);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(40f) ;
                circularProgressDrawable.start();
                Glide.with(SeeStatus.this)
                        .load(url)
                        .placeholder(circularProgressDrawable)
                        .apply(RequestOptions.circleCropTransform())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                return false;
                            }
                        })
                        .into(userpic)
                ;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        text.setText(textr);
        CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(40f) ;
        circularProgressDrawable.start();
        Glide.with(this)
                .load(url)
                .placeholder(circularProgressDrawable)

                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(content);
    }
    public void getData()
    {
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        ;


        long diff = now - time;
        if (diff > 0) {


            if (diff < MINUTE_MILLIS) {
                return (diff / SECOND_MILLIS) + "s";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "1m";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "1h";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + "d";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "1w";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + "w";
            } else {
                java.util.Date date = new java.util.Date((long) time);
                return date.toString();
            }

        } else {

            diff = time - now;
            if (diff < MINUTE_MILLIS) {
                return "this minute";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute later";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes later";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour later";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours later";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "tomorrow";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + " days later";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "a week later";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + " weeks later";
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));

                return dateString;
            }
        }
    }
        public void init()
    {
        reply=findViewById(R.id.reply);
        send=findViewById(R.id.sendreply);
        whoseen=findViewById(R.id.whoseen);

        usernm=findViewById(R.id.usernm);
        userpic=findViewById(R.id.user_pic);
        userpi=findViewById(R.id.us_pic);
        content=findViewById(R.id.contentimag);
        ago=findViewById(R.id.ago);
        text=findViewById(R.id.status_cont);
    }
}
