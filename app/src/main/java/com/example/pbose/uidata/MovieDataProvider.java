package com.example.pbose.uidata;

import android.content.Context;

import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.persistence.SQLiteMovieDataStorage;
import com.example.pbose.scrapper.WebsiteScrapperFactory;
import com.example.pbose.scrapper.WebsiteScrapperInterface;
import com.example.pbose.types.Location;
import com.example.pbose.types.Movie;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 3/21/16.
 */
public class MovieDataProvider {

    private static int REFRESH_DURATION_IN_HOUR = 24;
    private MovieDataStorage movieDataStorage;
    private WebsiteScrapperInterface websiteScrapperInterface;

    public MovieDataProvider(Context context){
        movieDataStorage = SQLiteMovieDataStorage.getInstance(context);
        websiteScrapperInterface = WebsiteScrapperFactory.getInstance().getScrapper();
    }
    public List<Movie> getAllMoviesForLocation(Location location){
        return getAllMoviesForLocation(location,false);
    }

    public List<Movie> getAllMoviesForLocation(Location location,boolean isForceRefresh){
        //Fetching from DB
        List<Movie> movieList = movieDataStorage.getMovies(location);
        Date lastRefreshDate = findLastRefreshDate(movieList);

        if(lastRefreshDate == null || isForceRefresh){
            return refreshAndReturnMovies(location);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastRefreshDate);
        calendar.add(Calendar.HOUR_OF_DAY, REFRESH_DURATION_IN_HOUR);
        Date refreshDueDate = calendar.getTime();
        Date currentDate = new Date();

        if(currentDate.after(refreshDueDate)){
            return refreshAndReturnMovies(location);
        }
        return movieList;
    }
    private List<Movie> refreshAndReturnMovies(Location location){
        refreshDBData(location);
        return movieDataStorage.getMovies(location);
    }

    private void refreshDBData(Location location){
        //Fetch movie data from Website
        List<Movie> websiteMovieList = websiteScrapperInterface.getAllMovies(location);
        //remove existing data for the location
        movieDataStorage.deleteMoviesForLocation(location);
        //Update them in DB
        movieDataStorage.updateOrInsertMovies(websiteMovieList);
    }

    private Date findLastRefreshDate(List<Movie> movieList){
        Date returnedDate = null;
        for(Movie movie : movieList){
            Date lastRefreshDate = movie.getLastRefreshDate();
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
