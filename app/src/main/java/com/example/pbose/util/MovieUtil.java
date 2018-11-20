package com.example.pbose.util;

import android.database.Cursor;

import com.example.pbose.sqlite.MovieDataContract;
import com.example.pbose.types.Location;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Theater;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 3/20/16.
 */
public class MovieUtil {
    public static List<Movie> getMoviesFromDBCursor(Cursor cursor){
        List<Movie> movieList = new ArrayList<Movie>();

        if(cursor.moveToFirst()){
            do{
                String movieId = cursor.getString(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_MOVIE_IDENTIFIER));
                String movieDisplayName = cursor.getString(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_MOVIE_DISPLAY_NAME));
                String languages = cursor.getString(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_LANGUAGES));
                String locationString = cursor.getString(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_LOCATION));
                Long lastRefreshTimeInMilis = cursor.getLong(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_LAST_REFRESH_TIME));

                Location location = Location.valueOf(locationString);
                List<String> languageList = getLanguageList(languages);
                Date lastRefreshDate = new Date(lastRefreshTimeInMilis);

                Movie movie = new Movie(movieId,movieDisplayName,location,languageList,lastRefreshDate);

                movieList.add(movie);

            }while(cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return movieList;
    }

    private static List<String> getLanguageList(String languages){
        List<String> languageList = new ArrayList<String>();
        if(StringUtil.isBlank(languages)){
            return languageList;
        }
        String[] languageArray = languages.split(",");

        for(String language : languageArray){
            languageList.add(language);
        }
        return languageList;
    }

    public static String getLanguageString(List<String> languageList){
        return StringUtil.join(languageList,",");
    }
}
