package com.example.pbose.sqlite;

import android.provider.BaseColumns;

/**
 * Created by pbose on 3/19/16.
 */
public final class MovieDataContract {
    public MovieDataContract(){}

    public static abstract class MovieData implements BaseColumns {
        /** Request Table **/
        public static final String MOVIE_REQUEST_TABLE_NAME = "movierequest";
        public static final String COLUMN_NAME_MOVIE_IDENTIFIER = "movieid";
        public static final String COLUMN_NAME_THEATER_IDENTIFIER = "theaterid";
        public static final String COLUMN_NAME_SEARCH_DATE = "searchdate";
        public static final String COLUMN_NAME_COMPLETION_DATE = "completiondate";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_FINAL_SCRAPPING_URL = "finalscrappingurl";

        /** Theater Table **/
        public static final String THEATER_TABLE_NAME = "theater";
        public static final String COLUMN_NAME_THEATER_DISPLAY_NAME = "theatername";
        public static final String COLUMN_NAME_LAST_REFRESH_TIME = "lastrefreshtime";

        /** Movie Table **/
        public static final String MOVIE_TABLE_NAME = "movie";
        public static final String COLUMN_NAME_MOVIE_DISPLAY_NAME = "moviename";
        public static final String COLUMN_NAME_LANGUAGES = "languages";

    }
}
