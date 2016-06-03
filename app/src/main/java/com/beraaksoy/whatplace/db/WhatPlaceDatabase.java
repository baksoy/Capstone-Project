package com.beraaksoy.whatplace.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by beraaksoy on 5/25/16.
 */
class WhatPlaceDatabase extends SQLiteOpenHelper {

    private static final String TAG = WhatPlaceDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "whatplace.db";
    private static final int DATABASE_VERSION = 3;
    private final Context mContext;

    interface Tables {
        String PLACES = "places";
    }

    public WhatPlaceDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the latest version of the database
        db.execSQL("CREATE TABLE " + Tables.PLACES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WhatPlaceContract.PlaceColumns.PLACE_NAME + " TEXT NOT NULL,"
                + WhatPlaceContract.PlaceColumns.PLACE_ADDRESS + " TEXT,"
                + WhatPlaceContract.PlaceColumns.PLACE_PHONE + " TEXT,"
                + WhatPlaceContract.PlaceColumns.PLACE_WEBSITE + " TEXT,"
                + WhatPlaceContract.PlaceColumns.PLACE_PHOTO + " TEXT,"
                + WhatPlaceContract.PlaceColumns.PLACE_MEMO + " TEXT,"
                + WhatPlaceContract.PlaceColumns.PLACE_BGCOLOR + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion;
        if (version == 2) {
            // Version check. If user is on older version, add some fields to
            // their DB here without deleting their existing data and set them to
            // the latest database version.
            version = 3;
        }

        if (version != DATABASE_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + Tables.PLACES);
            onCreate(db);
        }
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
