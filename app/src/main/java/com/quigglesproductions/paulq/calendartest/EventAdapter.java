package com.quigglesproductions.paulq.calendartest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by paulq on 14/10/2019.
 */

public class EventAdapter extends ArrayAdapter<Event> {
    private Context context;
    int layoutResourceId;
    private ArrayList<Event> events;
    public EventAdapter(Context context, int layoutResourceId, ArrayList<Event> events)
    {
        super(context, layoutResourceId,events);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.events = events;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null)
        {
            //create the list item view
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.event_listitem, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        //get item at position
        final Event currentEvent = events.get(position);
        convertView.setLongClickable(true);
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "ProductID: " + currentItem.getItemID() , Toast.LENGTH_SHORT).show();
            }
        });*/

        // set on layout
        mViewHolder.title.setText(currentEvent.getTitle());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        mViewHolder.date.setText(dateFormat.format(currentEvent.getStartDate())+" "+currentEvent.getStartTime()+" - "+currentEvent.getEndTime());
        long shLen = currentEvent.getEndDate().getTime() - currentEvent.getStartDate().getTime();
        long seconds = shLen/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        minutes = minutes-(hours*60);
        dateFormat = new SimpleDateFormat("hh:mm:ss");
        mViewHolder.shiftLength.setText(hours+" hours "+minutes+" minutes");
        mViewHolder.earning.setText("£"+String.format("%.2f",currentEvent.getEarnings(9.95)));
        return convertView;

    }
    private class MyViewHolder {
        TextView title, date, shiftLength, earning;

        // refer on layout
        public MyViewHolder(View item) {
            //sets the View Holder Text Views
            title = (TextView) item.findViewById(R.id.tv_groupView_title);
            date = (TextView) item.findViewById(R.id.tv_groupView_date);
            shiftLength = (TextView) item.findViewById(R.id.tv_groupView_shiftlength);
            earning = (TextView) item.findViewById(R.id.tv_groupView_earning);
        }
    }
}
