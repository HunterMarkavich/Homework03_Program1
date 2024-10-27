//Hunter Markavich
//10-21-2024
//Homework03_Program1: CRUD App

package com.example.homework03_program1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    //Declare the views
    TextView tv_j_mainScreenHeader;
    Button btn_j_addStudent;
    Button btn_j_addMajor;
    Button btn_j_searchStudentSearchButton;
    ListView lv_j_studentList;

    //Intent Declarations
    Intent intent_j_addNewStudent;
    Intent intent_j_addNewMajor;
    Intent intent_j_searchStudent;

    //DatabaseHelper and Adapter
    DatabaseHelper dbHelper;
    StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup GUI connections
        tv_j_mainScreenHeader = findViewById(R.id.tv_v_mainScreenHeader);
        btn_j_addStudent      = findViewById(R.id.btn_v_addStudent);
        btn_j_addMajor        = findViewById(R.id.btn_v_addMajor);
        btn_j_searchStudentSearchButton = findViewById(R.id.btn_v_searchStudentSearchButton);
        lv_j_studentList      = findViewById(R.id.lv_v_studentList);

        //Create our intents to go to different screens
        intent_j_addNewStudent = new Intent(MainActivity.this, NewStudentScreen.class);
        intent_j_addNewMajor = new Intent(MainActivity.this, NewMajorScreen.class);
        intent_j_searchStudent = new Intent(MainActivity.this, SearchStudentScreen.class);

        //Setup the button listener for our ADD STUDENT, ADD MAJOR, and SEARCH buttons
        addNewStudentButtonListener();
        addNewMajorButtonListener();
        searchStudentButtonListener();

        //Initialize DatabaseHelper and populate students and majors
        dbHelper = new DatabaseHelper(this);

        //Load our data into the ListView
        displayStudents();

        //Set item click listener for the ListView
        lv_j_studentList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Retrieve the clicked student's username from the cursor
                Cursor cursor = (Cursor) studentAdapter.getItem(position);
                String username = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

                //Create intent and pass the username to the ViewStudentScreen
                Intent viewStudentIntent = new Intent(MainActivity.this, ViewStudentScreen.class);
                viewStudentIntent.putExtra("username", username);
                startActivity(viewStudentIntent);
            }
        });

    }

    //Retrieve all students from the database and display them in the listview
    private void displayStudents()
    {
        Cursor cursor = dbHelper.getAllStudents();
        if (studentAdapter == null)
        {
           //Initialize adapter with cursor data only once
           studentAdapter = new StudentAdapter(this, cursor);
           lv_j_studentList.setAdapter(studentAdapter);
        }
        else
        {
            //Update the adapter if already initialized
            studentAdapter.changeCursor(cursor);
        }
    }

    //Refresh the students list each time this activity is resumed
    @Override
    protected void onResume()
    {
        super.onResume();
        //Refresh data in ListView after returning from another activity
        displayStudents();
    }

    //Create a method to handle the button click and jump to the NewStudentScreen
    private void addNewStudentButtonListener()
    {
        btn_j_addStudent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Pass data with the intent
                startActivity(intent_j_addNewStudent);
            }
        });
    }

    //Create a method to handle the button click and jump to the NewMajorScreen
    private void addNewMajorButtonListener()
    {
        btn_j_addMajor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Pass data with the intent
                startActivity(intent_j_addNewMajor);
            }
        });
    }

    //Create a method to handle the button click and jump to SearchStudentScreen
    private void searchStudentButtonListener()
    {
        btn_j_searchStudentSearchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Pass data with the intent
                startActivity(intent_j_searchStudent);
            }
        });
    }
}