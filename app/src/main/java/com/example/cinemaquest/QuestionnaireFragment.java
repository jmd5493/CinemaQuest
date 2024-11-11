package com.example.cinemaquest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class QuestionnaireFragment extends Fragment {

    private int currentQuestion = 1;
    private TextView questionText;
    private RadioGroup answerOptions;
    private Button previousButton, nextButton;

    private String[] questions = {
            "What genre do you prefer?",
            "Which director do you like?",
            "What is your favorite movie era?",
            "What mood are you in?",
            "Favorite actor?",
            "Preferred length of movie?",
            "What time period?",
            "Favorite language?",
            "Level of intensity?",
            "Favorite soundtrack type?"
    };

    private String[][] answers = {
            {"Action", "Comedy", "Drama", "Sci-Fi"},
            {"Director 1", "Director 2", "Director 3", "Director 4"},
            {"1990s", "2000", "2010s", "2020s"},
            {"Happy", "Sad", "Excited", "Relaxed"},
            {"Actor 1", "Actor 2", "Actor 3", "Actor 4"},
            {"Short", "Medium", "Long", "Epic"},
            {"Modern", "Historic", "Futuristic", "Fantasy"},
            {"English", "Spanish", "French", "Mandarin"},
            {"Calm", "Moderate", "Intense", "Thrilling"},
            {"Classical", "Pop", "Rock", "Jazz"}
    };

    private TextView[] indicators;
     private Button finishButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);

        questionText = view.findViewById(R.id.question_text);
        answerOptions = view.findViewById(R.id.radioGroup_answers);
        previousButton = view.findViewById(R.id.previous_button);
        nextButton = view.findViewById(R.id.next_button);

        // Initialize the finish button
        finishButton = view.findViewById(R.id.finish_button);
        finishButton.setVisibility(View.GONE); // Hide the finish button initially

        // Set up the Finish button click listener
        finishButton.setOnClickListener(v -> {
            // Reset the questionnaire for a fresh start
            currentQuestion = 1;
            updateQuestion();
            finishButton.setVisibility(View.GONE); // Hide the finish button after finishing
        });

        // Initialize indicators
        indicators = new TextView[10];
        indicators[0] = view.findViewById(R.id.indicator_1);
        indicators[1] = view.findViewById(R.id.indicator_2);
        indicators[2] = view.findViewById(R.id.indicator_3);
        indicators[3] = view.findViewById(R.id.indicator_4);
        indicators[4] = view.findViewById(R.id.indicator_5);
        indicators[5] = view.findViewById(R.id.indicator_6);
        indicators[6] = view.findViewById(R.id.indicator_7);
        indicators[7] = view.findViewById(R.id.indicator_8);
        indicators[8] = view.findViewById(R.id.indicator_9);
        indicators[9] = view.findViewById(R.id.indicator_10);

        // Set up listeners for previous and next buttons
        previousButton.setOnClickListener(v -> {
            if (currentQuestion > 1) {
                currentQuestion--;
                updateQuestion();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentQuestion < questions.length) {
                currentQuestion++;
                updateQuestion();
            }
        });

        updateQuestion(); // Initial call to set the first question

        return view;
    }

    private void updateQuestion() {
        // Set the question text dynamically
        questionText.setText("Question " + currentQuestion + ": " + questions[currentQuestion - 1]);

        // Update answer options dynamically
        answerOptions.removeAllViews();
        for (String answer : answers[currentQuestion - 1]) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(answer);
            answerOptions.addView(radioButton);
        }

        // Loop through all indicators to reset their background color
        for (int i = 0; i < indicators.length; i++) {
            if (indicators[i] != null) {
                // Set background color to transparent for all indicators
                indicators[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }

        // Highlight the current question indicator
        if (indicators[currentQuestion - 1] != null) {
            indicators[currentQuestion - 1].setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        }

        // Enable or disable the navigation buttons based on the question position
        previousButton.setEnabled(currentQuestion > 1);
        nextButton.setEnabled(currentQuestion < questions.length);

        // Show Finish button on the last question, hide Next button
        if (currentQuestion == questions.length) {
            finishButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        } else {
            finishButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }


}
