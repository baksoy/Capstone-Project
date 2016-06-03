package com.beraaksoy.whatplace.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by beraaksoy on 5/25/16.
 */
public class WhatPlaceProvider extends ContentProvider {
    private static final String TAG = WhatPlaceProvider.class.getSimpleName();

    private WhatPlaceDatabase mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int PLACES = 100;
    private static final int PLACE_ID = 101;
    private static final int PLACE_NAME = 102;
    private static final int PLACE_BGCOLOR = 102;
    // private static final int PLACE_ADDRESS = 103;
    // private static final int PLACE_PHONE = 104;
    // private static final int PLACE_WEBSITE = 105;
    // private static final int PLACE_PHOTO = 106;
    // private static final int PLACE_MEMO = 107;


    // Is the Uri valid ? Look at the uri that's passed to the content provider
    // and figure out if it is valid or not
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WhatPlaceContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "places", PLACES);
        matcher.addURI(authority, "places/*", PLACE_ID);
        matcher.addURI(authority, "places/*/name", PLACE_NAME);
        matcher.addURI(authority, "places/*/bgcolor", PLACE_BGCOLOR);
        // matcher.addURI(authority, "places/#/phone", PLACE_PHONE);
        // matcher.addURI(authority, "places/*/website", PLACE_WEBSITE);
        // matcher.addURI(authority, "places/*/photo", PLACE_PHOTO);
        // matcher.addURI(authority, "places/*/memo", PLACE_MEMO);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WhatPlaceDatabase(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                return WhatPlaceContract.Places.CONTENT_TYPE;
            case PLACE_ID:
                return WhatPlaceContract.Places.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // projection = new String[]{
        //         WhatPlaceContract.PlaceColumns.PLACE_NAME,
        //         WhatPlaceContract.PlaceColumns.PLACE_ADDRESS,
        //         WhatPlaceContract.PlaceColumns.PLACE_PHONE,
        //         WhatPlaceContract.PlaceColumns.PLACE_WEBSITE
        // };

        switch (match) {
            case PLACES:
                // Return ALL places
                queryBuilder.setTables(WhatPlaceDatabase.Tables.PLACES);
                break;
            case PLACE_ID:
                // Return 1 place
                queryBuilder.setTables(WhatPlaceDatabase.Tables.PLACES);
                String id = WhatPlaceContract.Places.getPlaceId(uri);
                queryBuilder.appendWhere(BaseColumns._ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "insert(uri=" + uri + ", values=" + values.toString());

        switch (match) {
            case PLACES:
                long recordId = db.insertOrThrow(WhatPlaceDatabase.Tables.PLACES, null, values);
                Log.d(TAG, "Return recordId: " + recordId);
                Log.d(TAG, "Return Uri: " + WhatPlaceContract.Places.buildPlaceUri(String.valueOf(recordId)).toString());
                return WhatPlaceContract.Places.buildPlaceUri(String.valueOf(recordId));
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update (uri=" + uri + ", values=" + values.toString());

        String selectionCriteria = selection;

        switch (match) {
            case PLACES:
                // do nothing to prevent accidental BULK update
                break;
            case PLACE_ID:
                String id = WhatPlaceContract.Places.getPlaceId(uri);
                selectionCriteria = BaseColumns._ID + "= " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        return db.update(WhatPlaceDatabase.Tables.PLACES, values, selectionCriteria, selectionArgs);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "delete (uri=" + uri);

        // Delete the COMPLETE database!!! We may introduce this feature in the future
        if (uri.equals(WhatPlaceContract.BASE_CONTENT_URI)) {
            WhatPlaceDatabase.deleteDatabase(getContext());
            return 0;
        }

        switch (match) {
            case PLACE_ID:
                String id = WhatPlaceContract.Places.getPlaceId(uri);
                String selectionCriteria = BaseColumns._ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                return db.delete(WhatPlaceDatabase.Tables.PLACES, selectionCriteria, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }
}
