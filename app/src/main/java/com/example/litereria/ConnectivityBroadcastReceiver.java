package com.example.litereria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("ghusa","jja");
        if (true) {
            Log.d("ghusa","jj");
            //check internet connection
            if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                Log.d("ghusa","ja");
                if (context != null) {
                    boolean show = false;
                    if (ConnectionHelper.lastNoConnectionTs == -1) {//first time
                        show = true;
                       // ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                    } else {
                        if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                            show = true;
                            ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                        }
                    }

                    if (show && ConnectionHelper.isOnline) {
                        ConnectionHelper.isOnline = false;
                       // context.stopService(new Intent(context, SyncData.class));

                        FirebaseDatabase.getInstance().getReference().child("time").setValue("Lost");
                    }
                }
            } else {
                //Toast.makeText(this,R.string.project_id,1).show();
                Log.e("netstate","Connected.");
                // Perform your actions here
               // FirebaseDatabase.getInstance().getReference().child("time").setValue("connected");

                ConnectionHelper.isOnline = true;
            }
        }
    }

}