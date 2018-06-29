package com.example.kunj.reminder;

import android.app.Application;
import android.content.Context;

/**
 * Created by Kunj on 6/26/2018.
 */

public class ReminderApplication extends Application{

        private static Context context;

        public void onCreate() {
            super.onCreate();
            ReminderApplication.context = getApplicationContext();
        }

        public static Context getAppContext() {
            return ReminderApplication.context;
        }
    }

