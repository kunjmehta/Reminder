package com.example.kunj.reminder;

import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kunj.reminder.data.ReminderContract;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ReminderCursorAdapter cAdapter;
    private static final int REM_LOADER = 0;
    Uri currentRemUri;

    private static int year;
    private static int month;
    private static int date;
    private static String monthString;

    private static final int REMINDER_NOTIFICATION_ID = 1138;
    private static final int REMINDER_PENDING_INTENT_ID = 3417;
    private static final String REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";

    public static final String[] NOTIFICATION_PROJECTION = {
            ReminderContract.ReminderEntry.COLUMN_DESC,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView reminderListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        reminderListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.

        cAdapter = new ReminderCursorAdapter(this, null);
        reminderListView.setAdapter(cAdapter);

        // Setup the item click listener
        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Create new intent to go to {@link EditorActivity}

                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                currentRemUri = ContentUris.withAppendedId(ReminderContract.ReminderEntry.CONTENT_URI, id);
                intent.setData(currentRemUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(REM_LOADER, null, this);

        reminderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentRemUri = ContentUris.withAppendedId(ReminderContract.ReminderEntry.CONTENT_URI, id);
                showDeleteConfirmationDialog();
                return true;
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteRem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    private void deleteRem() {
        // Only perform the delete if this is an existing pet.
        if (currentRemUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(currentRemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, CreateActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ReminderContract.ReminderEntry._ID,
                ReminderContract.ReminderEntry.COLUMN_DESC,
                ReminderContract.ReminderEntry.COLUMN_DATE,
                ReminderContract.ReminderEntry.COLUMN_MONTH,
                ReminderContract.ReminderEntry.COLUMN_YEAR};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ReminderContract.ReminderEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cAdapter.swapCursor(null);
    }
}
