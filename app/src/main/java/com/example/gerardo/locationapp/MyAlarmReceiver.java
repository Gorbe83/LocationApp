package com.example.gerardo.locationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Gerardo on 09/03/2016.
 */
public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.example.gerardo.locationapp";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        //La alarma, cada vez que se active, iniciará un nuevo IntentService de MyLocationService
        Intent i = new Intent(context, MyLocationService.class);
        context.startService(i);
    }
}
