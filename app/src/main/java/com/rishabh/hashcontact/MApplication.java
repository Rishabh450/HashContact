package com.rishabh.hashcontact;
import android.app.Application;
import android.content.Intent;

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