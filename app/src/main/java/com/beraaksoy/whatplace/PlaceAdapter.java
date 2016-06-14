package com.beraaksoy.whatplace;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beraaksoy.whatplace.activity.PlaceAddEditActivity;
import com.beraaksoy.whatplace.db.WhatPlaceContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beraaksoy on 5/16/16.
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private static final String TAG = "PlaceAdapter";

    private final LayoutInflater inflater;
    private List<Place> mPlaces;
    private final ContentResolver mContentResolver;
    private int bgColorIndex;
    private int[] colors;

    public PlaceAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        mPlaces = new ArrayList<>();
        mContentResolver = context.getContentResolver();
        colors = context.getResources().getIntArray(R.array.initial_colors);
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.place_card_view, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place place = mPlaces.get(position);
        holder.name.setText(place.getName());
        holder.address.setText(place.getAddress());

        // Set the place card background initial letter and background color
        holder.placeInitialView.setText(place.getName().substring(0, 1));
        setBgColorIndex(place); // need to set this up to pass into colors[] next line
        holder.placeInitialView.setBackgroundColor(colors[bgColorIndex]);
    }

    private void setBgColorIndex(Place place) {
        int placeId = place.get_id();
        Uri uri = WhatPlaceContract.Places.buildPlaceUri(String.valueOf(placeId));
        Log.d("Uri", "onBindViewHolder: " + uri);
        String[] projection = {WhatPlaceContract.PlaceColumns.PLACE_BGCOLOR};

        Cursor managedCursor =
                mContentResolver.query(
                        uri,
                        projection,    // Which columns to return.
                        null,          // WHERE clause.
                        null,          // WHERE clause value substitution
                        null);         // Sort order.

        int index = 0;
        if (managedCursor != null) {
            index = managedCursor.getColumnIndex(WhatPlaceContract.PlaceColumns.PLACE_BGCOLOR);
        }
        if (managedCursor != null) {
            while (managedCursor.moveToNext()) {
                bgColorIndex = managedCursor.getInt(index);
                Log.d(TAG, "Result: " + bgColorIndex);
            }
        } else {
            bgColorIndex = 1; // just give it a color for now
            Log.d(TAG, "Not able to receive color index");
        }
        if (managedCursor != null) {
            managedCursor.close();
        }
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public void setData(List<Place> places) {
        mPlaces = places;
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView address;
        final TextView phone;
        final TextView website;
        final TextView memo;
        final SimpleDraweeView photoView;
        final TextView placeInitialView;

        public PlaceViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.place_name);
            address = (TextView) itemView.findViewById(R.id.place_address);
            phone = (TextView) itemView.findViewById(R.id.place_phone);
            website = (TextView) itemView.findViewById(R.id.place_website);
            memo = (TextView) itemView.findViewById(R.id.place_memo);
            photoView = (SimpleDraweeView) itemView.findViewById(R.id.place_photo_view);
            placeInitialView = (TextView) itemView.findViewById(R.id.place_initial);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = PlaceAddEditActivity.getActionIntent(context, mPlaces.get(getAdapterPosition()), PlaceAddEditActivity.ACTION_EDIT);
                    context.startActivity(intent);
                }
            });

            // DELETE Place -
            Button button = (Button) itemView.findViewById(R.id.place_delete_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDeleteAlertDialog();
                }
            });

            // FAVORITE (Future Release) - Show Snackbar message when place is added to favorite
            ImageButton favoriteImageButton =
                    (ImageButton) itemView.findViewById(R.id.add_favorite_button);
            favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, R.string.added_favorites_message,
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }

        // Make sure the user wants to delete a Place
        private void confirmDeleteAlertDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
            alertDialog.setTitle("Confirm Delete");
            alertDialog.setMessage("Are you sure you want to delete this Place?");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int placeId = mPlaces.get(getAdapterPosition()).get_id();
                            mPlaces.remove(mPlaces.get(getAdapterPosition()));
                            Uri uri = WhatPlaceContract.Places.buildPlaceUri(String.valueOf(placeId));
                            mContentResolver.delete(uri, null, null);
                            notifyItemRemoved(getAdapterPosition());
                            // Show Snackbar message when place is deleted
                            Snackbar.make(itemView, R.string.deleted_place_message,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
            );
            alertDialog.show();
        }
    }
}
