package com.beraaksoy.whatplace.db;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.beraaksoy.whatplace.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beraaksoy on 5/25/16.
 */
public class PlaceListLoader extends AsyncTaskLoader<List<Place>> {

    private static final String TAG = PlaceListLoader.class.getSimpleName();
    private List<Place> mPlaces;
    private final ContentResolver mContentResolver;
    private Cursor mCursor;

    public PlaceListLoader(Context context, Uri uri, ContentResolver contentResolver) {
        super(context);
        mContentResolver = contentResolver;
    }

    // Grab all Places in the backgound
    @Override
    public List<Place> loadInBackground() {
        String[] projection = {BaseColumns._ID,
                WhatPlaceContract.PlaceColumns.PLACE_NAME,
                WhatPlaceContract.PlaceColumns.PLACE_ADDRESS,
                WhatPlaceContract.PlaceColumns.PLACE_PHONE,
                WhatPlaceContract.PlaceColumns.PLACE_WEBSITE,
                WhatPlaceContract.PlaceColumns.PLACE_PHOTO,
                WhatPlaceContract.PlaceColumns.PLACE_MEMO,
                WhatPlaceContract.PlaceColumns.PLACE_BGCOLOR};
        List<Place> entries = new ArrayList<>();
        mCursor = mContentResolver.query(WhatPlaceContract.URI_TABLE, projection, null, null, null);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                do {
                    int _id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID));
                    String name = mCursor.getString(mCursor.getColumnIndex(WhatPlaceContract.PlaceColumns.PLACE_NAME));
                    String address = mCursor.getString(mCursor.getColumnIndex(WhatPlaceContract.PlaceColumns.PLACE_ADDRESS));
                    String phone = mCursor.getString(mCursor.getColumnIndex(WhatPlaceContract.PlaceColumns.PLACE_PHONE));
                    String website = mCursor.getString(mCursor.getColumnIndex(WhatPlaceContract.PlaceColumns.PLACE_WEBSITE));
                    String memo = mCursor.getString(mCursor.getColumnIndex(WhatPlaceContract.PlaceColumns.PLACE_MEMO));
                    byte[] photo = mCursor.getBlob(mCursor.getColumnIndex(WhatPlaceContract.PlaceColumns.PLACE_PHOTO));
                    int bgcolor = mCursor.getInt(mCursor.getColumnIndex(WhatPlaceContract.PlaceColumns.PLACE_BGCOLOR));

                    Place place = new Place(_id, name, address, phone, website, photo, memo, bgcolor);
                    entries.add(place);
                } while (mCursor.moveToNext());
            }
        }
        return entries;
    }

    @Override
    public void deliverResult(List<Place> places) {
        if (isReset()) {
            if (places != null) {
                // Close cursor if loader manager calls reset
                mCursor.close();
            }
        }
        List<Place> oldPlaceList = mPlaces;
        if (mPlaces == null || mPlaces.size() == 0) {
            Log.d(TAG, "deliverResult: NO DATA RETURNED!");
        }
        mPlaces = places;

        if (isStarted()) {
            // if loader is started, we can deliver the results
            super.deliverResult(places);
        }

        if (oldPlaceList != null && oldPlaceList != places) {
            // Release cursor if its different data that what we had and we are done
            mCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mPlaces != null) {
            deliverResult(mPlaces);
        }
        if (takeContentChanged() || mPlaces == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mCursor != null) {
            mCursor.close();
        }
        mPlaces = null;
    }

    @Override
    public void onCanceled(List<Place> places) {
        super.onCanceled(places);
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }
}