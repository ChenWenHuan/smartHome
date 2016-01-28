package com.smarthome.client2.content;

import com.smarthome.client2.common.DatabaseConstants;
import com.smarthome.client2.manager.DBManager;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * 
 * @author xingang.sun.com
 *
 */
public class ClientContentProvider extends ContentProvider implements DatabaseConstants {
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String AUTHORITY = ContentProvider.class.getName();
    private static final int MESSAGES = 1;
    private static final int MESSAGE  = 2;
    static {
        MATCHER.addURI(AUTHORITY, "messages", MESSAGES);
        MATCHER.addURI(AUTHORITY, "message/#", MESSAGE);
    }
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private DBManager db;

    @Override
    public boolean onCreate() {
        db = DBManager.open(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
        case MESSAGES:
            return "vnd.android.cursor.dir/com.smarthome.client2.content.message";
        case MESSAGE:
            return "vnd.android.cursor.item/com.smarthome.client2.content.message";
        default:
            throw new IllegalArgumentException(uri.toString());
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (MATCHER.match(uri)) {
        case MESSAGES:
            return db.queryByClientProvider(T_MESSAGE, projection, selection, selectionArgs, null, null, sortOrder);
        case MESSAGE:
            long _id = ContentUris.parseId(uri);
            selection = T_MESSAGE_ID + "=?";
            selectionArgs = new String[] { String.valueOf(_id) };
            return db.queryByClientProvider(T_MESSAGE, projection, selection, selectionArgs, null, null, sortOrder);
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (MATCHER.match(uri)) {
        case MESSAGES:
        db.insertByClientProvider(T_MESSAGE, null, values);
        /* TODO */
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
