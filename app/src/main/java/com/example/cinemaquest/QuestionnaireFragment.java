package com.example.cinemaquest;

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
    private DatabaseHelper dbHelper; // Declare dbHelper

    private String[] questions = {
            "What is your favorite movie genre?",
            "Which movie do you prefer?",
            "What kind of movie ending do you like?",
            "Which actor do you prefer?",
            "What type of movie soundtrack do you enjoy?",
            "Which movie setting do you prefer?",
            "What kind of movie plot do you enjoy?",
            "Which movie director do you prefer?",
            "What movie length do you prefer?",
            "Which movie era do you prefer?"
    };

    private String[][] answers = {
            {"Action", "Comedy", "Drama", "Sci-Fi"},
            {"Inception", "Titanic", "The Godfather", "Star Wars"},
            {"Happy", "Sad", "Cliffhanger", "Open-ended"},
            {"Leonardo DiCaprio", "Tom Hanks", "Meryl Streep", "Scarlett Johansson"},
            {"Classical", "Rock", "Pop", "Instrumental"},
            {"Urban", "Rural", "Space", "Fantasy"},
            {"Linear", "Non-linear", "Multiple plots", "Single plot"},
            {"Steven Spielberg", "Christopher Nolan", "Quentin Tarantino", "Martin Scorsese"},
            {"Short", "Medium", "Long", "Epic"},
            {"Classic", "Modern", "Future", "Historical"}
    };

    private TextView[] indicators = new TextView[10];
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

        // Initialize indicators
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
                values.put(DatabaseHelper.COLUMN_QUESTION, questions[i - 1]);
                values.put(DatabaseHelper.COLUMN_ANSWER, selectedAnswers.get(i));
                String whereClause = DatabaseHelper.COLUMN_QUESTION + " = ?";
                String[] whereArgs = {questions[i - 1]};
                int rowsUpdated = db.update(DatabaseHelper.TABLE_ANSWERS, values, whereClause, whereArgs);
                if (rowsUpdated == 0) {
                    db.insert(DatabaseHelper.TABLE_ANSWERS, null, values);
                }
            }
        }

        db.close();
    }

    private void displaySavedAnswers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_QUESTION,
                DatabaseHelper.COLUMN_ANSWER
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ANSWERS,   // The table to query
                projection,                     // The columns to return
                null,                           // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                           // Don't group the rows
                null,                           // Don't filter by row groups
                null                            // The sort order
        );

        StringBuilder savedAnswers = new StringBuilder();
        while (cursor.moveToNext()) {
            String question = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUESTION));
            String answer = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ANSWER));
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