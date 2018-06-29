package com.example.kunj.reminder;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.kunj.reminder.data.ReminderContract;
import com.example.kunj.reminder.data.ReminderDbHelper;
import com.example.kunj.reminder.data.ReminderProvider;

import java.util.Calendar;

import androidx.work.Worker;

/**
 * Created by Kunj on 6/21/2018.
 */

public class DatabaseWorker extends Worker {

    @Override
    public Worker.Result doWork() {

        int counter = MyService.count;

        if (counter == 0) {
            NotificationUtils.giveNotification(getApplicationContext(), "All caught up!");
            Log.d("DatabaseWorker", "doWork:No reminder notification ");
        } else {
                NotificationUtils.giveNotification(getApplicationContext(),"Tasks incomplete" );
                Log.d("DatabaseWorker", "doWork:Reminders notification ");
            }
        //NotificationUtils.giveNotification(getApplicationContext(),"fzf");
        return Result.SUCCESS;
    }
}
