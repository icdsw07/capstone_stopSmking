package com.example.stopsmoking.subClass;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class AlarmService extends Service {

    private Vibrator vibrator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));

        Toast.makeText(getApplicationContext(), "알람", Toast.LENGTH_LONG).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}