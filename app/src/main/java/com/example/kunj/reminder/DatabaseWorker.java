package com.example.kunj.reminder;

import android.util.Log;

import androidx.work.Worker;

/**
 * Created by Kunj on 6/21/2018.
 */

public class DatabaseWorker extends Worker {
    @Override
    public Worker.Result doWork() {

        NotificationUtils.giveNotification(getApplicationContext());
        Log.d("Heya", "doWork:Hi ");
        return Result.SUCCESS;
    }
}
