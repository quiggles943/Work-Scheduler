package com.quigglesproductions.paulq.calendartest;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.widget.SwipeRefreshLayout;

import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class MainActivity extends AppActivity{

    Context context;
    View parentView;
    //ArrayList<String> calendars;
    ArrayList<Date> dates;
    ArrayList<Event> events;
    TreeMap<Date,ArrayList<Event>> dataSet;
    ExpandableEventAdapter eveAdpt;
    ExpandableListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = findViewById(android.R.id.content);
        dates = new ArrayList<Date>();
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.activity_main);
        getLayoutInflater().inflate(R.layout.content_main, layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_main);
        context = this;
        //calendars = getCalendars();
        getDataFromCalendarTable();
        listView = (ExpandableListView) findViewById(R.id.list);
        events = new ArrayList<>();
        dataSet = new TreeMap<Date,ArrayList<Event>>();
        //events.add(new Event(16,"Work","",1571048836,1571048847));
        dataSet = getDataFromEventTable();
        eveAdpt = new ExpandableEventAdapter(context,dates, dataSet);
        listView.setAdapter(eveAdpt);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(context,AddEvent.class);
                startActivityForResult(intent,1);

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateListView();
                Log.i("refresh", "Layout Refreshed");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                String returnString = data.getStringExtra("string");

                // Set text view with string
                getSnackbar(parentView, returnString);
            }

        }
    }

    public void getSnackbar(View view, String text)
    {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void updateListView()
    {
        dataSet = getDataFromEventTable();
        eveAdpt.update(dates,dataSet);
        eveAdpt.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    /*public ArrayList<String> getCalendars(){
        ArrayList<String> content = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR},0);
        }
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.Calendars.OWNER_ACCOUNT};
        Cursor calCursor =
                getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                CalendarContract.Calendars.VISIBLE + " = 1",
                                null,
                                CalendarContract.Calendars._ID + " ASC");
        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(1);
                String AccountName = calCursor.getString(2);
                String AccountType = calCursor.getString(3);
                String OwnerAccount = calCursor.getString(4);
                content.add(displayName);
                // ...
            } while (calCursor.moveToNext());
        }
        return content;
    }*/
    public void getDataFromCalendarTable() {
        Cursor cur = null;
        ContentResolver cr = getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                        CalendarContract.Calendars._ID
                };
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(context);
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{mSharedPreference.getString("account_name",""), mSharedPreference.getString("account_type",""),
                mSharedPreference.getString("owner_account","")};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR},0);
        }
        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
            String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
            String ID = cur.getString(cur.getColumnIndex(CalendarContract.Calendars._ID));


        }

    }

    public TreeMap<Date,ArrayList<Event>> getDataFromEventTable() {
        ArrayList<Event> content = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 0);
        }
        Cursor cur = null;
        ContentResolver cr = getContentResolver();

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.CALENDAR_ID,
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = CalendarContract.Events.CALENDAR_ID + " = ? ";
        String[] selectionArgs = new String[]{"16"};

        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            if (Integer.parseInt(cur.getString(cur.getColumnIndex(CalendarContract.Events.CALENDAR_ID))) == 16) {
            try {
                int id = Integer.parseInt(cur.getString(cur.getColumnIndex(CalendarContract.Events.CALENDAR_ID)));
                String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
                String location = cur.getString(cur.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                long dtstart = Long.parseLong(cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART)));
                long dtend = Long.parseLong(cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND)));
                //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                //String dateString = dateFormat.format(new Date(dtstart));
                Date testDate = new Date(dtstart);
                Calendar cal = Calendar.getInstance();
                cal.setTime(testDate);
                cal.set(Calendar.HOUR_OF_DAY,0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                Date inputDate = cal.getTime();
                Event event = new Event(id, title, location, dtstart, dtend);

                //String date = dateFormat.format(event.getStartDate());
                if(dataSet.get(inputDate)== null)
                {
                    ArrayList<Event> events = new ArrayList<>();
                    events.add(event);
                    dataSet.put(inputDate,events);
                    dates.add(inputDate);
                }
                else
                {
                    ArrayList<Event> events = dataSet.get(inputDate);
                    boolean unique = true;
                    for(Event e : events)
                    {
                        if(e.getUid() == event.getUid())
                        {
                            unique = false;
                        }
                    }
                    if(unique) {
                        events.add(event);
                        dataSet.remove(inputDate);
                        dataSet.put(inputDate, events);
                    }
                }
            }
            catch(Exception e)
            {
                Log.e("Error", e.getMessage());
                Log.e("start time",cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART)));
                Log.e("end time",cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND)));
            }
            }

        }
        return dataSet;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(context, Settings.class);
                startActivityForResult(intent,0);
                return true;
            case R.id.menu_refresh:
                updateListView();
                return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}
