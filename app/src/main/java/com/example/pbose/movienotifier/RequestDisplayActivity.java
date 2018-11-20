package com.example.pbose.movienotifier;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.types.Request;
import com.example.pbose.types.Status;
import com.example.pbose.uitype.IntentExtraType;
import com.example.pbose.uitype.LocationData;
import com.example.pbose.uitype.RequestDisplayUtil;

import java.util.List;

public class RequestDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_request_display);
        Status status = (Status) getIntent().getSerializableExtra(IntentExtraType.REQUEST_DISPLAY_STATUS);
        if(null == status){
            status = Status.SCHEDULED; //default
        }

        final MovieDataStorage movieDataStorage = SQLiteMovieDataStorage.getInstance(getApplicationContext());

        List<Request> scheduledRequests = movieDataStorage.getRequestsByStatus(status);
        final RequestArrayAdapter locationArrayAdapter = new RequestArrayAdapter(this,R.layout.content_individual_request,scheduledRequests);
        final ListView requestListView = (ListView) findViewById(R.id.requestListView);

        requestListView.setAdapter(locationArrayAdapter);

    }

    private class RequestArrayAdapter extends ArrayAdapter<Request> {
        private Context context;
        List<Request> requestList;
        int textViewResourceId;

        public RequestArrayAdapter(Context context, int textViewResourceId, List<Request> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.requestList = objects;
            this.textViewResourceId = textViewResourceId;
        }

        @Override
        public int getCount() {
            return requestList.size();
        }

        @Override
        public Request getItem(int position) {
            return requestList.get(position);
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

            Request request = requestList.get(position);
            RequestDisplayUtil.populateRequestData(view, request);
            return view;
        }
    }
}
