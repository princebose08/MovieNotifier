package com.example.pbose.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.pbose.sqlite.MovieDataContract.MovieData;
/**
 * Created by pbose on 3/19/16.
 */
public class MovieDataSQLiteHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "MovieData.db";
    private static MovieDataSQLiteHelper movieDataSQLiteHelper;

    public static MovieDataSQLiteHelper getInstance(Context context){
        if(movieDataSQLiteHelper == null){
            movieDataSQLiteHelper = new MovieDataSQLiteHelper(context);
        }
        return movieDataSQLiteHelper;

    }

    private MovieDataSQLiteHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Upgrading database from version :" + oldVersion + " to new version :" + newVersion);
        if (newVersion > oldVersion && newVersion > 1 && oldVersion < 2) {
            db.execSQL("ALTER TABLE " + MovieData.MOVIE_REQUEST_TABLE_NAME + " ADD COLUMN "
                    + MovieData.COLUMN_NAME_FINAL_SCRAPPING_URL + " TEXT");
        }
        if(newVersion > oldVersion && newVersion > 1 && oldVersion < 3){
            db.execSQL("ALTER TABLE " + MovieData.MOVIE_REQUEST_TABLE_NAME + " ADD COLUMN "
                    + MovieData.COLUMN_NAME_COMPLETION_DATE + " NUMERIC");
        }

        //whenever changing db, update the version and put code inside this condition
        // if(newVersion > oldVersion && newVersion > 1 && oldVersion < {new changed version} ){

    }

}
