package com.example.kunj.reminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Kunj on 6/10/2018.
 */

public class ReminderProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private boolean isDatabasePresent = false;
    String DB_FULL_PATH;


     //URI matcher code for the content URI for the reminder table
    private static final int REM = 100;


    //URI matcher code for the content URI for a single reminder
    private static final int REM_ID = 101;

    static {
        sUriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY, ReminderContract.PATH_REMINDER, REM);
        sUriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY, ReminderContract.PATH_REMINDER + "/#", REM_ID);
    }

    private ReminderDbHelper databaseHelper;

    @Override
    public boolean onCreate() {
        databaseHelper = new ReminderDbHelper(getContext());
        return true;
    }

    /*public Cursor queryAll(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        checkDataBase();
        if (isDatabasePresent) {

            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            Cursor cursor;
            int match = sUriMatcher.match(uri);
            switch (match) {
                case REM:
                    cursor = database.query(ReminderContract.ReminderEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
            }
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;
        }
        return null;
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        Context ctx = getContext();
        File dbpath = ctx.getDatabasePath("reminder.db");
        try {
            checkDB = SQLiteDatabase.openDatabase("reminder.db", null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
        }
        return true;
    }*/


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database;
            database = databaseHelper.getReadableDatabase();
            Cursor cursor;

            int match = sUriMatcher.match(uri);
            switch (match) {
                case REM:
                    cursor = database.query(ReminderContract.ReminderEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;

                    case REM_ID:
                    selection = ReminderContract.ReminderEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                        cursor = database.query(ReminderContract.ReminderEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
            }

            // Set notification URI on the Cursor so we know what content URI the Cursor was created for. If the data at this URI changes, then we know we need to update the Cursor.
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REM:
                return insertRem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REM:
                return ReminderContract.ReminderEntry.CONTENT_LIST_TYPE;
            case REM_ID:
                return ReminderContract.ReminderEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REM:
                rowsDeleted = database.delete(ReminderContract.ReminderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REM_ID:
                selection = ReminderContract.ReminderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ReminderContract.ReminderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REM:
                return updateRem(uri, contentValues, selection, selectionArgs);
            case REM_ID:
                selection = ReminderContract.ReminderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateRem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertRem(Uri uri, ContentValues values) {

        String desc = values.getAsString(ReminderContract.ReminderEntry.COLUMN_DESC);
        if (desc == null) {
            throw new IllegalArgumentException("Reminder requires a description");
        }

        String date = values.getAsString(ReminderContract.ReminderEntry.COLUMN_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Reminder requires a date");
        }

        String month = values.getAsString(ReminderContract.ReminderEntry.COLUMN_MONTH);
        if (month == null) {
            throw new IllegalArgumentException("Reminder requires a month");
        }

        String year = values.getAsString(ReminderContract.ReminderEntry.COLUMN_YEAR);
        if (year == null) {
            throw new IllegalArgumentException("Reminder requires a year");
        }


        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        long id = database.insert(ReminderContract.ReminderEntry.TABLE_NAME, null, values);

        // Notify all listeners that the data has changed for the reminder content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateRem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ReminderContract.ReminderEntry.COLUMN_DESC)) {
            String desc = values.getAsString(ReminderContract.ReminderEntry.COLUMN_DESC);
            if (desc == null) {
                throw new IllegalArgumentException("Reminder needs a description");
            }
        }

        if (values.containsKey(ReminderContract.ReminderEntry.COLUMN_DATE)) {
            String date = values.getAsString(ReminderContract.ReminderEntry.COLUMN_DATE);
            if (date == null) {
                throw new IllegalArgumentException("Reminder needs a date");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int rowsUpdated = database.update(ReminderContract.ReminderEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
