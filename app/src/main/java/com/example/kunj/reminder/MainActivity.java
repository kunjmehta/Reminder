package com.example.kunj.reminder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunj.reminder.data.ReminderContract;
import com.example.kunj.reminder.data.ReminderDbHelper;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.Calendar;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ReminderCursorAdapter cAdapter;
    private static final int REM_LOADER = 0;
    Uri currentRemUri;
    SwipeActionAdapter swipeAdapter;

    Intent serviceIntent;
    //Intent mServiceIntent;
    //private MyService mMyService;
    int SERVICE_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar now = Calendar.getInstance();
        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY,8);
        alarmStartTime.set(Calendar.MINUTE,0);
        alarmStartTime.set(Calendar.SECOND,0);
        alarmStartTime.set(Calendar.MILLISECOND,0);


        if (now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1);
        }

        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        serviceIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
        PendingIntent servicePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), MyService.SERVICE_ID,serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY, servicePendingIntent);

        //creating service
        /*&mMyService = new MyService(getApplicationContext());
        mServiceIntent = new Intent(getApplicationContext(), mMyService.getClass());
        if (!isMyServiceRunning(mMyService.getClass())) {
            startService(mServiceIntent);
        }*/

        final ListView reminderListView = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        reminderListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of reminder  in the Cursor.
        cAdapter = new ReminderCursorAdapter(this, null);

        // Wrap your content in a SwipeActionAdapter
        //swipeAdapter = new SwipeActionAdapter(cAdapter);

        // Pass a reference of your ListView to the SwipeActionAdapter
       // swipeAdapter.setListView(reminderListView);

        // Set the SwipeActionAdapter as the Adapter for your ListView
        //reminderListView.setAdapter(swipeAdapter);
        reminderListView.setAdapter(cAdapter);

        // Set backgrounds for the swipe directions
//        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.swipe_left)
//                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.swipe_left)
//                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.swipe_right)
//                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.swipe_right);

//        // Listen to swipes
//        swipeAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
//            @Override
//            public boolean hasActions(int position, SwipeDirection direction){
//                if(direction.isLeft()) return true; // Change this to false to disable left swipes
//                if(direction.isRight()) return true;
//                return false;
//            }
//
//            @Override
//            public boolean shouldDismiss(int position, SwipeDirection direction){
//                // Only dismiss an item when swiping normal left
//                return direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
//            }
//
//            @Override
//            public void onSwipe(int[] positionList, SwipeDirection[] directionList){
//                for(int i=0;i<positionList.length;i++) {
//                    SwipeDirection direction = directionList[i];
//                    int position = positionList[i];
//                    String dir = "";
//
//                    switch (direction) {
//                        case DIRECTION_FAR_LEFT:
//                            dir = "Far left";
//                            break;
//                        case DIRECTION_NORMAL_LEFT:
//                            dir = "Left";
//                            View view = (View)reminderListView.getItemAtPosition(position);
//                            //swipeAdapter.getView(position,null, TextView).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case DIRECTION_FAR_RIGHT:
//                            dir = "Far right";
//                            break;
//                        case DIRECTION_NORMAL_RIGHT:
//                            dir = "Right";
//                            break;
//                    }
//                    /*Toast.makeText(
//                            this,
//                            dir + " swipe Action triggered on " + swipeAdapter.getItem(position),
//                            Toast.LENGTH_SHORT
//                    ).show();*/
//                    swipeAdapter.notifyDataSetChanged();
//                }
//            }
//        });

        // Setup the item click listener
        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Go to UpdateActivity
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                currentRemUri = ContentUris.withAppendedId(ReminderContract.ReminderEntry.CONTENT_URI, id);
                intent.setData(currentRemUri);
                String selected = ((TextView)view.findViewById(R.id.reminder_description)).getText().toString();
                intent.putExtra("Description",selected);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(REM_LOADER, null, this);

        //delete reminder
        reminderListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentRemUri = ContentUris.withAppendedId(ReminderContract.ReminderEntry.CONTENT_URI, id);
                showDeleteConfirmationDialog();
                return true;
            }
        });

        /*reminderListView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });*/
    }


    private void showDeleteConfirmationDialog() {

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

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteRem() {
        if (currentRemUri != null) {
            int rowsDeleted = getContentResolver().delete(currentRemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_rem_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_rem_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //main menu
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Go to Creation
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, CreateActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ReminderContract.ReminderEntry._ID,
                ReminderContract.ReminderEntry.COLUMN_DESC,
                ReminderContract.ReminderEntry.COLUMN_DATE,
                ReminderContract.ReminderEntry.COLUMN_MONTH,
                ReminderContract.ReminderEntry.COLUMN_YEAR};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                ReminderContract.ReminderEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cAdapter.swapCursor(null);
    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }

    /*private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }*/
}