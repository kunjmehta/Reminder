package com.example.kunj.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Kunj on 6/22/2018.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.i(MyBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, MyService.class));
    }
}

