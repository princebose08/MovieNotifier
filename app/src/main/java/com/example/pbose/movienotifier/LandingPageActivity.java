package com.example.pbose.movienotifier;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.pbose.exceptions.IntentNotFoundException;
import com.example.pbose.movienotifier.fragments.HomeDisplayFragment;
import com.example.pbose.movienotifier.fragments.RequestDisplayFragment;
import com.example.pbose.movienotifier.fragments.settings.SettingsFragment;
import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageType;
import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.types.Request;
import com.example.pbose.types.Status;
import com.example.pbose.uitype.IntentExtraType;

public class LandingPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RequestDisplayFragment.OnFragmentInteractionListener {

    MovieDataStorage movieDataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Integer navItemId = (Integer) getIntent().getSerializableExtra(IntentExtraType.SELECTED_NAV_ITEM);
        if(navItemId == null){
            navItemId = new Integer(R.id.home);
        }

        movieDataStorage = SQLiteMovieDataStorage.getInstance(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(navItemId);
        onNavigationItemSelected(navigationView.getMenu().findItem(navItemId));
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        ImageView mImageView = (ImageView)headerLayout.findViewById(R.id.imageView);
        mImageView.setImageResource(R.mipmap.ic_logo);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            HomeDisplayFragment homeDisplayFragment = HomeDisplayFragment.getInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, homeDisplayFragment);
            transaction.commit();
        } else if (id == R.id.scheduled_notification) {
            renderRequestDisplayFragment(Status.SCHEDULED);
        } else if (id == R.id.completed_notification) {
            renderRequestDisplayFragment(Status.DONE);
        } else if (id == R.id.failed_notification) {
            renderRequestDisplayFragment(Status.FAILED);
        } else if (id == R.id.setting) {
            SettingsFragment settingsFragment = SettingsFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, settingsFragment);
            transaction.commit();
        } else if (id == R.id.start_notification_scheduling) {
            try {
                Request request = new Request();
                Intent countrySelectionIntent = PageNavigationFactory.getNextIntent(new PageNavigationData(request), PageType.LANDING_PAGE, getApplicationContext());
                startActivity(countrySelectionIntent);
            } catch (IntentNotFoundException e) {
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void renderRequestDisplayFragment(Status status){
        RequestDisplayFragment requestDisplayFragment = RequestDisplayFragment.newInstance(status,movieDataStorage);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, requestDisplayFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
