package com.example.cinemaquest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;

public class QuestionnaireFragment extends Fragment {

    private int currentQuestion = 1;
    private TextView questionText;
    private RadioGroup answerOptions;
    private Button previousButton, nextButton;
    private DatabaseHelper dbHelper; // Declare dbHelper

    private String[] questions = {
            "What genre do you prefer?",
            "What type of movie setting do you enjoy?",
            "What is your favorite movie era?",
            "What mood are you in?",
            "What kind of storyline do you prefer?"
    };

    private String[][] answers = {
            {"Action", "Comedy", "Drama", "Sci-Fi"},
            {"Urban", "Rural", "Fantasy", "Historical"},
            {"1990s", "2000", "2010s", "2020s"},
            {"Happy", "Sad", "Excited", "Relaxed"},
            {"Adventure", "Romance", "Mystery", "Thriller"}
    };

    private TextView[] indicators = new TextView[5];
    private Button finishButton;
    private HashMap<Integer, String> selectedAnswers = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);
        questionText = view.findViewById(R.id.question_text);
        answerOptions = view.findViewById(R.id.answer_options);
        previousButton = view.findViewById(R.id.previous_button);
        nextButton = view.findViewById(R.id.next_button);
        finishButton = view.findViewById(R.id.finish_button);
        dbHelper = new DatabaseHelper(getContext());

        // Clear previous answers from the database
        clearPreviousAnswers();

        // Initialize indicators
        indicators[0] = view.findViewById(R.id.indicator_1);
        indicators[1] = view.findViewById(R.id.indicator_2);
        indicators[2] = view.findViewById(R.id.indicator_3);
        indicators[3] = view.findViewById(R.id.indicator_4);
        indicators[4] = view.findViewById(R.id.indicator_5);

        // Check for null indicators and log an error if any are null
        for (int i = 0; i < indicators.length; i++) {
            if (indicators[i] == null) {
                Log.e("QuestionnaireFragment", "Indicator " + (i + 1) + " is null");
            }
        }
        // Set up listeners and initial state
        updateQuestion();

        previousButton.setOnClickListener(v -> {
            if (currentQuestion > 1) {
                saveCurrentAnswer();
                currentQuestion--;
                updateQuestion();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentQuestion < questions.length) {
                saveCurrentAnswer();
                currentQuestion++;
                updateQuestion();
            }
        });

        finishButton.setOnClickListener(v -> {
            saveCurrentAnswer();
            saveAnswers();
            displaySavedAnswers(); // Display the saved answers
            // Reset the questionnaire for a fresh start
            currentQuestion = 1;
            updateQuestion();
            finishButton.setVisibility(View.GONE); // Hide the finish button after finishing
        });

        return view;
    }

    private void clearPreviousAnswers() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("answers", null, null);
        db.close();
    }

    private void updateQuestion() {
        // Update question text and answer options
        questionText.setText(questions[currentQuestion - 1]);
        answerOptions.removeAllViews();
        for (String answer : answers[currentQuestion - 1]) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(answer);
            answerOptions.addView(radioButton);
        }

        // Update indicators
        updateIndicators();

        // Enable/disable navigation buttons
        previousButton.setEnabled(currentQuestion > 1);
        nextButton.setEnabled(currentQuestion < questions.length);

        // Show Finish button on the last question, hide Next button
        if (currentQuestion == questions.length) {
            nextButton.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            finishButton.setVisibility(View.GONE);
        }
    }

    private void updateIndicators() {
        for (int i = 0; i < indicators.length; i++) {
            if (i < currentQuestion - 1) {
                indicators[i].setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            } else if (i == currentQuestion - 1) {
                indicators[i].setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            } else {
                indicators[i].setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    private void saveCurrentAnswer() {
        int selectedId = answerOptions.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = answerOptions.findViewById(selectedId);
            selectedAnswers.put(currentQuestion, selectedRadioButton.getText().toString());
        }
    }

    private void saveAnswers() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i = 1; i <= questions.length; i++) {
            values.put("question", questions[i - 1]);
            values.put("answer", selectedAnswers.get(i));
            db.insert("answers", null, values);
        }

        db.close();
    }

    private void displaySavedAnswers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "question",
                "answer"
        };

        Cursor cursor = db.query(
                "answers",
                projection,
                null,
                null,
                null,
                null,
                null
        );

        StringBuilder savedAnswers = new StringBuilder();
        while (cursor.moveToNext()) {
            String question = cursor.getString(cursor.getColumnIndexOrThrow("question"));
            String answer = cursor.getString(cursor.getColumnIndexOrThrow("answer"));
            savedAnswers.append(question).append(": ").append(answer).append("\n");
        }
        cursor.close();
        db.close();

        // Display the saved answers (e.g., in a TextView or a dialog)
        new AlertDialog.Builder(getContext())
                .setTitle("Saved Answers")
                .setMessage(savedAnswers.toString())
                .setPositiveButton("OK", null)
                .show();
    }
}