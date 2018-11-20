package com.example.pbose.movienotifier.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.pbose.movienotifier.R;
import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageType;
import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.types.Request;
import com.example.pbose.types.Status;
import com.example.pbose.uitype.IntentExtraType;
import com.example.pbose.uitype.LocationData;
import com.example.pbose.uitype.RequestDisplayUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestDisplayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestDisplayFragment extends Fragment {
    private static final String STATUS = "STATUS";
    View requestDisplayView;
    RequestArrayAdapter requestArrayAdapter;
    private static final Map<Status,String> TITLE_MAP;
    static
    {
        TITLE_MAP = new HashMap<Status, String>();
        TITLE_MAP.put(Status.DONE, "Completed Notifications");
        TITLE_MAP.put(Status.SCHEDULED, "Scheduled Notifications");
        TITLE_MAP.put(Status.FAILED, "Failed Notifications");
    }

    private Status status;

    private OnFragmentInteractionListener mListener;
    private static MovieDataStorage MOVIE_DATA_STORAGE;

    public RequestDisplayFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestDisplayFragment.
     */
    public static RequestDisplayFragment newInstance(Status status,MovieDataStorage movieDataStorage) {
        RequestDisplayFragment fragment = new RequestDisplayFragment();
        MOVIE_DATA_STORAGE = movieDataStorage;
        Bundle args = new Bundle();
        args.putSerializable(STATUS,status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = (Status)getArguments().getSerializable(STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestDisplayView = inflater.inflate(R.layout.activity_active_request_display, container, false);

        if(null == status){
            status = Status.SCHEDULED; //default
        }

        TextView requestDisplayLabel = (TextView) requestDisplayView.findViewById(R.id.requestDisplayLabel);
        requestDisplayLabel.setText(TITLE_MAP.get(status));

        List<Request> scheduledRequests = MOVIE_DATA_STORAGE.getRequestsByStatus(status);
        Collections.sort(scheduledRequests,Request.CompletionDateComparator);
        requestArrayAdapter = new RequestArrayAdapter(requestDisplayView.getContext(),R.layout.content_individual_request,scheduledRequests);
        final ListView requestListView = (ListView) requestDisplayView.findViewById(R.id.requestListView);

        requestListView.setAdapter(requestArrayAdapter);

        return requestDisplayView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            final View view = inflater.inflate(textViewResourceId,parent,false);

            Request request = requestList.get(position);
            RequestDisplayUtil.populateRequestData(view, request);

            handleMenuPopup(view, request);
            return view;
        }

        public void removeRequest(Request requestToRemove){
            this.requestList.remove(requestToRemove);
            notifyDataSetChanged();
        }

        private void handleMenuPopup(final View view,final Request request){
            ImageButton imageButton = (ImageButton) view.findViewById(R.id.menuImageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Menu clicked");
                    PopupMenu popup = new PopupMenu(view.getContext(), v);

                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.request_edit_menu, popup.getMenu());
                    if(status == Status.SCHEDULED) {
                        // Only for scheduled have edit option
                        MenuItem editItem = popup.getMenu().findItem(R.id.edit_request);
                        editItem.setVisible(true);
                    }
                    popup.show();



                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.edit_request:
                                    System.out.println("Edit Clicked");
                                    Intent intent = PageNavigationFactory.getIntent(new PageNavigationData(request), PageType.LOCATION_SELECTION_PAGE,PageType.LANDING_PAGE,view.getContext());
                                    startActivity(intent);
                                    return true;
                                case R.id.delete_request:
                                    System.out.println("Delete clicked");
                                    MOVIE_DATA_STORAGE.deleteRequest(request);
                                    requestArrayAdapter.removeRequest(request);
                                    return true;
                                default:
                                    return false;

                            }
                        }
                    });
                }
            });

        }
    }
}
