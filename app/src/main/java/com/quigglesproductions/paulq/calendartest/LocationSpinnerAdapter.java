package com.quigglesproductions.paulq.calendartest;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    ArrayList<Location> locations;
    Context context;
    public LocationSpinnerAdapter(Context context, ArrayList<Location> locations)
    {
        this.locations = locations;
        this.context = context;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Location location = locations.get(position);
        View view = View.inflate(context, R.layout.spinner_dropdown_item, null);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(location.getName());
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Location location = locations.get(position);
        View view = View.inflate(context, R.layout.spinner_item, null);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(location.getName());
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
