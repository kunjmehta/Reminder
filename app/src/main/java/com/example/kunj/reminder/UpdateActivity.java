package com.example.kunj.reminder;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kunj.reminder.data.ReminderContract;

public class UpdateActivity extends AppCompatActivity{

    private boolean remHasChanged = false;
    private DatePicker dPicker;
    private Uri currentRemUri;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            remHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent intent = getIntent();
        currentRemUri = intent.getData();

        DatePicker dPicker = findViewById(R.id.update_picker);
        dPicker.setOnTouchListener(touchListener);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveRem();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!remHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.yes, discardButtonClickListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveRem() {

        dPicker = findViewById(R.id.update_picker);
        String date = Integer.toString(dPicker.getDayOfMonth());

        int month = dPicker.getMonth();
        String monthString = "";

        switch (month) {
            case 0:
                monthString = "Jan";
                break;
            case 1:
                monthString = "Feb";
                break;
            case 2:
                monthString = "Mar";
                break;
            case 3:
                monthString = "Apr";
                break;
            case 4:
                monthString = "May";
                break;
            case 5:
                monthString = "Jun";
                break;
            case 6:
                monthString = "Jul";
                break;
            case 7:
                monthString = "Aug";
                break;
            case 8:
                monthString = "Sep";
                break;
            case 9:
                monthString = "Oct";
                break;
            case 10:
                monthString = "Nov";
                break;
            case 11:
                monthString = "Dec";
                break;
        }

        String year = Integer.toString(dPicker.getYear());

        ContentValues values = new ContentValues();
        values.put(ReminderContract.ReminderEntry.COLUMN_DATE, date);
        values.put(ReminderContract.ReminderEntry.COLUMN_MONTH, monthString);
        values.put(ReminderContract.ReminderEntry.COLUMN_YEAR, year);


        if (currentRemUri != null) {
            int rowsAffected = getContentResolver().update(currentRemUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_rem_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_rem_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}


