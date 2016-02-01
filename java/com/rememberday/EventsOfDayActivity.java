package com.rememberday;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rememberday.R;
import com.rememberday.DB.event;

import java.util.ArrayList;
import java.util.Calendar;


public class EventsOfDayActivity extends Activity {

    EventsOfDayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Bundle args = getIntent().getExtras();
        if (args!=null) {
            WindowManager.LayoutParams wmlp = getWindow().getAttributes();
            wmlp.gravity = Gravity.TOP | Gravity.CENTER;
            wmlp.y = args.getInt("y", 0);
        }
        setContentView(R.layout.activity_list_events_of_day);

        Calendar calendar = Calendar.getInstance();
        DB eventsDB = new DB(EventsOfDayActivity.this);
        eventsDB.open();
        ArrayList<event> eventsList = eventsDB.getAllEventsOnDay(calendar);
        eventsDB.close();

        ListView lv = (ListView) findViewById(R.id.lv_EventsOfDay);

        mAdapter = new EventsOfDayAdapter(this, eventsList);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new EventOnItemClickListener());

    }

    private class EventOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            event selectedEvent = (event) adapter.getItemAtPosition(position);
            String wiki_id = selectedEvent.wiki_id;
            if (!wiki_id.isEmpty())
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Settings.WIKI_URL + wiki_id));
                startActivity(browserIntent);
            }

        }
    }
}
