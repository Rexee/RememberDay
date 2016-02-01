package com.rememberday;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DBListProvider extends ContentProvider {

    public static final String AUTHORITY = "eventsprovider";
    static final        String PATH      = "events";

    public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    public static final int TOKEN = 1;
    public static final String[] PROJECTION = {DB.FIELD_ID, DB.FIELD_DAY_MONTH_DESCR, DB.FIELD_DESCRIPTION, DB.FIELD_COLOR, DB.FIELD_WIKI_ID};
    public static String SortOrder = DB.FIELD_ID;

    DB dbHelper;

    public DBListProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DB(getContext());
        dbHelper.open();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase sqldb = dbHelper.getReadableDatabase();
        Cursor cursor = sqldb.query(DB.TABLE_MAIN, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), URI);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
