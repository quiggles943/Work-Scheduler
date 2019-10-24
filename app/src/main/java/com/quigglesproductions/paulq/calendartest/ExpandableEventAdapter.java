package com.quigglesproductions.paulq.calendartest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by paulq on 13/10/2019.
 */

public class ExpandableEventAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> dates;
    private HashMap<String,ArrayList<Event>> dataSet;

    public ExpandableEventAdapter(Context context,ArrayList<String> dates, HashMap<String,ArrayList<Event>> events)
    {
        this.context = context;
        this.dates = dates;
        this.dataSet = events;
    }

    @Override
    public int getGroupCount() {

        return this.dates.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        String key = this.dates.get(listPosition);
        int size = this.dataSet.get(key).size()+1;
        return size;
    }

    @Override
    public Object getGroup(int listPosition) {

        return this.dates.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition)
    {
        return this.dataSet.get(this.dates.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {

        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPostion) {
        return expandedListPostion;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String date = dates.get(listPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.groupview_date,null);
        }
        TextView dateView = (TextView) convertView.findViewById(R.id.tv_groupView_date);
        TextView shiftNumber = (TextView) convertView.findViewById(R.id.tv_groupView_shift_number);


        dateView.setText(date);
        //long shLen = date.getEndDate().getTime() - date.getStartDate().getTime();
        shiftNumber.setText((getChildrenCount(listPosition)-1)+" shift(s)");
        return convertView;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView title, date, shiftLength, earning;
        //create the list item view
        if(expandedListPosition<getChildrenCount(listPosition)-1) {
            final Event currentEvent = (Event) getChild(listPosition,expandedListPosition);
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.event_listitem, parent, false);
            //get item at position
            convertView.setLongClickable(true);
            title = (TextView) convertView.findViewById(R.id.tv_groupView_title);
            date = (TextView) convertView.findViewById(R.id.tv_groupView_date);
            shiftLength = (TextView) convertView.findViewById(R.id.tv_groupView_shiftlength);
            earning = (TextView) convertView.findViewById(R.id.tv_groupView_earning);
            // set on layout
            title.setText(currentEvent.getTitle());
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            date.setText(dateFormat.format(currentEvent.getStartDate()) + " " + currentEvent.getStartTime() + " - " + currentEvent.getEndTime());
            long shLen = currentEvent.getEndDate().getTime() - currentEvent.getStartDate().getTime();
            long seconds = shLen / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            minutes = minutes - (hours * 60);
            dateFormat = new SimpleDateFormat("hh:mm:ss");
            shiftLength.setText(hours + " hours " + minutes + " minutes");
            earning.setText("£" + String.format("%.2f", currentEvent.getEarnings(9.95)));
        }
        if(expandedListPosition == getChildrenCount(listPosition)-1)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.childview_footer,parent,false);
            TextView hrsWorked = (TextView) convertView.findViewById(R.id.tv_childViewFooter_hrs);
            TextView totalEarnings = (TextView) convertView.findViewById(R.id.tv_childViewFooter_total_earnings);
            hrsWorked.setText(getTotalShiftLength(listPosition)+" hrs Total");
            totalEarnings.setText("£"+String.format("%.2f",getDayEarnings(listPosition)));
        }
        return convertView;
    }

    public double getDayEarnings(int listPosition)
    {
        double earnings = 0;
        ArrayList<Event> events = this.dataSet.get(this.dates.get(listPosition));
        for(Event e : events)
        {
            earnings = earnings + e.getEarnings(9.95);
        }
        return earnings;
    }

    public double getTotalShiftLength(int listPosition)
    {
        double length = 0;
        ArrayList<Event> events = this.dataSet.get(this.dates.get(listPosition));
        for(Event e : events)
        {
            length = length + e.getShiftLength();
        }
        return length;
    }

    public void update(ArrayList<String> dates,HashMap<String,ArrayList<Event>> events){
        this.dates = dates;
        this.dataSet = events;
        notifyDataSetChanged();
    }



    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
