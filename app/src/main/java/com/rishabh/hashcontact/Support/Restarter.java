package com.rishabh.hashcontact.Support;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("taskremoved","broadcast");

        // Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
        //context.startService(new Intent(context, MyService.class));
        // PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_NO_CREATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            context.startForegroundService(new Intent(context,LiveLocationService.class));
        } else {
            context.startService(new Intent(context, LiveLocationService.class));
        }
    }
}