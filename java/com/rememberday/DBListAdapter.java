package com.rememberday;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DBListAdapter extends CursorAdapter {

    public static final int COLOR_BLACK85 = 0x262626;

    private LayoutInflater mInflater;

    public DBListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) view.findViewById(android.R.id.text1);
        holder.description = (TextView) view.findViewById(android.R.id.text2);

        holder.nameIndex = cursor.getColumnIndex(DB.FIELD_DAY_MONTH_DESCR);
        holder.descriptionIndex = cursor.getColumnIndex(DB.FIELD_DESCRIPTION);
        holder.colorIndex = cursor.getColumnIndex(DB.FIELD_COLOR);

        view.setTag(holder);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(cursor.getString(holder.nameIndex));
        holder.description.setText(cursor.getString(holder.descriptionIndex));
        int color = cursor.getInt(holder.colorIndex);
        if (color == 0) color = COLOR_BLACK85;
        holder.name.setTextColor(0xFF000000 | color);
    }

    private static class ViewHolder {
        TextView name;
        TextView description;
        int      nameIndex;
        int      descriptionIndex;
        int      colorIndex;
    }

}