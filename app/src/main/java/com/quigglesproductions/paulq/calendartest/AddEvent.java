package com.quigglesproductions.paulq.calendartest;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddEvent extends AppCompatActivity {
    Context context;
    Date startDate,endDate;
    TextView tv_start, tv_end;
    EditText title;
    Button cancel_btn, add_btn;
    Spinner spinner;
    ArrayList<Location> locations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());
        c.set(Calendar.MINUTE, 0);
        context = this;
        setContentView(R.layout.activity_add_event);
        LinearLayout startLayout = (LinearLayout) findViewById(R.id.startLayout);
        LinearLayout endLayout = (LinearLayout) findViewById(R.id.endLayout);
        tv_start = (TextView) findViewById(R.id.tv_startTime);
        tv_end = (TextView) findViewById(R.id.tv_endTime);
        cancel_btn = (Button) findViewById(R.id.cancelButton);
        add_btn = (Button) findViewById(R.id.addButton);
        spinner = (Spinner) findViewById(R.id.spinner_location);
        title = findViewById(R.id.et_title);
        locations = new ArrayList<>();

        //test location
        locations.add(new Location("Sainsburys(Longstone)","Test location"));
        //

        LocationSpinnerAdapter spinnerAdapter = new LocationSpinnerAdapter(context, locations);
        spinner.setAdapter(spinnerAdapter);
        DateFormat df = new SimpleDateFormat("EE dd MMM  kk:mm", Locale.UK);
        int hour = c.get(Calendar.HOUR) +1;
        c.set(Calendar.HOUR,hour);
        tv_start.setText(df.format(c.getTime()));
        hour++;
        c.set(Calendar.HOUR,hour);
        tv_end.setText(df.format(c.getTime()));
        //start time onClick
        startLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("start","start Clicked");
                SlideDateTimeListener listener = new SlideDateTimeListener() {

                    @Override
                    public void onDateTimeSet(Date date)
                    {
                        // Do something with the date. This Date object contains
                        // the date and time that the user has selected.
                        startDate = date;
                        DateFormat df = new SimpleDateFormat("EE dd MMM  kk:mm", Locale.UK);
                        tv_start.setText(df.format(startDate));
                    }

                    @Override
                    public void onDateTimeCancel()
                    {
                        // Overriding onDateTimeCancel() is optional.
                    }
                };
                Date date;
                if(startDate != null)
                {
                    date = startDate;
                }
                else
                {
                    date = c.getTime();
                }
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(date)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });
        //end time onClick
        endLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("end", "end Clicked");

                SlideDateTimeListener listener = new SlideDateTimeListener() {

                    @Override
                    public void onDateTimeSet(Date date)
                    {
                        // Do something with the date. This Date object contains
                        // the date and time that the user has selected.
                        endDate = date;
                        DateFormat df = new SimpleDateFormat("EE dd MMM  kk:mm", Locale.UK);
                        tv_end.setText(df.format(endDate));

                    }

                    @Override
                    public void onDateTimeCancel()
                    {
                        // Overriding onDateTimeCancel() is optional.
                    }
                };
                Date date;
                if(endDate != null)
                {
                    date = endDate;
                }
                else if(startDate != null)
                {
                    c.setTime(startDate);
                    c.set(Calendar.HOUR,c.get(Calendar.HOUR)+1);
                    date = c.getTime();
                }
                else
                {
                    date = c.getTime();
                }

                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(date)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = addEvent();
                if(success) {
                    String passbackString = "Add button works";
                    Intent intent = new Intent();
                    intent.putExtra("string", passbackString);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else{
                    Snackbar.make(view,"Unable to add Event",Snackbar.LENGTH_LONG).show();
                }
            }
        });
            }

            public boolean addEvent(){
                try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 0);
                }
                ContentResolver cr = getContentResolver();
                //add event details
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, startDate.getTime());
                values.put(CalendarContract.Events.DTEND, endDate.getTime());
                values.put(CalendarContract.Events.TITLE, title.getText().toString());
                values.put(CalendarContract.Events.DESCRIPTION, "");
                values.put(CalendarContract.Events.CALENDAR_ID, 16);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/London");
                Location location = (Location) spinner.getSelectedItem();
                values.put(CalendarContract.Events.EVENT_LOCATION, location.getLocation());
                values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, "1");
                values.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, "1");

                cr.insert(CalendarContract.Events.CONTENT_URI, values);
                }
                catch(SecurityException e){
                    Log.e("Security Exception", e.getMessage());
                    return false;
                }
                catch(Exception e) {
                    Log.e("General Exception", e.getMessage());
                    return false;
                }
                return true;
            }
    }
