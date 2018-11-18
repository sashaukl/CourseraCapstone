package com.example.percival.coursera;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.net.URI;

public class MyContactProvider extends ContentProvider {
    private static final String DB_NAME = "myDB";
    //private static final String DB_VERSION = "myDB";

    //tables
    private static final String TEMPERATURE_TABLE = "temperature";

    //fields
    private static final String CITY = "city";
    private static final String TEMPERATURE = "temperature";

    //create table script
    private static final String CREATE_TABLE = "CREATE TABLE " + TEMPERATURE_TABLE
            + "(" + CITY + " text primary key, " + TEMPERATURE + " REAL);";

    // //URI
    //authority
    private static final String AUTHORITY = "com.example.percival";

    //path
    private static final String CONTACT_PATH = "temperature";

    //common URI
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+CONTACT_PATH);

    //data types
    //strings
    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd"+AUTHORITY+"."+CONTACT_PATH;
    //string
    private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd"+AUTHORITY+"."+CONTACT_PATH;

    //URI mather
    //common uri
    private static final int URI_TEMPERATURE = 1;

    //URI with id
    static final int URI_TEMPERATURE_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,CONTACT_PATH, URI_TEMPERATURE);
        uriMatcher.addURI(AUTHORITY,CONTACT_PATH + "/*", URI_TEMPERATURE_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        Log.d("onCreate", "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    // read
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // check Uri
        switch (uriMatcher.match(uri)) {
            case URI_TEMPERATURE:
                // if no sort
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CITY + " ASC";
                }
                break;
            case URI_TEMPERATURE_ID:
                String id = uri.getLastPathSegment();

                // add ID to selection
                if (TextUtils.isEmpty(selection)) {
                    selection = CITY + " = \"" + id+"\"";
                } else {
                    selection = selection + " AND " + CITY + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TEMPERATURE_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в CONTACT_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(),
                CONTENT_URI);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_TEMPERATURE)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(TEMPERATURE_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_TEMPERATURE:
                break;
            case URI_TEMPERATURE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = CITY + " = " + id;
                } else {
                    selection = selection + " AND " + CITY + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(TEMPERATURE_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_TEMPERATURE:

                break;
            case URI_TEMPERATURE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = CITY + " = " + id;
                } else {
                    selection = selection + " AND " + CITY + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(TEMPERATURE_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case URI_TEMPERATURE:
                return  CONTENT_TYPE;
            case URI_TEMPERATURE_ID:
                return CONTENT_ITEM_TYPE;
        }
        return null;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public String getType(Uri uri) {
            switch (uriMatcher.match(uri)) {
                case URI_TEMPERATURE:
                    return CONTENT_TYPE;
                case URI_TEMPERATURE_ID:
                    return CONTENT_ITEM_TYPE;
            }
            return null;
        }

        public DBHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            ContentValues cv = new ContentValues();
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}