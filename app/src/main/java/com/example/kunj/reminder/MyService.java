package com.example.kunj.reminder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kunj.reminder.data.ReminderContract;
import com.example.kunj.reminder.data.ReminderDbHelper;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by Kunj on 6/22/2018.
 */

//TODO swipe for task completed
//TODO preferences about alarms

public class MyService extends Service {

    //public static int count = 0;
    //public static String descNotif[] = new String[50];
    public static int SERVICE_ID = 100;



    public MyService(Context applicationContext) {
        super();
        Log.i("MyService Constructor", "here I am!");
    }

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        int count=0;
        String description;
        Cursor cursor;
        ReminderDbHelper dbHelper = new ReminderDbHelper(getApplicationContext());
        int date, year;
        String month;
        int monthint = 0;

        //try {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        cursor = database.rawQuery("SELECT " + ReminderContract.ReminderEntry.COLUMN_DESC + "," +
                ReminderContract.ReminderEntry.COLUMN_DATE + "," +
                ReminderContract.ReminderEntry.COLUMN_MONTH + "," +
                ReminderContract.ReminderEntry.COLUMN_YEAR + " FROM " + ReminderContract.ReminderEntry.TABLE_NAME, null);
        //}catch (Exception e){Log.d("Heya","Null database");}

        while (cursor.moveToNext()) {
            int index;

            index = cursor.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_DESC);
            description = cursor.getString(index);

            index = cursor.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_DATE);
            date = cursor.getInt(index);

            index = cursor.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_MONTH);
            month = cursor.getString(index);

            index = cursor.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_YEAR);
            year = cursor.getInt(index);

            Calendar cal = Calendar.getInstance();
            switch (month) {
                case "Jan":
                    monthint = 0;
                    break;
                case "Feb":
                    monthint = 1;
                    break;
                case "Mar":
                    monthint = 2;
                    break;
                case "Apr":
                    monthint =3;
                    break;
                case "May":
                    monthint = 4;
                    break;
                case "Jun":
                    monthint = 5;
                    break;
                case "Jul":
                    monthint = 6;
                    break;
                case "Aug":
                    monthint = 7;
                    break;
                case "Sep":
                    monthint = 8;
                    break;
                case "Oct":
                    monthint = 9;
                    break;
                case "Nov":
                    monthint = 10;
                    break;
                case "Dec":
                    monthint = 11;
                    break;
            }
            if (cal.get(Calendar.DATE) == date && cal.get(Calendar.MONTH) == monthint && cal.get(Calendar.YEAR) == year) {

                NotificationUtils.giveNotification(getApplicationContext(), description);
                //Log.d("MyService start", "OnStartCommand:description ");
                //descNotif[count] = description;
                count++;
                continue;
            }
        }
        if(count == 0) {
            NotificationUtils.giveNotification(getApplicationContext(), "All caught up!");
            Log.d("MyService start", "OnStartCommand:All caught up");//catch(Exception e){Log.d("Heya","Null cursor");}
        }
        else{
            NotificationUtils.giveNotification(getApplicationContext(), "There are "+ count+ " incomplete tasks for today");
        }


        /*PeriodicWorkRequest.Builder ReminderBuilder =
                    new PeriodicWorkRequest.Builder(DatabaseWorker.class, 1,
                            TimeUnit.MINUTES);
            PeriodicWorkRequest ReminderWork = ReminderBuilder.build();
            WorkManager.getInstance().enqueue(ReminderWork);
            // NotificationUtils.giveNotification(getApplicationContext());*/
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("sgsgag");
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}