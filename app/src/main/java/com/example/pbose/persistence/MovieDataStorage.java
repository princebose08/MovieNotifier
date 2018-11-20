package com.example.pbose.persistence;

import com.example.pbose.exceptions.DataNotFoundException;
import com.example.pbose.types.Location;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Request;
import com.example.pbose.types.Status;
import com.example.pbose.types.Theater;

import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 3/19/16.
 */
public interface MovieDataStorage {

    public boolean addRequest(Request request);

    public List<Request> getRequestsByStatus(Status status);

    public List<Request> getRequests();

    public void updateRequests(List<Request> requests);

    public List<Theater> getTheaters(Location location);

    public Theater getTheaterById(String theaterId,Location localtion) throws DataNotFoundException;

    public List<Movie> getMovies(Location location);

    public Movie getMovieById(String movieId,Location location) throws DataNotFoundException;

    public void updateOrInsertMovies(List<Movie> movieList);

    public void updateOrInsertTheaters(List<Theater> theaterList);

    public void deleteMoviesForLocation(Location location);

    public void deleteTheatersForLocation(Location location);

    public void deleteRequest(Request request);

    public List<Movie> getAllMovies();
}
