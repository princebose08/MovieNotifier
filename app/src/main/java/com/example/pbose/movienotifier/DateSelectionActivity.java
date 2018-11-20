package com.example.pbose.movienotifier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;

import com.example.pbose.exceptions.IntentNotFoundException;
import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageType;
import com.example.pbose.types.Request;
import com.example.pbose.uitype.IntentExtraType;

import java.util.Calendar;
import java.util.Date;

public class DateSelectionActivity extends RequestSchedulingBaseActivity {

    Request request;
    PageNavigationData pageNavigationData;
    DatePicker datePicker;

    private DatePicker.OnDateChangedListener dateSetListener = new DatePicker.OnDateChangedListener() {

        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
            Date selectedDate = getDateFromDatePicker(datePicker);
            request.setSearchDate(selectedDate);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_selection);
        pageNavigationData = (PageNavigationData) getIntent().getSerializableExtra(IntentExtraType.PAGE_NAVIGATION_DATA);
        request = pageNavigationData.getRequest();

        datePicker = (DatePicker)findViewById(R.id.datePicker);
        Calendar calMin = Calendar.getInstance();
        calMin.add(Calendar.HOUR,-10);

        datePicker.setMinDate(calMin.getTimeInMillis());

        Calendar calMax = Calendar.getInstance();
        calMax.add(Calendar.MONTH, 3);
        datePicker.setMaxDate(calMax.getTimeInMillis());

        populateData();
    }

    private Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        return calendar.getTime();
    }

    @Override
    public void handleBackClick(View v) {
        navigateToBackPage();
    }

    @Override
    public void populateData() {
        Calendar calendar = Calendar.getInstance();
        Date searchDate = request.getSearchDate();
        if(null != searchDate) {
            calendar.setTime(searchDate);
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), dateSetListener);
    }

    @Override
    public void handleNextIconClick() {
        Date selectedDate = getDateFromDatePicker(datePicker);
        request.setSearchDate(selectedDate);
        startNextIntent();
    }

    @Override
    public PageNavigationData getPageNavigationData() {
        return this.pageNavigationData;
    }

    @Override
    public PageType getPageType() {
        return PageType.DATE_SELECTION_PAGE;
    }

    @Override
    public void startupSetup() {
        // show all icons
    }
}
