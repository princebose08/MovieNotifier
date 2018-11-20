package com.example.pbose.movienotifier;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.pbose.exceptions.IntentNotFoundException;
import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageType;

/**
 * Created by pbose on 4/10/16.
 */
public abstract class RequestSchedulingBaseActivity extends AppCompatActivity {
    Toolbar myToolbar;


    public abstract void handleNextIconClick();

    public abstract PageNavigationData getPageNavigationData();

    public abstract PageType getPageType();

    public abstract void startupSetup();

    public abstract void handleBackClick(View v);

    public abstract void populateData();

    public void handleHomeIconClick() {
        DialogFragment newFragment = new HomePageConfirmationDialogFragment();
        newFragment.show(getSupportFragmentManager(), "home confirmation");
    }


    public void startNextIntent() {
        try {
            Intent intent = PageNavigationFactory.getNextIntent(getPageNavigationData(), getPageType(), getApplicationContext());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
        } catch (IntentNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void hideNextButton() {
        hideMenuIcon(R.id.next_icon);
    }

    protected void hideHomeButton() {
        hideMenuIcon(R.id.home_icon);
    }

    private void hideMenuIcon(int id) {
        Menu menu = myToolbar.getMenu();
        MenuItem menuItem = menu.findItem(id);
        menuItem.setVisible(false);
    }

    public void enableToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleBackClick(v);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        startupSetup();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home_icon: {
                handleHomeIconClick();
                break;
            }
            case R.id.next_icon: {
                handleNextIconClick();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(int id) {
        super.setContentView(id);
        enableToolbar();
    }

    public void navigateToBackPage() {
        try {
            Intent intent = PageNavigationFactory.getPreviousIntent(getPageNavigationData(), getPageType(), getApplicationContext());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        } catch (IntentNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ValidFragment")
    class HomePageConfirmationDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.home_alert_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = PageNavigationFactory.getIntent(getPageNavigationData(), PageType.LANDING_PAGE, getPageType(), getApplicationContext());
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            return builder.create();
        }
    }
}