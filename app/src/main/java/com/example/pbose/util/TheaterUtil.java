package com.example.pbose.util;

import android.database.Cursor;

import com.example.pbose.exceptions.DataNotFoundException;
import com.example.pbose.persistence.MovieDataStorage;
import com.example.pbose.sqlite.MovieDataContract;
import com.example.pbose.types.Location;
import com.example.pbose.types.Theater;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pbose on 3/20/16.
 */
public class TheaterUtil {

    public static List<Theater> getTheaterListFromString(String theaters,Location location,MovieDataStorage movieDataStorage){
        List<Theater> theaterList = new ArrayList<Theater>();
        if(StringUtil.isBlank(theaters)){
            return theaterList;
        }
        String[] theaterArray = theaters.split(",");

        for(String theater : theaterArray){
            try {
                theaterList.add(movieDataStorage.getTheaterById(theater,location));
            } catch (DataNotFoundException e) {
                e.printStackTrace();
            }
        }
        return theaterList;
    }

    public static List<String> getTheaterIdList(List<Theater> theaterList){
        List<String> theaterIdList = new ArrayList<String>();
        for(Theater theater : theaterList){
            String id = theater.getId();
            if(!StringUtil.isBlank(id)) {
                theaterIdList.add(id);
            }
        }

        return theaterIdList;
    }

    public static List<String> getTheaterNameList(List<Theater> theaterList){
        List<String> theaterNameList = new ArrayList<String>();
        for(Theater theater : theaterList){
            String theaterName = theater.getName();
            if(!StringUtil.isBlank(theaterName)) {
                theaterNameList.add(theaterName);
            }
        }

        return theaterNameList;
    }

    public static String getTheaterString(List<Theater> theaterList){
        return StringUtil.join(getTheaterIdList(theaterList),",");
    }

    public static List<Theater> getTheatersFromDBCursor(Cursor cursor){
        List<Theater> theaterList = new ArrayList<Theater>();

        if(cursor.moveToFirst()){
            do{
                String theaterId = cursor.getString(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_THEATER_IDENTIFIER));
                String theaterDisplayName = cursor.getString(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_THEATER_DISPLAY_NAME));
                String locationString = cursor.getString(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_LOCATION));
                Long lastRefreshTimeInMilis = cursor.getLong(cursor.getColumnIndexOrThrow(MovieDataContract.MovieData.COLUMN_NAME_LAST_REFRESH_TIME));

                Location location = Location.valueOf(locationString);
                Date lastRefreshDate = new Date(lastRefreshTimeInMilis);

                Theater theater = new Theater(theaterId,theaterDisplayName,location,lastRefreshDate);

                theaterList.add(theater);

            }while(cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return theaterList;
    }
}
