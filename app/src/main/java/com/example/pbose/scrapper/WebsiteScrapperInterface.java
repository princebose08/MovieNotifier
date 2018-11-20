package com.example.pbose.scrapper;

import com.example.pbose.types.Location;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Request;
import com.example.pbose.types.Theater;

import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 3/20/16.
 */
public interface WebsiteScrapperInterface {
    /**
     * Find if movie is available or not
     * @param request
     * @return
     */
    public boolean isMovieAvailable(Request request);

    /**
     * Return all movies for a location
     * @param location
     * @return
     */
    public List<Movie> getAllMovies(Location location);

    /**
     * Return all theaters for a location
     * @param location
     * @return
     */
    public List<Theater> getAllTheaters(Location location);
}
