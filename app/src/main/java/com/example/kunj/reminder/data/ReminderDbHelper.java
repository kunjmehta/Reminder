package com.example.kunj.reminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Kunj on 6/10/2018.
 */

public class ReminderDbHelper  extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "reminder.db";

    private static String DB_PATH = "";
    private SQLiteDatabase database;
    private Context mcontext = null;

    private static final int DATABASE_VERSION = 1;

    String SQL_CREATE_REMINDER_TABLE;

    public ReminderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        SQL_CREATE_REMINDER_TABLE =  "CREATE TABLE " + ReminderContract.ReminderEntry.TABLE_NAME + " ("
                + ReminderContract.ReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReminderContract.ReminderEntry.COLUMN_DESC + " TEXT NOT NULL, "
                + ReminderContract.ReminderEntry.COLUMN_DATE + " NUMBER NOT NULL, "
                + ReminderContract.ReminderEntry.COLUMN_MONTH + " TEXT NOT NULL, "
                + ReminderContract.ReminderEntry.COLUMN_YEAR + " NUMBER NOT NULL);";

        db.execSQL(SQL_CREATE_REMINDER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
