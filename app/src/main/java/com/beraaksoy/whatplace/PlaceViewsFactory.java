package com.beraaksoy.whatplace;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.beraaksoy.whatplace.db.WhatPlaceContract;

import java.util.ArrayList;
import java.util.List;

public class PlaceViewsFactory implements
        RemoteViewsService.RemoteViewsFactory {

    private Context ctxt = null;
    private int appWidgetId;
    private ContentResolver mContentResolver;
    public List<Place> mPlaces;

    public PlaceViewsFactory(Context ctxt, Intent intent) {
        this.ctxt = ctxt;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        mContentResolver = ctxt.getContentResolver();
    }

    private List<Place> getPlaceList() {
        List<Place> entries = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                WhatPlaceContract.PlaceColumns.PLACE_NAME,
                WhatPlaceContract.PlaceColumns.PLACE_ADDRESS,
                WhatPlaceContract.PlaceColumns.PLACE_PHONE,
                WhatPlaceContract.PlaceColumns.PLACE_WEBSITE,
                WhatPlaceContract.PlaceColumns.PLACE_PHOTO,
                WhatPlaceContract.PlaceColumns.PLACE_MEMO,
                WhatPlaceContract.PlaceColumns.PLACE_BGCOLOR};
        Cursor mCursor = mContentResolver.query(WhatPlaceContract.URI_TABLE, projection, null, null, null);
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
            mCursor.close();
        }
        return entries;
    }

    @Override
    public void onCreate() {
        mPlaces = getPlaceList();
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return (mPlaces.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctxt.getPackageName(), R.layout.row);
        row.setTextViewText(android.R.id.text1, mPlaces.get(position).getName());

        Intent i = new Intent();
        Bundle extras = new Bundle();

        extras.putString(PlaceWidgetProvider.EXTRA_PLACE, mPlaces.get(position).getName());
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        i.putExtras(extras);
        row.setOnClickFillInIntent(android.R.id.text1, i);

        return (row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return (null);
    }

    @Override
    public int getViewTypeCount() {
        return (1);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public boolean hasStableIds() {
        return (true);
    }

    @Override
    public void onDataSetChanged() {
        // no-op
    }

}