package com.quigglesproductions.paulq.calendartest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by paulq on 13/10/2019.
 */

public class Event {
    private int id;
    private int uid;
    private String title;
    private String eventLocation;
    private long dtstart;
    private long dtend;

    public Event()
    {

    }
    public Event(int id, String title, String eventLocation, long dtstart, long dtend)
    {
        this.id = id;
        this.title = title;
        this.eventLocation = eventLocation;
        this.dtstart = dtstart;
        this.dtend = dtend;
        this.uid = (int) (dtstart+dtend);
    }

    public Event(int id, String title, Location location, long dtstart, long dtend)
    {
        this.id = id;
        this.title = title;
        this.eventLocation = location.getLocation();
        this.dtstart = dtstart;
        this.dtend = dtend;
        this.uid = (int) (dtstart+dtend);
    }

    public Date getStartDate(){
        return new Date(dtstart);
    }

    public Date getEndDate(){
        return new Date(dtend);
    }

    public String getTitle(){
        return title;
    }

    public String getEventLocation()
    {
        return eventLocation;
    }

    public double getEarnings(double hourlyRate){
        double minuteRate = hourlyRate/60;
        double shLen = dtend - dtstart;
        double seconds = shLen/1000;
        double minutes = seconds/60;
        int hours = (int) (minutes/60);

        double earnings = minutes*minuteRate;
        earnings = earnings - breakLength(hours);
        return earnings;
    }

    public String getStartTime(){
        DateFormat dateFormat = new SimpleDateFormat("kk:mm");
        String time = dateFormat.format(new Date(dtstart));
        return time;
    }

    public String getEndTime(){
        DateFormat dateFormat = new SimpleDateFormat("kk:mm");
        String time = dateFormat.format(new Date(dtend));
        return time;
    }

    private double breakLength(int hours)
    {
        double breakLn;
        switch(hours){
            case 5:
                breakLn = 0.25;
            break;
            case 6:
                breakLn = 0.33333;
                break;
            case 7:
                breakLn = 0.41667;
                break;
            case 8:
                breakLn = 0.5;
                break;
            case 9:
                breakLn = 0.75;
                break;
            case 10:
                breakLn = 1;
                break;
            default:
                breakLn = 0;
                break;
        }
        return breakLn;

    }

    public double getShiftLength(){
        double shLen = dtend - dtstart;
        double seconds = shLen/1000;
        double minutes = seconds/60;
        double hours = (minutes/60);
        return hours;

    }

    public int getUid()
    {
        return uid;
    }

}
