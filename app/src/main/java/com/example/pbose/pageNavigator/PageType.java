package com.example.pbose.pageNavigator;

import com.example.pbose.movienotifier.ConfirmationPageActivity;
import com.example.pbose.movienotifier.DateSelectionActivity;
import com.example.pbose.movienotifier.LandingPageActivity;
import com.example.pbose.movienotifier.LocationSelectionActivity;
import com.example.pbose.movienotifier.MovieSelectionActivity;
import com.example.pbose.movienotifier.TheaterSelectionActivity;

/**
 * Created by pbose on 4/10/16.
 */
public enum PageType {
    LANDING_PAGE(LandingPageActivity.class),
    LOCATION_SELECTION_PAGE(LocationSelectionActivity.class),
    MOVIE_SELECTION_PAGE(MovieSelectionActivity.class),
    THEATER_SELECTION_PAGE(TheaterSelectionActivity.class),
    DATE_SELECTION_PAGE(DateSelectionActivity.class),
    CONFIRMATION_PAGE(ConfirmationPageActivity.class);

    private Class pageClass;

    PageType(Class pageClass){
        this.pageClass = pageClass;
    }

    Class getPageClass(){
       return this.pageClass;
    }
}
