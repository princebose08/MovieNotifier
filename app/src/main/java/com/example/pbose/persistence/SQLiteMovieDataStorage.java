package com.example.pbose.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pbose.exceptions.DataNotFoundException;
import com.example.pbose.sqlite.MovieDataContract;
import com.example.pbose.sqlite.MovieDataSQLiteHelper;
import com.example.pbose.types.Location;
import com.example.pbose.types.Movie;
import com.example.pbose.types.Request;
import com.example.pbose.types.Status;
import com.example.pbose.types.Theater;
import com.example.pbose.util.MovieUtil;
import com.example.pbose.util.TheaterUtil;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.example.pbose.sqlite.MovieDataContract.MovieData;

/**
 * Created by pbose on 3/19/16.
 */
public class SQLiteMovieDataStorage implements MovieDataStorage {

    MovieDataSQLiteHelper sqliteHelper;
    SQLiteDatabase readableDB;
    SQLiteDatabase writeableDB;

    private static SQLiteMovieDataStorage movieDataStorage;

    public static SQLiteMovieDataStorage getInstance(Context context){
        if(movieDataStorage == null){
            movieDataStorage = new SQLiteMovieDataStorage(context);
        }
        return movieDataStorage;
    }

    private SQLiteMovieDataStorage(Context context){
        sqliteHelper = MovieDataSQLiteHelper.getInstance(context);

        readableDB = sqliteHelper.getReadableDatabase();
        writeableDB = sqliteHelper.getWritableDatabase();

        //writeableDB.execSQL("DROP TABLE "+MovieData.MOVIE_REQUEST_TABLE_NAME);
        writeableDB.execSQL(getCreateRequestTableQuery());
        writeableDB.execSQL(getCreateTheaterTableQuery());
        writeableDB.execSQL(getCreateMovieTableQuery());
    }

