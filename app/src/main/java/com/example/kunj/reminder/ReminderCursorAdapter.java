package com.example.kunj.reminder;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.kunj.reminder.data.ReminderContract;

/**
 * Created by Kunj on 6/10/2018.
 */

public class ReminderCursorAdapter extends CursorAdapter {

    public ReminderCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.reminder_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView descriptionTextView = (TextView) view.findViewById(R.id.reminder_description);
        TextView dateTextView = (TextView) view.findViewById(R.id.reminder_date);


        int descColumnIndex = cursor.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_DESC);
        int dateColumnIndex = cursor.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_DATE);
        int monthColumnIndex = cursor.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_MONTH);
        int yearColumnIndex = cursor.getColumnIndex(ReminderContract.ReminderEntry.COLUMN_YEAR);

        String description = cursor.getString(descColumnIndex);
        String date = Integer.toString(cursor.getInt(dateColumnIndex));
        String month = cursor.getString(monthColumnIndex);
        String year = Integer.toString(cursor.getInt(yearColumnIndex));

        String timeOfReminding = month + " " + date + "," + year;

        descriptionTextView.setText(description);
        dateTextView.setText(timeOfReminding);
    }
}

