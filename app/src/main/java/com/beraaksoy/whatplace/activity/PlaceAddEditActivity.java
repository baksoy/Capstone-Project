package com.beraaksoy.whatplace.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beraaksoy.whatplace.Place;
import com.beraaksoy.whatplace.R;
import com.beraaksoy.whatplace.db.WhatPlaceContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by beraaksoy on 5/25/16.
 */

public class PlaceAddEditActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    public static final String ACTION_ADD = "add_place";
    public static final String ACTION_EDIT = "update_place";

    private static final String TAG = "PlaceAddEditActivity";
    private static final int API_REQ_CODE = 222;
    private static final int IMAGE_WIDTH = 480;
    private static final int IMAGE_HEIGHT = 360;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.place_name)
    EditText placeName;
    @BindView(R.id.place_address)
    EditText placeAddress;
    @BindView(R.id.place_phone)
    EditText placePhone;
    @BindView(R.id.place_website)
    EditText placeWebsite;
    @BindView(R.id.place_memo)
    EditText placeMemo;
    @BindView(R.id.place_photo_view)
    ImageView placePhotoView;
    @BindView(R.id.fab_save_place)
    FloatingActionButton fabSavePlace;

    private ContentResolver mContentResolver;
    private boolean isEditable = false;
    private Place mPlace; // Our Serializable Place
    private String mPlaceId; // Needed to fetch a photo from Google Places

    private TextView mPlaceNameView;
    private TextView mPlaceAddressView;
    private TextView mPlacePhoneView;
    private TextView mPlaceWebsiteView;
    private TextView mPlaceMemoView;
    private ImageView mPlacePhotoView;
    public AdView mAdview;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_add);
        ButterKnife.bind(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        mAdview = (AdView) findViewById(R.id.adView);
        if (mAdview != null) {
            //Turn off Interstitial Ad for now
            mAdview.setVisibility(View.GONE);
        }


        String addUnitId = getResources().getString(R.string.ad_unit_id);
        new AdFetchTask().execute(addUnitId);

        mPlaceNameView = (TextView) findViewById(R.id.place_name);
        mPlaceAddressView = (TextView) findViewById(R.id.place_address);
        mPlacePhoneView = (TextView) findViewById(R.id.place_phone);
        mPlaceWebsiteView = (TextView) findViewById(R.id.place_website);
        mPlaceMemoView = (TextView) findViewById(R.id.place_memo);
        mPlacePhotoView = (ImageView) findViewById(R.id.place_photo_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mPlace = (Place) intent.getSerializableExtra(MainActivity.PLACE);
        mPlaceId = intent.getStringExtra(MainActivity.PLACE_ID);

        if (intent.getAction().equals(ACTION_ADD)) {
            isEditable = true;
            // Get a photo for the Place
            placePhotoTask();
        }
        if (intent.getAction().equals(ACTION_EDIT)) {
            enableFields(false);
            Bitmap mPlacePhoto = null;
            if (mPlace.getPhotoId() != null) {
                mPlacePhotoView = (ImageView) findViewById(R.id.place_photo_view);
                byte[] byteArrayExtra = mPlace.getPhotoId();

                if (byteArrayExtra != null) {
                    mPlacePhoto = BitmapFactory.decodeByteArray(byteArrayExtra, 0, byteArrayExtra.length);
                }
                mPlacePhotoView.setImageBitmap(mPlacePhoto);
            }
        }

        mPlaceNameView.setText(mPlace.getName());
        mPlaceAddressView.setText(mPlace.getAddress());
        mPlacePhoneView.setText(mPlace.getPhone());
        mPlaceWebsiteView.setText(mPlace.getWebsite());
        mPlaceMemoView.setText(mPlace.getMemo());


        Log.d(TAG, "Place ID: " + mPlaceId);

        mContentResolver = PlaceAddEditActivity.this.getContentResolver();
        final FloatingActionButton save_edit_place_fab = (FloatingActionButton) findViewById(R.id.fab_save_place);

        if (!isEditable) {
            assert save_edit_place_fab != null;
            save_edit_place_fab.setImageResource(R.drawable.ic_menu_edit);
        }

        assert save_edit_place_fab != null;
        save_edit_place_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                if (intent.getAction().equals(ACTION_ADD)) {
                    isEditable = true;
                    if (isValid()) {
                        ContentValues values = getContentValues();
                        mContentResolver.insert(WhatPlaceContract.URI_TABLE, values);
                        finishAddEdit();
                        Toast.makeText(PlaceAddEditActivity.this, "Place saved to your list", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Place name is required. Please enter valid data.", Toast.LENGTH_LONG).show();
                    }
                }

                if (intent.getAction().equals(ACTION_EDIT)) {
                    enableFields(false);
                    save_edit_place_fab.setImageResource(R.drawable.ic_menu_save);
                    if (isEditable) {
                        if (isValid()) {
                            ContentValues values = getContentValues();
                            String[] placeIds = {Integer.toString(mPlace.get_id())};
                            // int placeBgColorIndex = mPlace.getBgColorIndex();
                            mContentResolver.update(WhatPlaceContract.URI_TABLE, values, WhatPlaceContract.PlaceColumns.PLACE_ID.concat("= ?"), placeIds);
                            finishAddEdit();
                            Toast.makeText(PlaceAddEditActivity.this, "Place updated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Place name is required. Please enter valid data.", Toast.LENGTH_LONG).show();
                        }
                    }
                    isEditable = !isEditable;
                    enableFields(isEditable);
                }
            }
        });
    }

    // Finish off Adding/Editing and go to Main Screen with List of Cards
    private void finishAddEdit() {
        Intent mainActivityIntent = new Intent(PlaceAddEditActivity.this, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityIntent);
        finish();
    }

    @NonNull
    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(WhatPlaceContract.PlaceColumns.PLACE_NAME, mPlaceNameView.getText().toString());
        values.put(WhatPlaceContract.PlaceColumns.PLACE_ADDRESS, mPlaceAddressView.getText().toString());
        values.put(WhatPlaceContract.PlaceColumns.PLACE_PHONE, mPlacePhoneView.getText().toString());
        values.put(WhatPlaceContract.PlaceColumns.PLACE_WEBSITE, mPlaceWebsiteView.getText().toString());
        values.put(WhatPlaceContract.PlaceColumns.PLACE_MEMO, mPlaceMemoView.getText().toString());
        values.put(WhatPlaceContract.PlaceColumns.PLACE_PHOTO, mPlace.getPhotoId());
        if (mPlace.getBgColorIndex() != 0) {
            values.put(WhatPlaceContract.PlaceColumns.PLACE_BGCOLOR, mPlace.getBgColorIndex());
        }

        Log.d(TAG, "*********************** SAVED DATA ************************************ ");
        Log.d(TAG, "Place Name Saved: " + values.get(WhatPlaceContract.PlaceColumns.PLACE_NAME));
        Log.d(TAG, "Place Address Saved: " + values.get(WhatPlaceContract.PlaceColumns.PLACE_ADDRESS));
        Log.d(TAG, "Place Phone Saved: " + values.get(WhatPlaceContract.PlaceColumns.PLACE_PHONE));
        Log.d(TAG, "Place Website Saved: " + values.get(WhatPlaceContract.PlaceColumns.PLACE_WEBSITE));
        Log.d(TAG, "Place Memo Saved: " + values.get(WhatPlaceContract.PlaceColumns.PLACE_MEMO));
        Log.d(TAG, "Place Photo Saved: " + values.get(WhatPlaceContract.PlaceColumns.PLACE_PHOTO));
        Log.d(TAG, "Place BackgroundColor Index Saved: " + values.get(WhatPlaceContract.PlaceColumns.PLACE_BGCOLOR));

        return values;
    }

    private void enableFields(boolean isActive) {
        mPlaceNameView.setEnabled(isActive);
        mPlaceAddressView.setEnabled(isActive);
        mPlacePhoneView.setEnabled(isActive);
        mPlaceWebsiteView.setEnabled(isActive);
        mPlaceMemoView.setEnabled(isActive);
    }

    public static Intent getActionIntent(Context context, Place place, String action) {
        Intent intent = new Intent(context, PlaceAddEditActivity.class);
        intent.setAction(action);
        intent.putExtra(MainActivity.PLACE, place);
        return intent;
    }

    private boolean isValid() {
        return mPlaceNameView.getText().toString().length() != 0;
    }

    private void placePhotoTask() {
        final String placeId = mPlaceId;

        new PhotoTask() {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                mPlacePhotoView.setImageResource(R.drawable.placeholder);
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    //Photo is loaded. Display it
                    mPlacePhotoView.setImageBitmap(attributedPhoto.bitmap);

                    Bitmap photo = attributedPhoto.bitmap;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    mPlace.setPhotoId(bos.toByteArray());


                    //Display the attribution at HTML content if set
                    if (attributedPhoto.attribution == null) {
                        Log.d(TAG, "Attribution: NONE");
                    } else {
                        Log.d(TAG, "Attribution: " + Html.fromHtml(attributedPhoto.attribution.toString()));
                    }
                }
            }
        }.execute(placeId);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApiClient successfully connected!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, API_REQ_CODE);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                Log.e(TAG, "onConnectionFailed: ", e);
                finish();
            }
        } else {
            finish();
        }
    }

    // Fetch Ad Service
    class AdFetchTask extends AsyncTask<String, Void, AdRequest> {

        @Override
        protected void onPostExecute(AdRequest adRequest) {

            if (PlaceAddEditActivity.this.mAdview != null) {
                mAdview.loadAd(adRequest);
            }
        }

        @Override
        protected AdRequest doInBackground(String... params) {
            // Requesting Test AdMob
            MobileAds.initialize(getApplicationContext(), params[0]);

            return new AdRequest.Builder()
                    .addTestDevice("366E9B0179BA824BAD82DA1FE1AFC8D8") // replace with your own device id
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // if you want to test on real device
                    // .setLocation(location) - location targeting may be appropriate for WhatPlace
                    .build();
        }
    }


    // Fetch 1 Photo of the Place from Google Places
    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private final int mHeight;
        private final int mWidth;

        public PhotoTask() {
            mHeight = PlaceAddEditActivity.IMAGE_HEIGHT;
            mWidth = PlaceAddEditActivity.IMAGE_WIDTH;
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         * The place id must be the first and the only parameter.
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);

                    // Attibution for the photo required by Google to be displayed if available
                    CharSequence attribution = photo.getAttributions();
                    Log.d(TAG, "Attribution: " + attribution.toString());

                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            if (attributedPhoto != null) {
                Log.d(TAG, "I HAVE A PHOTO FOR THIS PLACE!");
            } else {
                Log.d(TAG, "No photo available for Place");
            }
            return attributedPhoto;
        }

        class AttributedPhoto {
            public final CharSequence attribution;
            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }

// ------ end of PlaceAddEditActivity class ------
}


