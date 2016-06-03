package com.beraaksoy.whatplace.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beraaksoy.whatplace.PlaceCardContentFragment;
import com.beraaksoy.whatplace.PlaceType;
import com.beraaksoy.whatplace.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by beraaksoy on 5/01/16.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PLACEPICKER_REQUEST_CODE = 1453;
    public static final String PLACE = "place";
    public static final String PLACE_ID = "place_id";
    public static final String PLACE_NAME = "place_name";
    public static final String PLACE_ADDRESS = "place_address";
    public static final String PLACE_PHONE = "place_phone";
    public static final String PLACE_MEMO = "place_memo";
    public static final String PLACE_WEBSITE = "place_website";
    public static final String PLACE_PHOTO = "place_photo";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.fab_add_place)
    FloatingActionButton fabAddPlace;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.drawer)
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setLogo(R.drawable.app_logo);
        }

        // Fab to search a nearby Place and add one
        FloatingActionButton add_place_fab = (FloatingActionButton) findViewById(R.id.fab_add_place);
        assert add_place_fab != null;
        add_place_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPlace();
                Toast.makeText(MainActivity.this, "Fetching list of nearby places", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Intent intent;
        try {
            intent = builder.build(this);
            startActivityForResult(intent, PLACEPICKER_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACEPICKER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getApplicationContext(), data);
                String name = place.getName().toString();
                String address = place.getAddress().toString();
                String id = place.getId();
                String phone = "Phone unknown";
                if (place.getPhoneNumber() != null) {
                    phone = place.getPhoneNumber().toString();
                }
                String website = "No website";
                if (place.getWebsiteUri() != null) {
                    website = place.getWebsiteUri().toString();
                }
                Log.d(TAG, "Place: ************* DATA ****************");
                Log.d(TAG, "Place: " + "Name: " + name);
                Log.d(TAG, "Place: " + "Address: " + address);
                Log.d(TAG, "Place: " + "Phone: " + phone);
                Log.d(TAG, "Place: " + "Website: " + website);

                // Fetch Place Types (FLORIST, BAKERY, ESTABLISHMENT, etc.)
                List<String> placeTypeString = PlaceType.getPlaceTypesString(place.getPlaceTypes());
                if (placeTypeString != null) {
                    for (int i = 0; i < placeTypeString.size(); i++) {
                        Log.d(TAG, "Place: " + "Type: " + placeTypeString.get(i));
                    }
                }
                com.beraaksoy.whatplace.Place place1 = new com.beraaksoy.whatplace.Place();
                place1.setName(name);
                place1.setAddress(address);
                place1.setPhone(phone);
                place1.setWebsite(website);
                Intent intent = PlaceAddEditActivity.getActionIntent(this, place1, PlaceAddEditActivity.ACTION_ADD);
                intent.putExtra(PLACE_ID, id);
                startActivity(intent);
            }
        }
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new PlaceCardContentFragment());
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add("Card");
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.search_places) {
            return true;
        }
        // else if (id == android.R.id.home) {
        //    mDrawerLayout.openDrawer(GravityCompat.START);
        //}
        return super.onOptionsItemSelected(item);
    }
}
