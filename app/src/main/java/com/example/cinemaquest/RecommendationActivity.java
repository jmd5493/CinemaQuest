package com.example.cinemaquest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    private databaseHelper3 dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        dbHelper = new databaseHelper3(this);
        List<String[]> randomMovies = dbHelper.getRandomMovies(10);  // Fetch 10 random movies

        LinearLayout movieListContainer = findViewById(R.id.movie_list_container);
        movieListContainer.removeAllViews();  // Clear any previous views

        if (randomMovies.isEmpty()) {
            TextView noMoviesFound = new TextView(this);
            noMoviesFound.setText("No movie recommendations found.");
            movieListContainer.addView(noMoviesFound);
        } else {
            // Add movie titles and genres to the layout
            for (String[] movie : randomMovies) {
                String title = movie[0];
                String genre = movie[1];

                LinearLayout movieItemLayout = new LinearLayout(this);
                movieItemLayout.setOrientation(LinearLayout.VERTICAL);
                movieItemLayout.setPadding(8, 8, 8, 8);

                TextView movieTitle = new TextView(this);
                movieTitle.setText(title);
                movieTitle.setTextSize(18);
                movieTitle.setTextColor(getResources().getColor(android.R.color.black));

                TextView movieGenre = new TextView(this);
                movieGenre.setText(genre);
                movieGenre.setTextSize(16);
                movieGenre.setTextColor(getResources().getColor(android.R.color.darker_gray));

                Button movieButton = new Button(this);
                movieButton.setText("View Movie");

                // Add a dummy listener for now
                movieButton.setOnClickListener(v -> {
                    Toast.makeText(RecommendationActivity.this, "Details for: " + title, Toast.LENGTH_SHORT).show();
                });

                movieItemLayout.addView(movieTitle);
                movieItemLayout.addView(movieGenre);
                movieItemLayout.addView(movieButton);

                movieListContainer.addView(movieItemLayout);
            }
        }
    }
}
