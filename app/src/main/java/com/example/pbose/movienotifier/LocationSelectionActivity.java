package com.example.pbose.movienotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pbose.exceptions.IntentNotFoundException;
import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageType;
import com.example.pbose.types.Location;
import com.example.pbose.types.Request;
import com.example.pbose.uitype.IntentExtraType;
import com.example.pbose.uitype.LocationData;

import java.util.ArrayList;
import java.util.List;

public class LocationSelectionActivity extends RequestSchedulingBaseActivity {

    Request request;
    PageNavigationData pageNavigationData;
    EditText locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selection);
        pageNavigationData = (PageNavigationData) getIntent().getSerializableExtra(IntentExtraType.PAGE_NAVIGATION_DATA);
        request = pageNavigationData.getRequest();

        // Get the elements
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        final ListView listview = (ListView) findViewById(R.id.cityListView);


        final List<LocationData> locationDataList = getDefaultLocationData();
        final LocationArrayAdapter locationArrayAdapter = new LocationArrayAdapter(this,R.layout.content_individual_country,locationDataList);

        listview.setAdapter(locationArrayAdapter);

        locationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<LocationData> filteredLocationData = getFilteredLocationData(locationDataList, s.toString());
                locationArrayAdapter.updateLocationDataList(filteredLocationData);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationData locationDataSelected = (LocationData) listview.getItemAtPosition(position);
                Location location = Location.valueOf(locationDataSelected.getEnumName());
                request.setLocation(location);
                startNextIntent();

            }
        });

        populateData();

    }

    private List<LocationData> getFilteredLocationData(List<LocationData> locationDataList,String hint){

        List<LocationData> filteredLocationData = new ArrayList<LocationData>();
        for(LocationData locationData : locationDataList){
            if(locationData.getDisplayName().toLowerCase().contains(hint.toLowerCase())){
                filteredLocationData.add(locationData);
            }
        }
        return filteredLocationData;
    }

    private List<LocationData> getDefaultLocationData(){
        List<LocationData> locationDataList = new ArrayList<LocationData>();
        Location[] locations = Location.values();
        for(Location location : locations){
            locationDataList.add(new LocationData(location.getDisplayName(),location.name()));
        }
        return locationDataList;
    }

    @Override
    public void handleBackClick(View v) {
        handleHomeIconClick();
    }

    @Override
    public void populateData() {
        Location location = request.getLocation();
        if(null != location){
            locationEditText.setText(location.getDisplayName());
            locationEditText.setSelection(locationEditText.getText().length());

        }
    }

    @Override
    public void handleNextIconClick() {
        // No next button
    }

    @Override
    public PageNavigationData getPageNavigationData() {
        return this.pageNavigationData;
    }

    @Override
    public PageType getPageType() {
        return PageType.LOCATION_SELECTION_PAGE;
    }

    @Override
    public void startupSetup() {
        hideNextButton();
    }

    private class LocationArrayAdapter extends ArrayAdapter<LocationData> {
        private Context context;
        List<LocationData> locationDataList;
        int textViewResourceId;

        public LocationArrayAdapter(Context context, int textViewResourceId, List<LocationData> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.locationDataList = objects;
            this.textViewResourceId = textViewResourceId;
        }

        @Override
        public int getCount() {
            return locationDataList.size();
        }

        @Override
        public LocationData getItem(int position) {
            return locationDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(textViewResourceId,parent,false);
            TextView cityNameView = (TextView)view.findViewById(R.id.cityName);
            LocationData locationData = locationDataList.get(position);
            cityNameView.setText(locationData.getDisplayName());
            return view;
        }

        public void updateLocationDataList(List<LocationData> locationDataList){
            this.locationDataList = locationDataList;
            notifyDataSetChanged();
        }
    }

}
