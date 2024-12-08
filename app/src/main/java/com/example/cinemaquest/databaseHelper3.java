package com.example.cinemaquest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class databaseHelper3 extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cinema_movie.db";
    private static final int DATABASE_VERSION = 1;

    public databaseHelper3(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // If the table already exists, there's no need to create it again.
        // Just make sure the table is in the database
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    // Method to get random movies from the database
    public List<String[]> getRandomMovies(int numberOfMovies) {
        List<String[]> movieList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get random movies (title and genres)
        String query = "SELECT title, genres FROM movies ORDER BY RANDOM() LIMIT ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(numberOfMovies)});

        // Get column indices for title and genres
        int titleColumnIndex = cursor.getColumnIndex("title");
        int genresColumnIndex = cursor.getColumnIndex("genres");

        if (titleColumnIndex == -1 || genresColumnIndex == -1) {
            cursor.close();
            db.close();
            return movieList;
        }

        if (cursor.moveToFirst()) {
            do {
                // Fetch the movie title and genres
                String title = cursor.getString(titleColumnIndex);
                String genres = cursor.getString(genresColumnIndex);
                movieList.add(new String[]{title, genres});
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return movieList;
    }
}

