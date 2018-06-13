package com.example.kunj.reminder;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.kunj.reminder.data.ReminderContract;

public class CreateActivity extends AppCompatActivity {

    private Uri currentRemUri;
    private boolean remHasChanged = false;

    private EditText description;
    private DatePicker dPicker;

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
        setContentView(R.layout.activity_create);

        Intent intent = getIntent();
        currentRemUri = intent.getData();

        dPicker = findViewById(R.id.create_picker);
        dPicker.setOnTouchListener(touchListener);

        description = findViewById(R.id.enter_description);
        description.setOnTouchListener(touchListener);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveRem();
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void saveRem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        description = findViewById(R.id.enter_description);
        dPicker = findViewById(R.id.create_picker);
        String desc = description.getText().toString().trim();
        String date = Integer.toString(dPicker.getDayOfMonth());

        int month = dPicker.getMonth();
        String monthString="";

        switch (month){
            case 0:monthString = "Jan"; break;
            case 1:monthString = "Feb"; break;
            case 2:monthString = "Mar"; break;
            case 3:monthString = "Apr"; break;
            case 4:monthString = "May"; break;
            case 5:monthString = "Jun"; break;
            case 6:monthString = "Jul"; break;
            case 7:monthString = "Aug"; break;
            case 8:monthString = "Sep"; break;
            case 9:monthString = "Oct"; break;
            case 10:monthString = "Nov"; break;
            case 11:monthString = "Dec"; break;
        }

        String year = Integer.toString(dPicker.getYear());

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (currentRemUri == null && TextUtils.isEmpty(desc) && TextUtils.isEmpty(monthString) && TextUtils.isEmpty(date) && TextUtils.isEmpty(year)) {
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ReminderContract.ReminderEntry.COLUMN_DESC, desc);
        values.put(ReminderContract.ReminderEntry.COLUMN_DATE, date);
        values.put(ReminderContract.ReminderEntry.COLUMN_MONTH, monthString);
        values.put(ReminderContract.ReminderEntry.COLUMN_YEAR, year);


        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (currentRemUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ReminderContract.ReminderEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!remHasChanged)
        {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners

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

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}

