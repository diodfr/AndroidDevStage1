/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.didier.stage1.movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Manages a local database for favorite film data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    private static final String DATABASE_NAME = "film.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 3;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_FAVORITE_FILM_TABLE =

                "CREATE TABLE " + MovieDbContract.MovieEntry1.TABLE_NAME + " (" +
                        MovieDbContract.MovieEntry1._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
                        MovieDbContract.MovieEntry1.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieDbContract.MovieEntry1.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieDbContract.MovieEntry1.COLUMNM_FAVORITE + " INTEGER " +
                        ");";

        Log.d(MovieDbHelper.class.getName(), SQL_CREATE_FAVORITE_FILM_TABLE);
        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_FILM_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String SQL_DROP_MOVIE_DB_TABLE = "DROP TABLE " + MovieDbContract.MovieEntry1.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(SQL_DROP_MOVIE_DB_TABLE);

        onCreate(sqLiteDatabase);
    }
}