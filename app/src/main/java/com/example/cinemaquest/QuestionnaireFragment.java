package com.example.cinemaquest;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.HashMap;

public class QuestionnaireFragment extends Fragment {

    private int currentQuestion = 1;
    private TextView questionText;
    private RadioGroup answerOptions;
    private Button previousButton, nextButton;
    private DatabaseHelper2 dbHelper; // Declare dbHelper

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
        dbHelper = new DatabaseHelper2(getContext());

        // Initialize indicators
        indicators[0] = view.findViewById(R.id.indicator_1);
        indicators[1] = view.findViewById(R.id.indicator_2);
        indicators[2] = view.findViewById(R.id.indicator_3);
        indicators[3] = view.findViewById(R.id.indicator_4);
        indicators[4] = view.findViewById(R.id.indicator_5);


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
            // Perform existing actions (saving answers, resetting questionnaire, etc.)
            saveCurrentAnswer();
            saveAnswers();
            displaySavedAnswers(); // Display the saved answers

            // Reset the questionnaire for a fresh start
            currentQuestion = 1;
            updateQuestion();

            // Hide the finish button after finishing
            finishButton.setVisibility(View.GONE);

            // Start the RecommendationActivity from the Fragment using getActivity()
            Intent intent = new Intent(requireActivity(), RecommendationActivity.class);
            startActivity(intent);
        });
        return view;
    }

    private void updateQuestion() {
        // Update question logic
        questionText.setText(questions[currentQuestion - 1]);
        answerOptions.removeAllViews();
        for (String answer : answers[currentQuestion - 1]) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(answer);
            answerOptions.addView(radioButton);
        }

        // Restore previously selected answer if available
        if (selectedAnswers.containsKey(currentQuestion)) {
            String selectedAnswer = selectedAnswers.get(currentQuestion);
            for (int i = 0; i < answerOptions.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) answerOptions.getChildAt(i);
                if (radioButton.getText().equals(selectedAnswer)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        }

        // Update question indicators
        updateIndicators();

        // Enable or disable the navigation buttons based on the question position
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
            if (i == currentQuestion - 1) {
                indicators[i].setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            } else {
                indicators[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }
    }

    private void saveCurrentAnswer() {
        int selectedId = answerOptions.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = answerOptions.findViewById(selectedId);
            String selectedAnswer = selectedRadioButton.getText().toString();
            selectedAnswers.put(currentQuestion, selectedAnswer);
        }
    }

    private void saveAnswers() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i = 1; i <= questions.length; i++) {
            if (selectedAnswers.containsKey(i)) {
                values.put(DatabaseHelper2.COLUMN_QUESTION, questions[i - 1]);
                values.put(DatabaseHelper2.COLUMN_ANSWER, selectedAnswers.get(i));
                String whereClause = DatabaseHelper2.COLUMN_QUESTION + " = ?";
                String[] whereArgs = {questions[i - 1]};
                int rowsUpdated = db.update(DatabaseHelper2.TABLE_ANSWERS, values, whereClause, whereArgs);
                if (rowsUpdated == 0) {
                    db.insert(DatabaseHelper2.TABLE_ANSWERS, null, values);
                }
            }
        }

        db.close();
    }

    private void displaySavedAnswers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper2.COLUMN_QUESTION,
                DatabaseHelper2.COLUMN_ANSWER
        };

        Cursor cursor = db.query(
                DatabaseHelper2.TABLE_ANSWERS,   // The table to query
                projection,                     // The columns to return
                null,                           // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                           // Don't group the rows
                null,                           // Don't filter by row groups
                null                            // The sort order
        );

        StringBuilder savedAnswers = new StringBuilder();
        while (cursor.moveToNext()) {
            String question = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper2.COLUMN_QUESTION));
            String answer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper2.COLUMN_ANSWER));
            savedAnswers.append("Question: ").append(question).append("\nAnswer: ").append(answer).append("\n\n");
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