package com.rememberday;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Calendar;


public class DBListActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    DBListAdapter mAdapter;
    ListView            lvMain;
    int                 eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new DBListAdapter(this, null, 0);

        lvMain = (ListView) findViewById(R.id.mainList);
        lvMain.setAdapter(mAdapter);
        lvMain.setOnItemClickListener(new EventOnItemClickListener());
        lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        getSupportLoaderManager().initLoader(DBListProvider.TOKEN, null, this);

        Calendar calendar = Calendar.getInstance();
        DB eventsDB = new DB(DBListActivity.this);
        eventsDB.open();
        eventId = eventsDB.getMainIdOnDay(calendar);
        eventsDB.close();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DBListProvider.TOKEN) {
            CursorLoader cursorLoader = new CursorLoader(this, DBListProvider.URI, DBListProvider.PROJECTION, null, null, DBListProvider.SortOrder);
            return cursorLoader;
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mAdapter.swapCursor(cursor);

        if (eventId < 0) return;

        lvMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvMain.requestFocusFromTouch();
                lvMain.setSelection(eventId-1);
            }
        },200);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private class EventOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) mAdapter.getItem(position);
            cur.moveToPosition(position);
            String wiki_id = cur.getString(cur.getColumnIndexOrThrow(DB.FIELD_WIKI_ID));
            if (!wiki_id.isEmpty())
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Settings.WIKI_URL+wiki_id));
                startActivity(browserIntent);
            }

        }
    }
}
