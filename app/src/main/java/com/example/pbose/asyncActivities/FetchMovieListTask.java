package com.example.pbose.asyncActivities;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pbose.movienotifier.MovieSelectionActivity;
import com.example.pbose.types.Movie;
import com.example.pbose.uidata.MovieDataProvider;

import java.util.List;

/**
 * Created by pbose on 3/21/16.
 */
public class FetchMovieListTask extends AsyncTask<FetchTaskParams,Void,List<Movie>> {
    MovieDataProvider movieDataProvider;
    MovieSelectionActivity.MovieArrayAdapter movieArrayAdapter;

    public FetchMovieListTask(Context context,MovieSelectionActivity.MovieArrayAdapter movieArrayAdapter){
        this. movieDataProvider = new MovieDataProvider(context);
        this.movieArrayAdapter = movieArrayAdapter;
    }

    @Override
    protected List<Movie> doInBackground(FetchTaskParams... params) {
        FetchTaskParams param = params[0];
        return movieDataProvider.getAllMoviesForLocation(param.getLocation(),param.isForceRefresh());
    }

    @Override
    protected void onPostExecute(List<Movie> movieList){
        System.out.println("Printing Movies");

        for(Movie movie : movieList){
            System.out.println(movie);
        }
        movieArrayAdapter.refreshMovieList(movieList);

    }
}
