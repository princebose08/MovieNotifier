package com.example.pbose.movienotifier.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pbose.exceptions.IntentNotFoundException;
import com.example.pbose.movienotifier.LocationSelectionActivity;
import com.example.pbose.movienotifier.R;
import com.example.pbose.pageNavigator.PageNavigationData;
import com.example.pbose.pageNavigator.PageNavigationFactory;
import com.example.pbose.pageNavigator.PageType;
import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.types.Request;
import com.example.pbose.uitype.IntentExtraType;

/**
 * Created by pbose on 4/3/16.
 */
public class HomeDisplayFragment extends Fragment {

    public HomeDisplayFragment(){
    }

    public static HomeDisplayFragment getInstance(){
        HomeDisplayFragment homeDisplayFragment = new HomeDisplayFragment();
        return homeDisplayFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_main, container, false);


        Button addNotifier = (Button) view.findViewById(R.id.add_notifier);

        addNotifier.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("here button clicked");

                try {
                    Request request = new Request();
                    Intent countrySelectionIntent = PageNavigationFactory.getNextIntent(new PageNavigationData(request), PageType.LANDING_PAGE, view.getContext());
                    startActivity(countrySelectionIntent);
                } catch (IntentNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }));

        return view;
    }
}
