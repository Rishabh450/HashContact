package com.rishabh.hashcontact.Support;


import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Switch;
import android.widget.Toast;


import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rishabh.hashcontact.MainActivity;
import com.rishabh.hashcontact.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;


public class LiveLocationService extends AccessibilityService {
    String providerr,currentUser;
    DatabaseReference myref;
    boolean isServiceRunning=false;

    LocationManager locationManager;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }


    void startServiceWithNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("app_channel", "HashContact Service", NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(null, null);
            NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(LiveLocationService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(LiveLocationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(LiveLocationService.this)
                .setContentTitle("Service")
                .setContentText("Connected To Server")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId("app_channel");
        notificationBuilder.build().flags = notificationBuilder.build().flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(12, notificationBuilder.build());
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("taskremoved","ontaskremoved");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);


       /* Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 2000, restartServicePI);*/

    }


    @Override
    public void onDestroy() {

        // stoptimertask();
        Log.d("taskremoved","onDestroy");

        isServiceRunning = false;

        Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        super.onDestroy();

    }


    @Override
    public void onCreate() {
        // This will be called when your Service is created for the first time
        // Just do any operations you need in this method.
        Log.d("taskremoved","oncreate");
        startServiceWithNotification();
        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                providerr="facebook";
                currentUser= Profile.getCurrentProfile().getId();
            }
            else {
                providerr = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser=userInfo.getUid();
            }
        }
        myref= FirebaseDatabase.getInstance().getReference();



        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    String currentDateAndTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());


                    myref.child(currentUser).child("isConnected").setValue(currentDateAndTime);
                    //do your code here
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
                finally{
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 500);
                }
            }
        };

//runnable must be execute once
        handler.post(runnable);





    }

    public Notification getNotification()
    {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("app_channel", "Demo Notification", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(LiveLocationService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(LiveLocationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(LiveLocationService.this)
                .setContentTitle("Service Running")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId("app_channel");
        return notificationBuilder.build();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            startServiceWithNotification();
        Log.d("taskremoved","onStartCommand");


        return START_STICKY;


    }




}