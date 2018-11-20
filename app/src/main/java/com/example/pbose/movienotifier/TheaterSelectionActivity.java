package com.example.pbose.movienotifier;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pbose.asyncActivities.FetchMovieListTask;
import com.example.pbose.asyncActivities.FetchTaskParams;
import com.example.pbose.asyncActivities.FetchTheaterListTask;
import com.example.pbose.exceptions.IntentNotFoundException;
import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageType;
import com.example.pbose.types.Location;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Request;
import com.example.pbose.types.Theater;
import com.example.pbose.uidata.MovieDataProvider;
import com.example.pbose.uidata.TheaterDataProvider;
import com.example.pbose.uitype.IntentExtraType;
import com.example.pbose.uitype.MovieFilter;
import com.example.pbose.uitype.MovieFilterInputData;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TheaterSelectionActivity extends RequestSchedulingBaseActivity {
    public Request request;
    PageNavigationData pageNavigationData;
    TheaterDataProvider theaterDataProvider;
    FetchTheaterListTask fetchTheaterListTask;
    ProgressBar loadingImage;
    String hintString;
    EditText theaterEditText;
    TheaterArrayAdapter theaterArrayAdapter;

    private void renderMainPage(){
        setContentView(R.layout.activity_theater_selection);
        theaterEditText = (EditText) findViewById(R.id.theaterSelectionEditText);
        if(StringUtils.isNotBlank(hintString)){
            theaterEditText.setText(hintString);
        }

        this.theaterDataProvider = new TheaterDataProvider(getApplicationContext());
        List<Theater> theaterList = new ArrayList<Theater>();

        List<Theater> filteredRequestTheaterList = filterTheaterForLocation(request.getTheaterList(),request.getLocation());


        theaterArrayAdapter = new TheaterArrayAdapter(this,R.layout.content_individual_theater,theaterList,filteredRequestTheaterList);
        final ListView theaterListView = (ListView) findViewById(R.id.theaterSelectionListView);

        theaterListView.setAdapter(theaterArrayAdapter);

        fetchTheaterListTask = new FetchTheaterListTask(getApplicationContext(),theaterArrayAdapter);
        fetchTheaterListTask.execute(new FetchTaskParams(request.getLocation(),false));
        loadingImage = (ProgressBar)findViewById(R.id.theaterSelectionProgressBar);
        loadingImage.setVisibility(View.VISIBLE);

        setMainPageActions();
    }

    private void setMainPageActions(){
        theaterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hintString = s.toString();
                theaterArrayAdapter.updateTheaterList(theaterArrayAdapter.originalTheaterList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNavigationData = (PageNavigationData) getIntent().getSerializableExtra(IntentExtraType.PAGE_NAVIGATION_DATA);
        request = pageNavigationData.getRequest();
        renderMainPage();
    }

    @Override
    public void handleBackClick(View v) {
        navigateToBackPage();
    }

    @Override
    public void populateData() {
       // this is already taken care in ArrayAdapter constructor
    }

    @Override
    public void handleNextIconClick() {
        System.out.println("Theaters selected" + theaterArrayAdapter.selectedTheaters);
        request.setTheater(new ArrayList<Theater>(theaterArrayAdapter.selectedTheaters));
        startNextIntent();
    }

    @Override
    public PageNavigationData getPageNavigationData() {
        return this.pageNavigationData;
    }

    @Override
    public PageType getPageType() {
        return PageType.THEATER_SELECTION_PAGE;
    }

    @Override
    public void startupSetup() {
        // show all the icons
    }

    private List<Theater> filterTheaterForLocation(List<Theater> theaterList,Location location){
        List<Theater> filteredTheaterList = new ArrayList<Theater>();
        if(null != theaterList) {
            for (Theater theater : theaterList) {
                if (location == theater.getLocation()) {
                    filteredTheaterList.add(theater);
                }
            }
        }
        return filteredTheaterList;
    }


    public class TheaterArrayAdapter extends ArrayAdapter<Theater> {
        List<Theater> theaterList;
        List<Theater> originalTheaterList;
        Set<Theater> selectedTheaters;
        Context context;
        int resource;

        public TheaterArrayAdapter(Context context, int resource, List<Theater> movieList,List<Theater> selectedTheaterList) {
            super(context, resource, movieList);
            this.theaterList = movieList;
            this.originalTheaterList = movieList;
            this.context = context;
            this.resource = resource;
            System.out.println("In const : selected theaters"+selectedTheaterList);
            if(null == selectedTheaterList) {
                this.selectedTheaters = new HashSet<Theater>();
            }else{
                this.selectedTheaters = new HashSet<Theater>(selectedTheaterList);
            }
        }

        @Override
        public int getCount() {
            return theaterList.size();
        }

        @Override
        public Theater getItem(int position) {
            return theaterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(resource,parent,false);
            CheckBox theaterCheckBox = (CheckBox)view.findViewById(R.id.theaterCheckBox);


            final Theater theater = theaterList.get(position);
            theaterCheckBox.setText(theater.getName());

            //System.out.println("***** selected Theaters - "+selectedTheaters);
            //System.out.println("#### current theater - "+theater);
            if(selectedTheaters.contains(theater)){
                System.out.println("Setting checkbox true for"+theater);
                theaterCheckBox.setChecked(true);
            }

            theaterCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    if (checkBox.isChecked()) {
                        selectedTheaters.add(theater);
                    } else {
                        selectedTheaters.remove(theater);
                    }
                    request.setTheater(new ArrayList<Theater>(selectedTheaters));
                    System.out.println(selectedTheaters);
                }
            });
            return view;
        }

        public void refreshTheaterList(List<Theater> theaterList){
            this.originalTheaterList = theaterList;
            updateTheaterList(theaterList);
        }

        public void updateTheaterList(List<Theater> theaterList){
            this.theaterList = getFilteredTheatersForHintString(theaterList, hintString);
            notifyDataSetChanged();
            loadingImage.setVisibility(View.GONE);
        }

        private List<Theater> getFilteredTheatersForHintString(List<Theater> theaterList,String hintString){
            if(StringUtil.isBlank(hintString)){
                return theaterList;
            }
            List<Theater> filteredTheaterList = new ArrayList<Theater>();
            for(Theater theater : theaterList){
                if(theater.getName().toLowerCase().contains(hintString.toLowerCase())){
                    filteredTheaterList.add(theater);
                }
            }
            return filteredTheaterList;
        }

    }

}
