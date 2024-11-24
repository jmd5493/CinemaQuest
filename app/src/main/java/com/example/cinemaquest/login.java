package com.example.cinemaquest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity
{
    //Defining UI components
    databasehelper dbHelp;
    private EditText myEditText1, myEditText2, myEditText3, myEditText4,myEditText5;
    private TextView myTextView1, myTextView2;
    private Button myButton1, myButton2;
    private Switch s;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginandsignup);

        dbHelp = new databasehelper(this);
        s = findViewById(R.id.loginandSignupSwitch);
        myButton1 = findViewById(R.id.loginBtn);
        myButton2 = findViewById(R.id.SignUpBtn);
        myTextView1 = findViewById(R.id.loginForm);
        myTextView2 = findViewById(R.id.signupForm);
        myEditText1 = findViewById(R.id.loginEmail);
        myEditText2 = findViewById(R.id.loginPassword);
        myEditText3 = findViewById(R.id.SignUpEmail);
        myEditText4 = findViewById(R.id.SignUpPassword);
        myEditText5 = findViewById(R.id.SignUpConfirmPassword);

        s.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(s.isChecked())
                {
                    myTextView2.setVisibility(View.VISIBLE);
                    myButton2.setVisibility(View.VISIBLE);
                    myEditText3.setVisibility(View.VISIBLE);
                    myEditText4.setVisibility(View.VISIBLE);
                    myEditText5.setVisibility(View.VISIBLE);

                    myTextView1.setVisibility(View.INVISIBLE);
                    myButton1.setVisibility(View.INVISIBLE);
                    myEditText1.setVisibility(View.INVISIBLE);
                    myEditText2.setVisibility(View.INVISIBLE);
                }
                else
                {
                    myTextView1.setVisibility(View.VISIBLE);
                    myButton1.setVisibility(View.VISIBLE);
                    myEditText1.setVisibility(View.VISIBLE);
                    myEditText2.setVisibility(View.VISIBLE);

                    myTextView2.setVisibility(View.INVISIBLE);
                    myButton2.setVisibility(View.INVISIBLE);
                    myEditText3.setVisibility(View.INVISIBLE);
                    myEditText4.setVisibility(View.INVISIBLE);
                    myEditText5.setVisibility(View.INVISIBLE);
                }
            }
        });
        myButton1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String userEmail = myEditText1.getText().toString().trim();
                String userPassword = myEditText2.getText().toString().trim();
                Cursor searchCursor = dbHelp.getUserByEmailAndPassword(userEmail,userPassword);
                StringBuilder builder = new StringBuilder();
                if(searchCursor != null)
                {
                    if(searchCursor.moveToFirst())
                    {
                        int userEmailColumnIndex = searchCursor.getColumnIndex(databasehelper.COLUMN_EMAIL);
                        int userPasswordColumnIndex = searchCursor.getColumnIndex(databasehelper.COLUMN_PASSWORD);
                        Toast.makeText(login.this, "Login successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(login.this, QuestionsActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(login.this, "Invalid Email or password", Toast.LENGTH_SHORT).show();
                    }
                    searchCursor.close();
                }
                else
                {
                    builder.append("Error: Could not search for the user");
                }
            }
        });
        myButton2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String userEmail = myEditText3.getText().toString();
                String userPassword = myEditText4.getText().toString();
                String userConfirmPassword = myEditText5.getText().toString();
                Cursor cursor = dbHelp.getAllUsers();
                StringBuilder builder = new StringBuilder();

                if(!userPassword.equals(userConfirmPassword))
                {
                    Toast.makeText(login.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHelp.insertUserInfo(userEmail,userPassword);
                myEditText3.setText("");
                myEditText4.setText("");
                if(cursor != null)
                {
                    int idColumnIndex = cursor.getColumnIndex(databasehelper.COLUMN_ID);
                    int userEmailColumnIndex = cursor.getColumnIndex(databasehelper.COLUMN_EMAIL);
                    int userPasswordColumnIndex = cursor.getColumnIndex(databasehelper.COLUMN_PASSWORD);
                    if(idColumnIndex !=-1 && userEmailColumnIndex !=-1 && userPasswordColumnIndex !=-1)
                    {
                        while(cursor.moveToNext())
                        {
                            int id = cursor.getInt(idColumnIndex);
                            String email = cursor.getString(userEmailColumnIndex);
                            String password = cursor.getString(userPasswordColumnIndex);
                            builder.append(id).append("\t");
                            builder.append(email).append("\t\t");
                            builder.append(password);
                            builder.append("\n\n");
                        }
                    }
                    else
                    {
                        builder.append("Error: Column not found.");
                    }
                    cursor.close();
                }
                else
                {
                    builder.append("User not found");
                }
                myTextView1.setVisibility(View.VISIBLE);
                myButton1.setVisibility(View.VISIBLE);
                myEditText1.setVisibility(View.VISIBLE);
                myEditText2.setVisibility(View.VISIBLE);

                myTextView2.setVisibility(View.INVISIBLE);
                myButton2.setVisibility(View.INVISIBLE);
                myEditText3.setVisibility(View.INVISIBLE);
                myEditText4.setVisibility(View.INVISIBLE);
                myEditText5.setVisibility(View.INVISIBLE);
            }
        });
    }
}