package com.rememberday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class DB extends SQLiteOpenHelper {

    private final Context        mContext;
    private       SQLiteDatabase mDB;
    private static      String DB_PATH    = "";
    public static final String DB_NAME    = "events.db";
    public static final int    DB_VERSION = 13;

    public static final String TABLE_MAIN = "events";

    public static final String FIELD_ID              = "_id";
    public static final String FIELD_YEAR            = "year";
    public static final String FIELD_DAY_MONTH       = "daymonth";
    public static final String FIELD_DESCRIPTION     = "description";
    public static final String FIELD_COLOR           = "color";
    public static final String FIELD_LOCALE          = "locale";
    public static final String FIELD_PRIORITY        = "priority";
    public static final String FIELD_DAY_MONTH_DESCR = "daymonth_descr";
    public static final String FIELD_WIKI_ID         = "wiki_id";

    class event {
        public int year;
        public String name;
        public int    color;
        public String wiki_id;

        public event() {
            this.year = 0;
            this.name = "";
            this.color = 0;
            this.wiki_id = "";
        }
    }

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        if (!isDBExists()) {
            updateDBFiles();
        }
    }

    private void updateDBFiles() {
        L.L("copying DB...");
        this.getReadableDatabase();
        this.close();
        copyDBFiles();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        L.L("onUpgrade:" + oldVersion + " " + newVersion);
    }

    public void open() {

        mDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        final int ver = mDB.getVersion();

        if (ver == 0) mDB.setVersion(DB_VERSION);
        else if (ver < DB_VERSION) {

            try {
                mDB.close();
                updateDBFiles();
                mDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            } finally {
                mDB.setVersion(DB_VERSION);
            }

        }

    }

    @Override
    public synchronized void close() {
        if (mDB != null) mDB.close();
        super.close();
    }

    private boolean isDBExists() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDBFiles() {
        InputStream mInput = null;
        try {
            mInput = mContext.getAssets().open("db/" + DB_NAME);

            String outFileName = DB_PATH + DB_NAME;

            OutputStream mOutput = null;
            mOutput = new FileOutputStream(outFileName);

            byte[] mBuffer = new byte[1024];
            int mLength;

            if (mInput != null) {
                while ((mLength = mInput.read(mBuffer)) > 0) {
                    if (mOutput != null) {
                        mOutput.write(mBuffer, 0, mLength);
                    }
                }
            }

            if (mOutput != null) {
                mOutput.flush();
                mOutput.close();
            }


            if (mInput != null) {
                mInput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public event getMainEventOnDay(Calendar calendar) {

        String dayStr = getDayRepresentation(calendar);
        final Cursor cursor = mDB.rawQuery("SELECT " + FIELD_DESCRIPTION + ", " + FIELD_COLOR + " FROM " + TABLE_MAIN + " WHERE " + FIELD_DAY_MONTH + " = ? ORDER BY " + FIELD_PRIORITY + " ASC LIMIT 1", new String[]{dayStr});

        event res = new event();

        if (cursor.moveToFirst()) {
            res.name = cursor.getString(0);
            res.color = cursor.getInt(1);
        }

        cursor.close();

        return res;
    }

    public ArrayList<event> getAllEventsOnDay(Calendar calendar) {
        ArrayList<event> res;
        String dayStr = getDayRepresentation(calendar);

        final Cursor cursor = mDB.rawQuery("SELECT " + FIELD_DESCRIPTION + ", " + FIELD_COLOR + ", " + FIELD_YEAR + ", " + FIELD_WIKI_ID + " FROM " + TABLE_MAIN + " WHERE " + FIELD_DAY_MONTH + " = ? ORDER BY " + FIELD_PRIORITY + " ASC", new String[]{dayStr});

        final int count = cursor.getCount();

        if (count > 0) {
            res = new ArrayList<>(count);

            while (cursor.moveToNext()) {
                event newEvent = new event();
                newEvent.name = cursor.getString(0);
                newEvent.color = cursor.getInt(1);
                newEvent.year = cursor.getInt(2);
                newEvent.wiki_id = cursor.getString(3);
                res.add(newEvent);
            }

        } else res = new ArrayList<>();

        cursor.close();

        return res;
    }

    public String getDayRepresentation(Calendar calendar)
    {
        String dayStr = "0" + calendar.get(Calendar.DAY_OF_MONTH);
        dayStr = dayStr.substring(dayStr.length() - 2);
        String monthStr = "0" + (calendar.get(Calendar.MONTH)+1);
        monthStr = monthStr.substring(monthStr.length() - 2);

        return dayStr + monthStr;
    }

    public void addEventOnDay(String dayStr, String event) {

        final SQLiteDatabase db = this.getWritableDatabase();

        final ContentValues row = new ContentValues(2);

        row.put(FIELD_DAY_MONTH, dayStr);
        row.put(FIELD_DESCRIPTION, event);

        final long result = db.replace(TABLE_MAIN, null, row);

        if (result < 0) {
            L.L("DB insert failed. topicID: " + dayStr);

        }
    }

    public int getMainIdOnDay(Calendar calendar) {
        String dayStr = getDayRepresentation(calendar);
        final Cursor cursor = mDB.rawQuery("SELECT " + FIELD_ID + " FROM " + TABLE_MAIN + " WHERE " + FIELD_DAY_MONTH + " = ? ORDER BY " + FIELD_PRIORITY + " ASC LIMIT 1", new String[]{dayStr});

        int res = -1;
        if (cursor.moveToFirst()) res = cursor.getInt(0);

        cursor.close();

        return res;
    }
}
