package com.example.cinemaquest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    //Defining UI components
    private EditText myEditText1, myEditText2, myEditText3, myEditText4,myEditText5;
    private TextView myTextView1, myTextView2;
    private Button myButton1, myButton2;
    private Switch s;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginandsignup);

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
                    myTextView1.setVisibility(View.VISIBLE); // Login Form
                    myButton1.setVisibility(View.VISIBLE); // Login Button
                    myEditText1.setVisibility(View.VISIBLE); // Login Email
                    myEditText2.setVisibility(View.VISIBLE);

                    myTextView2.setVisibility(View.INVISIBLE);
                    myButton2.setVisibility(View.INVISIBLE);
                    myEditText3.setVisibility(View.INVISIBLE);
                    myEditText4.setVisibility(View.INVISIBLE);
                    myEditText5.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
}