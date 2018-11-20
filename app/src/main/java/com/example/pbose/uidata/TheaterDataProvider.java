package com.example.pbose.uidata;

import android.content.Context;

import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.scrapper.WebsiteScrapperFactory;
import com.example.pbose.scrapper.WebsiteScrapperInterface;
import com.example.pbose.types.Location;
import com.example.pbose.types.Theater;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 4/3/16.
 */
public class TheaterDataProvider {
    private static int REFRESH_DURATION_IN_HOUR = 24;
    private MovieDataStorage movieDataStorage;
    private WebsiteScrapperInterface websiteScrapperInterface;

    public TheaterDataProvider(Context context){
        movieDataStorage = SQLiteMovieDataStorage.getInstance(context);
        websiteScrapperInterface = WebsiteScrapperFactory.getInstance().getScrapper();
    }
    public List<Theater> getAllTheatersForLocation(Location location){
        return getAllTheatersForLocation(location, false);
    }

    public List<Theater> getAllTheatersForLocation(Location location,boolean isForceRefresh){
        //Fetching from DB
        List<Theater> theaterList = movieDataStorage.getTheaters(location);
        Date lastRefreshDate = findLastRefreshDate(theaterList);

        if(lastRefreshDate == null || isForceRefresh){
            return refreshAndReturnTheaters(location);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastRefreshDate);
        calendar.add(Calendar.HOUR_OF_DAY, REFRESH_DURATION_IN_HOUR);
        Date refreshDueDate = calendar.getTime();
        Date currentDate = new Date();

        if(currentDate.after(refreshDueDate)){
            return refreshAndReturnTheaters(location);
        }
        return theaterList;
    }
    private List<Theater> refreshAndReturnTheaters(Location location){
        refreshDBData(location);
        return movieDataStorage.getTheaters(location);
    }

    private void refreshDBData(Location location){
        //Fetch movie data from Website
        List<Theater> websiteTheaterList = websiteScrapperInterface.getAllTheaters(location);
        //remove existing data for the location
        movieDataStorage.deleteTheatersForLocation(location);
        //Update them in DB
        movieDataStorage.updateOrInsertTheaters(websiteTheaterList);
    }

    private Date findLastRefreshDate(List<Theater> theaterList){
        Date returnedDate = null;
        for(Theater theater : theaterList){
            Date lastRefreshDate = theater.getLastRefreshDate();
            if(null == returnedDate){
                returnedDate = lastRefreshDate;
                continue;
            }
            if(lastRefreshDate != null && lastRefreshDate.before(returnedDate)){
                returnedDate = lastRefreshDate;
            }
        }

        return returnedDate;
    }

}
