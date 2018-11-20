package com.example.pbose.movienotifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageNavigator;
import com.example.pbose.pageNavigator.PageType;
import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.types.Request;
import com.example.pbose.uitype.IntentExtraType;
import com.example.pbose.uitype.RequestDisplayUtil;
import com.example.pbose.util.BulletTextUtil;
import com.example.pbose.util.TheaterUtil;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ConfirmationPageActivity extends RequestSchedulingBaseActivity {

    PageNavigationData pageNavigationData;
    Button scheduleNotificationButton;
    Request request;

    static Map<Integer,PageType> EDIT_BUTTON_PAGE_TYPE_MAP = new HashMap<Integer,PageType>();
    static {
        EDIT_BUTTON_PAGE_TYPE_MAP.put(R.id.locationEdit, PageType.LOCATION_SELECTION_PAGE);
        EDIT_BUTTON_PAGE_TYPE_MAP.put(R.id.movieEdit, PageType.MOVIE_SELECTION_PAGE);
        EDIT_BUTTON_PAGE_TYPE_MAP.put(R.id.theaterEdit, PageType.THEATER_SELECTION_PAGE);
        EDIT_BUTTON_PAGE_TYPE_MAP.put(R.id.targetDateEdit, PageType.DATE_SELECTION_PAGE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_page);
        pageNavigationData = (PageNavigationData) getIntent().getSerializableExtra(IntentExtraType.PAGE_NAVIGATION_DATA);
        request = pageNavigationData.getRequest();
        ViewGroup view = (ViewGroup)getWindow().getDecorView();

        final MovieDataStorage movieDataStorage = SQLiteMovieDataStorage.getInstance(getApplicationContext());
        RequestDisplayUtil.populateRequestData(view, request);
        final Activity activity = this;

        /*TextView locationTextView = (TextView) findViewById(R.id.locationConfirmationText);
        locationTextView.setText(request.getLocation().getDisplayName());

        TextView movieTextView = (TextView) findViewById(R.id.movieConfirmationText);
        movieTextView.setText(request.getMovie().getName());

        CharSequence bulletedList = new BulletTextUtil().makeBulletList(2, TheaterUtil.getTheaterNameList(request.getTheaterList()));
        TextView theaterTextView = (TextView) findViewById(R.id.theaterConfirmationText);
        theaterTextView.setText(bulletedList);

        TextView dateConfirmationTextView = (TextView) findViewById(R.id.dateConfirmationText);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d");
        String displayDate = simpleDateFormat.format(request.getSearchDate());
        dateConfirmationTextView.setText(displayDate);*/

        scheduleNotificationButton = (Button) findViewById(R.id.notificationScheduleButton);

        scheduleNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Request : "+request);
                boolean status = movieDataStorage.addRequest(request);
                System.out.println(status);
                PageNavigator.goToScheduledNotificationPage(getApplicationContext(),activity);
            }
        });

        for(Integer id : EDIT_BUTTON_PAGE_TYPE_MAP.keySet()){
            ImageButton imageButton = (ImageButton)findViewById(id);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PageType targetPageType = EDIT_BUTTON_PAGE_TYPE_MAP.get(v.getId());
                    Intent intent = PageNavigationFactory.getIntent(getPageNavigationData(),targetPageType,getPageType(),getApplicationContext(),true);
                    startActivity(intent);
                }
            });
        }


        populateData();
    }

    @Override
    public void handleBackClick(View v) {
        navigateToBackPage();
    }

    @Override
    public void populateData() {
        if(request.getId() > 0){
            // update request
            scheduleNotificationButton.setText("Update Notification");
        }
    }

    @Override
    public void handleNextIconClick() {
        //No next Icon
    }

    @Override
    public PageNavigationData getPageNavigationData() {
        return this.pageNavigationData;
    }

    @Override
    public PageType getPageType() {
        return PageType.CONFIRMATION_PAGE;
    }

    @Override
    public void startupSetup() {
        hideNextButton();
    }
}
