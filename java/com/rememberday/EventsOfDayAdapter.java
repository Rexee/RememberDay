package com.rememberday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rememberday.DB.event;

import java.util.ArrayList;

public class EventsOfDayAdapter extends BaseAdapter {

    private final LayoutInflater   mInflater;
    private final ArrayList<event> mEvents;

    public EventsOfDayAdapter(Context context, ArrayList<event> events) {
        mEvents = events;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public event getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View v = convertView;

        event currentEvent = mEvents.get(position);

        if (v == null) {
            v = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(android.R.id.text1);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        String eventRepresentation = currentEvent.name;
        if (currentEvent.year != 0 ) eventRepresentation = "" + currentEvent.year + ". " + eventRepresentation;
        holder.name.setText(eventRepresentation);

        int color = currentEvent.color;
        if (color == 0 ) color = TextDrawer.mSecondaryColor;
        holder.name.setTextColor(0xFF000000 | color);


        return v;
    }
}
