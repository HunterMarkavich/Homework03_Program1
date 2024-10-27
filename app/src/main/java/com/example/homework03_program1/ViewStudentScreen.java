package com.example.homework03_program1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ViewStudentScreen extends AppCompatActivity
{
    TextView tv_j_viewStudentHeader;
    TextView tv_j_viewStudentNameHere;
    TextView tv_j_viewStudentFullName;
    TextView tv_j_viewStudentUsernameHere;
    TextView tv_j_viewStudentUsername;
    TextView tv_j_viewStudentEmailHere;
    TextView tv_j_viewStudentEmail;
    TextView tv_j_viewStudentAgeHere;
    TextView tv_j_viewStudentAge;
    TextView tv_j_viewStudentGPAHere;
    TextView tv_j_viewStudentGPA;
    TextView tv_j_viewStudentMajorHere;
    TextView tv_j_viewStudentMajor;
    Button btn_j_viewStudentEditInfoButton;
    Button btn_j_viewStudentBackButton;
    Button btn_j_viewStudentDeleteStudentButton;
    TextView tv_j_viewStudentWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_student_screen_activity);

        //Initialize views
        tv_j_viewStudentHeader = findViewById(R.id.tv_v_viewStudentHeader);
        tv_j_viewStudentNameHere = findViewById(R.id.tv_v_viewStudentNameHere);
        tv_j_viewStudentFullName = findViewById(R.id.tv_v_viewStudentFullName);
        tv_j_viewStudentUsernameHere = findViewById(R.id.tv_v_viewStudentUsernameHere);
        tv_j_viewStudentUsername = findViewById(R.id.tv_v_viewStudentUsername);
        tv_j_viewStudentEmailHere = findViewById(R.id.tv_v_viewStudentEmailHere);
        tv_j_viewStudentEmail = findViewById(R.id.tv_v_viewStudentEmail);
        tv_j_viewStudentAgeHere = findViewById(R.id.tv_v_viewStudentAgeHere);
        tv_j_viewStudentAge = findViewById(R.id.tv_v_viewStudentAge);
        tv_j_viewStudentGPAHere = findViewById(R.id.tv_v_viewStudentGPAHere);
        tv_j_viewStudentGPA = findViewById(R.id.tv_v_viewStudentGPA);
        tv_j_viewStudentMajorHere = findViewById(R.id.tv_v_viewStudentMajorHere);
        tv_j_viewStudentMajor = findViewById(R.id.tv_v_viewStudentMajor);
        btn_j_viewStudentEditInfoButton = findViewById(R.id.btn_v_viewStudentEditInfoButton);
        btn_j_viewStudentBackButton = findViewById(R.id.btn_v_viewStudentBackButton);
        btn_j_viewStudentDeleteStudentButton = findViewById(R.id.btn_v_viewStudentDeleteStudentButton);
        tv_j_viewStudentWarning = findViewById(R.id.tv_v_viewStudentWarning);

        //Initialize DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //Get the passed username from intent
        String username = getIntent().getStringExtra("username");

        //Retrieve and display student data
        displayStudentInfo(username, dbHelper);

        //Listeners for back button and edit info button
        addBackButtonListener();
        addEditInfoButtonListener(username);
        addDeleteButtonListener(username, dbHelper);
    }

    private void addBackButtonListener()
    {
        btn_j_viewStudentBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ViewStudentScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addEditInfoButtonListener(final String username)
    {
        btn_j_viewStudentEditInfoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Create intent to open UpdateStudentInfoScreen and pass the username
                Intent intent = new Intent(ViewStudentScreen.this, UpdateStudentInfoScreen.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    private void addDeleteButtonListener(final String username, final DatabaseHelper dbHelper)
    {
        btn_j_viewStudentDeleteStudentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Delete the student from the database
                boolean deleted = dbHelper.deleteStudent(username);
                if (deleted)
                {
                    //Go back to the main screen
                    Intent intent = new Intent(ViewStudentScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(ViewStudentScreen.this, "Failed to delete student", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayStudentInfo(String username, DatabaseHelper dbHelper) {
        Cursor cursor = dbHelper.getStudentByUsername(username);
        if (cursor != null && cursor.moveToFirst()) {
            // Set text views with student data
            tv_j_viewStudentUsername.setText(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow("fname")) + " " + cursor.getString(cursor.getColumnIndexOrThrow("lname"));
            tv_j_viewStudentFullName.setText(fullName);
            tv_j_viewStudentEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            tv_j_viewStudentAge.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("age"))));
            tv_j_viewStudentGPA.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("GPA"))));
            tv_j_viewStudentMajor.setText(cursor.getString(cursor.getColumnIndexOrThrow("major")));
        }
        cursor.close();
    }
}
