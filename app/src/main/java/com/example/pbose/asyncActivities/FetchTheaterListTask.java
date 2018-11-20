package com.example.pbose.asyncActivities;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pbose.movienotifier.TheaterSelectionActivity;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Theater;
import com.example.pbose.uidata.MovieDataProvider;
import com.example.pbose.uidata.TheaterDataProvider;

import java.util.List;

/**
 * Created by pbose on 4/3/16.
 */
public class FetchTheaterListTask  extends AsyncTask<FetchTaskParams,Void,List<Theater>> {
    TheaterDataProvider theaterDataProvider;
    TheaterSelectionActivity.TheaterArrayAdapter theaterArrayAdapter;

    public FetchTheaterListTask(Context context,TheaterSelectionActivity.TheaterArrayAdapter theaterArrayAdapter){
        this.theaterDataProvider = new TheaterDataProvider(context);
        this.theaterArrayAdapter = theaterArrayAdapter;
    }

    @Override
    protected List<Theater> doInBackground(FetchTaskParams... params) {
        FetchTaskParams param = params[0];
        return theaterDataProvider.getAllTheatersForLocation(param.getLocation(),param.isForceRefresh());
    }

    @Override
    protected void onPostExecute(List<Theater> theaterList){
        /*System.out.println("Printing Theaters");

        for(Theater theater : theaterList){
            System.out.println(theater);
        }
*/
        theaterArrayAdapter.refreshTheaterList(theaterList);
    }
}
