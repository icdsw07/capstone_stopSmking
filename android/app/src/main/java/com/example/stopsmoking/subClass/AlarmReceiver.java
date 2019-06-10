package com.example.stopsmoking.subClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmService.class);
        context.startService(serviceIntent);
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            context.startForegroundService(serviceIntent);
//        }else{
//            context.startService(serviceIntent);
//        }
    }
}
