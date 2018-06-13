package com.example.kunj.reminder.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kunj on 6/10/2018.
 */

public final class ReminderContract {

    private ReminderContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.kunj.reminder";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_REMINDER = "Reminder";

    public static final class ReminderEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_REMINDER);

       /*
         The MIME type of the {@link #CONTENT_URI} for a list of pets.*/

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;

        /*
         * The MIME type of the {@link #CONTENT_URI} for a single pet.*/

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;

        public static final String TABLE_NAME = "Reminder";

        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_DESC = "Description";
        public static final String COLUMN_DATE = "Date";
        public static final String COLUMN_MONTH= "Month";
        public static final String COLUMN_YEAR= "Year";
    }
}