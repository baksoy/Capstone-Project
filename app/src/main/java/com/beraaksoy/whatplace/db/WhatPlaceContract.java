package com.beraaksoy.whatplace.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by beraaksoy on 5/25/16.
 */
public class WhatPlaceContract {

    public interface PlaceColumns {
        String PLACE_ID = "_id";
        String PLACE_NAME = "place_name";
        String PLACE_ADDRESS = "place_address";
        String PLACE_PHONE = "place_phone";
        String PLACE_WEBSITE = "place_website";
        String PLACE_PHOTO = "place_photo";
        String PLACE_MEMO = "place_memo";
        String PLACE_BGCOLOR = "place_bgcolor";
    }

    public static final String CONTENT_AUTHORITY = "com.beraaksoy.whatplace.db.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_PLACES = "places";
    public static final Uri URI_TABLE = Uri.parse(BASE_CONTENT_URI.toString() + "/" + PATH_PLACES);

    public static final String[] TOP_LEVEL_PATHS = {
            PATH_PLACES
    };

    public static class Places implements PlaceColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_PLACES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".places";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".places";

        public static Uri buildPlaceUri(String placeId) {
            return CONTENT_URI.buildUpon().appendEncodedPath(placeId).build();
        }

        public static String getPlaceId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