    @Override
    public boolean addRequest(Request request) {
        System.out.println("Adding data in DB");
        ContentValues values = new ContentValues();
        if(request.getId() > 0) {
            values.put(MovieData._ID, request.getId());
        }
        values.put(MovieData.COLUMN_NAME_MOVIE_IDENTIFIER,request.getMovie().getId());
        values.put(MovieData.COLUMN_NAME_THEATER_IDENTIFIER,TheaterUtil.getTheaterString(request.getTheaterList()));
        values.put(MovieData.COLUMN_NAME_SEARCH_DATE,request.getSearchDate().getTime());
        values.put(MovieData.COLUMN_NAME_LOCATION,request.getLocation().name());
        values.put(MovieData.COLUMN_NAME_STATUS, Status.SCHEDULED.name());
        System.out.println("Performing Insert or Update");

        //long rowId = writeableDB.insert(MovieData.MOVIE_REQUEST_TABLE_NAME, null, values);
        long rowId = writeableDB.insertWithOnConflict(MovieData.MOVIE_REQUEST_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        System.out.println("data addedin DB");
        return rowId != -1;

    }

    @Override
    public List<Request> getRequestsByStatus(Status status) {
        Cursor cursor = readableDB.query(MovieData.MOVIE_REQUEST_TABLE_NAME, null, MovieData.COLUMN_NAME_STATUS + "=?", new String[]{status.name()}, null, null, null);
        return getRequestsFromDBCursor(cursor);
    }

    @Override
    public List<Request> getRequests() {
        Cursor cursor = readableDB.query(MovieData.MOVIE_REQUEST_TABLE_NAME, null, null, null, null, null, null);
        return getRequestsFromDBCursor(cursor);
    }

    @Override
    public void updateRequests(List<Request> requests) {
        for(Request request : requests) {
            ContentValues values = new ContentValues();
            values.put(MovieData.COLUMN_NAME_MOVIE_IDENTIFIER, request.getMovie().getId());
            values.put(MovieData.COLUMN_NAME_THEATER_IDENTIFIER, TheaterUtil.getTheaterString(request.getTheaterList()));
            values.put(MovieData.COLUMN_NAME_SEARCH_DATE, request.getSearchDate().getTime());
            values.put(MovieData.COLUMN_NAME_LOCATION, request.getLocation().name());
            if(request.getCompletionDate() != null) {
                values.put(MovieData.COLUMN_NAME_COMPLETION_DATE, request.getCompletionDate().getTime());
            }
            if(!StringUtil.isBlank(request.getScrapperUrl())){
                values.put(MovieData.COLUMN_NAME_FINAL_SCRAPPING_URL,request.getScrapperUrl());
            }
            if(request.getStatus() != null) {
                values.put(MovieData.COLUMN_NAME_STATUS, request.getStatus().name());
            }
            System.out.println("Updating Request as :"+request);
            writeableDB.update(MovieData.MOVIE_REQUEST_TABLE_NAME,values,"_id="+request.getId(),null);
        }
    }

    @Override
    public List<Theater> getTheaters(Location location){
        Cursor cursor = readableDB.query(MovieData.THEATER_TABLE_NAME, null, MovieData.COLUMN_NAME_LOCATION+"=?", new String[]{location.name()}, null, null, null);
        return TheaterUtil.getTheatersFromDBCursor(cursor);
    }

    @Override
    public Theater getTheaterById(String theaterId,Location location) throws DataNotFoundException {
        Cursor cursor = readableDB.query(MovieData.THEATER_TABLE_NAME, null, MovieData.COLUMN_NAME_LOCATION+"=? AND "+MovieData.COLUMN_NAME_THEATER_IDENTIFIER + "=?", new String[]{location.name(),theaterId}, null, null, null);
        List<Theater> theaters = TheaterUtil.getTheatersFromDBCursor(cursor);
        if(theaters.size() > 0){
            return theaters.get(0);
        }else {
            throw new DataNotFoundException("No matching theater found.");
        }
    }

    public List<Movie> getAllMovies(){
        Cursor cursor = readableDB.query(MovieData.MOVIE_TABLE_NAME, null, null, null, null, null, null);
        return MovieUtil.getMoviesFromDBCursor(cursor);
    }

    @Override
    public List<Movie> getMovies(Location location){
        Cursor cursor = readableDB.query(MovieData.MOVIE_TABLE_NAME, null, MovieData.COLUMN_NAME_LOCATION+"=?", new String[]{location.name()}, null, null, null);
        return MovieUtil.getMoviesFromDBCursor(cursor);
    }

    @Override
    public Movie getMovieById(String movieId,Location location) throws DataNotFoundException{
        Cursor cursor = readableDB.query(MovieData.MOVIE_TABLE_NAME, null, MovieData.COLUMN_NAME_LOCATION+"=? AND "+MovieData.COLUMN_NAME_MOVIE_IDENTIFIER + "=?", new String[]{location.name(),movieId}, null, null, null);
        List<Movie> movies = MovieUtil.getMoviesFromDBCursor(cursor);
        if(movies.size() > 0){
            return movies.get(0);
        }else {
            throw new DataNotFoundException("No matching theater found.");
        }
    }

    @Override
    public void updateOrInsertMovies(List<Movie> movieList){
        for(Movie movie : movieList) {
            ContentValues values = new ContentValues();
            values.put(MovieData.COLUMN_NAME_MOVIE_IDENTIFIER, movie.getId());
            values.put(MovieData.COLUMN_NAME_MOVIE_DISPLAY_NAME, movie.getName());
            values.put(MovieData.COLUMN_NAME_LOCATION, movie.getLocation().name());
            values.put(MovieData.COLUMN_NAME_LANGUAGES, MovieUtil.getLanguageString(movie.getLanguageList()));
            values.put(MovieData.COLUMN_NAME_LAST_REFRESH_TIME, new Date().getTime());

            System.out.println("Updating Movie as :" + movie);
            writeableDB.insertWithOnConflict(MovieData.MOVIE_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    @Override
    public void deleteMoviesForLocation(Location location){
        writeableDB.delete(MovieData.MOVIE_TABLE_NAME,MovieData.COLUMN_NAME_LOCATION+"=?",new String[]{location.name()});
    }

    @Override
    public void deleteTheatersForLocation(Location location){
        writeableDB.delete(MovieData.THEATER_TABLE_NAME,MovieData.COLUMN_NAME_LOCATION+"=?",new String[]{location.name()});
    }

    public void deleteRequest(int requestId){
        writeableDB.delete(MovieData.MOVIE_REQUEST_TABLE_NAME,MovieData._ID+"=?",new String[]{String.valueOf(requestId)});
    }

    @Override
    public void deleteRequest(Request request){
        writeableDB.delete(MovieData.MOVIE_REQUEST_TABLE_NAME,MovieData._ID+"=?",new String[]{String.valueOf(request.getId())});
    }

    public void updateOrInsertTheaters(List<Theater> theaterList){
        for(Theater theater : theaterList) {
            ContentValues values = new ContentValues();
            values.put(MovieData.COLUMN_NAME_THEATER_IDENTIFIER, theater.getId());
            values.put(MovieData.COLUMN_NAME_THEATER_DISPLAY_NAME, theater.getName());
            values.put(MovieData.COLUMN_NAME_LOCATION, theater.getLocation().name());
            values.put(MovieData.COLUMN_NAME_LAST_REFRESH_TIME, new Date().getTime());

            System.out.println("Updating Movie as :" + theater);
            writeableDB.insertWithOnConflict(MovieData.THEATER_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }


    private List<Request> getRequestsFromDBCursor(Cursor cursor){
        List<Request> requestList = new ArrayList<Request>();

        if(cursor.moveToFirst()){
            do{
                String movieId = cursor.getString(cursor.getColumnIndexOrThrow(MovieData.COLUMN_NAME_MOVIE_IDENTIFIER));
                String theaterIds = cursor.getString(cursor.getColumnIndexOrThrow(MovieData.COLUMN_NAME_THEATER_IDENTIFIER));
                String locationString = cursor.getString(cursor.getColumnIndexOrThrow(MovieData.COLUMN_NAME_LOCATION));
                String statusString = cursor.getString(cursor.getColumnIndexOrThrow(MovieData.COLUMN_NAME_STATUS));
                String scrapperUrl = cursor.getString(cursor.getColumnIndexOrThrow(MovieData.COLUMN_NAME_FINAL_SCRAPPING_URL));
                Long searchDateInMilis = cursor.getLong(cursor.getColumnIndexOrThrow(MovieData.COLUMN_NAME_SEARCH_DATE));
                Long completionDateInMilis = cursor.getLong(cursor.getColumnIndexOrThrow(MovieData.COLUMN_NAME_COMPLETION_DATE));
                System.out.println("******"+completionDateInMilis);

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MovieData._ID));


                try {
                    Date searchDate = new Date(searchDateInMilis);
                    Status status = Status.valueOf(statusString);
                    Location location = Location.valueOf(locationString);

                    Movie movie = getMovieById(movieId, location);

                    List<Theater> theaterList = TheaterUtil.getTheaterListFromString(theaterIds, location, this);

                    Request req = new Request(id, movie, theaterList, searchDate, location);
                    req.setStatus(status);
                    req.setScrapperUrl(scrapperUrl);
                    if(completionDateInMilis != null && completionDateInMilis > 0){
                        req.setCompletionDate(new Date(completionDateInMilis));
                    }
                    requestList.add(req);
                }catch (Exception e){
                    e.printStackTrace();
                    // delete the request
                    deleteRequest(id);
                    continue;
                }

            }while(cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return requestList;
    }


    public String getCreateRequestTableQuery(){
        String TEXT_TYPE = " TEXT";
        String DATE_TYPE = " NUMERIC";
        String COMMA_SEP = ",";

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + MovieData.MOVIE_REQUEST_TABLE_NAME + " (" +
                        MovieData._ID + " INTEGER PRIMARY KEY," +
                        MovieData.COLUMN_NAME_MOVIE_IDENTIFIER + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_THEATER_IDENTIFIER + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_SEARCH_DATE + DATE_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_COMPLETION_DATE + DATE_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_FINAL_SCRAPPING_URL + TEXT_TYPE+" )";
        return SQL_CREATE_ENTRIES;

    }

    public String getCreateTheaterTableQuery(){
        String TEXT_TYPE = " TEXT";
        String DATE_TYPE = " NUMERIC";
        String PRIMARY_KEY=" PRIMARY KEY";
        String COMMA_SEP = ",";

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + MovieData.THEATER_TABLE_NAME + " (" +
                        MovieData.COLUMN_NAME_THEATER_IDENTIFIER + TEXT_TYPE +PRIMARY_KEY+ COMMA_SEP +
                        MovieData.COLUMN_NAME_THEATER_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_LAST_REFRESH_TIME + DATE_TYPE +" )";
        return SQL_CREATE_ENTRIES;

    }

    public String getCreateMovieTableQuery(){
        String TEXT_TYPE = " TEXT";
        String DATE_TYPE = " NUMERIC";
        String PRIMARY_KEY=" PRIMARY KEY";
        String COMMA_SEP = ",";

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + MovieData.MOVIE_TABLE_NAME + " (" +
                        MovieData.COLUMN_NAME_MOVIE_IDENTIFIER + TEXT_TYPE +PRIMARY_KEY+ COMMA_SEP +
                        MovieData.COLUMN_NAME_MOVIE_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_LANGUAGES + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                        MovieData.COLUMN_NAME_LAST_REFRESH_TIME + DATE_TYPE +" )";
        return SQL_CREATE_ENTRIES;

    }
}
