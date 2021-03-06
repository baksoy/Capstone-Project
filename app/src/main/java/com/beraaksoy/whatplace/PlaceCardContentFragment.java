package com.beraaksoy.whatplace;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beraaksoy.whatplace.db.PlaceListLoader;
import com.beraaksoy.whatplace.db.WhatPlaceContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * UI for the view with Cards.
 */
public class PlaceCardContentFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Place>> {

    private static final String TAG = PlaceCardContentFragment.class.getSimpleName();
    @BindView(R.id.my_recycler_view)
    private RecyclerView myRecyclerView;
    private ContentResolver mContentResolver;
    private PlaceAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContentResolver = getActivity().getContentResolver();
        mAdapter = new PlaceAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(R.id.place_loader_id, null, this);
        // if you want to get a hold of the loader object uncomment next line
        // mLoader = (PlaceListLoader) getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ButterKnife.bind(this, mRecyclerView);
        return mRecyclerView;
    }

    // Instantiate and return a loader with a given ID
    @Override
    public Loader<List<Place>> onCreateLoader(int id, Bundle args) {
        mContentResolver = getActivity().getContentResolver();
        Uri table = WhatPlaceContract.URI_TABLE;
        return new PlaceListLoader(getActivity(), table, mContentResolver);
    }

    // Called when our loader has finished its load
    @Override
    public void onLoadFinished(Loader<List<Place>> loader, List<Place> places) {
        mAdapter.notifyItemInserted(mAdapter.getItemCount());
        mAdapter.setData(places);
        //mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onLoaderReset(Loader<List<Place>> loader) {
    }
}
