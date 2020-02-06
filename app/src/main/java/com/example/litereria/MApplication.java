package com.example.litereria;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        ConnectivityBroadcastReceiver connectivityBroadcastReceiver= new ConnectivityBroadcastReceiver();
        connectivityBroadcastReceiver.onReceive(this,new Intent(this,ConnectivityBroadcastReceiver.class));

    }

}