package com.rishabh.hashcontact;
import android.app.Application;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        OneSignal.startInit(this)


                .unsubscribeWhenNotificationsAreDisabled(false)
                .init();


        OneSignal.setSubscription(true);
        /*ConnectivityBroadcastReceiver connectivityBroadcastReceiver= new ConnectivityBroadcastReceiver();
        connectivityBroadcastReceiver.onReceive(this,new Intent(this,ConnectivityBroadcastReceiver.class));
*/

    }

}