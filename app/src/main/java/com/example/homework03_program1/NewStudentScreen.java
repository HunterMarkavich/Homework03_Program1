package com.example.homework03_program1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import java.util.ArrayList;

public class NewStudentScreen extends AppCompatActivity
{
    //Declare the views for the text, spinners, and buttons
    TextView tv_j_newStudentHeader;
    TextView tv_j_newStudentUserName;
    TextView tv_j_newStudentFirstName;
    TextView tv_j_newStudentLastName;
    TextView tv_j_newStudentEmail;
    TextView tv_j_newStudentAge;
    TextView tv_j_newStudentGPA;
    TextView tv_j_newStudentMajor;
    EditText et_j_newStudentAddUserName;
    EditText et_j_newStudentAddFirstName;
    EditText et_j_newStudentAddLastName;
    EditText et_j_newStudentAddEmail;
    EditText et_j_newStudentAddAge;
    EditText et_j_newStudentAddGPA;
    Spinner sp_j_newStudentDropDown;
    Button btn_j_newStudentAddNewStudentButton;
    Button btn_j_newStudentBackButton;
    TextView tv_j_newStudentAddUsername_error;
    TextView tv_j_newStudentAddEmail_error;
    TextView tv_j_newStudentAddStudent_error;

    //Database helper and adapter for the dropdown
    DatabaseHelper dbHelper;
    ArrayAdapter<String> majorAdapter;
    ArrayList<String> majorList;
    String selectedMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_student_screen_activity);

        tv_j_newStudentHeader = findViewById(R.id.tv_v_newStudentHeader);
        tv_j_newStudentUserName = findViewById(R.id.tv_v_viewStudentNameHere);
        tv_j_newStudentFirstName = findViewById(R.id.tv_v_newStudentFirstName);
        tv_j_newStudentLastName = findViewById(R.id.tv_v_newStudentLastName);
        tv_j_newStudentEmail = findViewById(R.id.tv_v_newStudentEmail);
        tv_j_newStudentAge = findViewById(R.id.tv_v_newStudentAge);
        tv_j_newStudentGPA = findViewById(R.id.tv_v_newStudentGPA);
        tv_j_newStudentMajor = findViewById(R.id.tv_v_newStudentMajor);
        et_j_newStudentAddUserName = findViewById(R.id.et_v_NewStudentAddUserName);
        et_j_newStudentAddFirstName = findViewById(R.id.et_v_newStudentAddFirstName);
        et_j_newStudentAddLastName = findViewById(R.id.et_v_NewStudentAddLastName);
        et_j_newStudentAddEmail = findViewById(R.id.et_v_newStudentAddEmail);
        et_j_newStudentAddAge = findViewById(R.id.et_v_newStudentAddAge);
        et_j_newStudentAddGPA = findViewById(R.id.et_v_newStudentAddGPA);
        sp_j_newStudentDropDown = findViewById(R.id.sp_v_newStudentDropDown);
        btn_j_newStudentAddNewStudentButton = findViewById(R.id.btn_v_newStudentAddStudentButton);
        btn_j_newStudentBackButton = findViewById(R.id.btn_v_newStudentBackButton);

        //Initialize error message TextViews and set them to invisible
        tv_j_newStudentAddUsername_error = findViewById(R.id.tv_v_newStudentAddUsername_error);
        tv_j_newStudentAddEmail_error = findViewById(R.id.tv_v_newStudentAddEmail_error);
        tv_j_newStudentAddStudent_error = findViewById(R.id.tv_v_newStudentAddStudent_error);
        tv_j_newStudentAddUsername_error.setVisibility(View.INVISIBLE);
        tv_j_newStudentAddEmail_error.setVisibility(View.INVISIBLE);
        tv_j_newStudentAddStudent_error.setVisibility(View.INVISIBLE);

        //Initialize DatabaseHelper and setup the spinner with the majors
        dbHelper = new DatabaseHelper(this);
        loadMajorsIntoSpinner();

        //Listeners for buttons and spinner
        addStudentButtonListener();
        addBackButtonListener();
        spinnerEventListener();
    }

    //Load our majors from the database into the Spinner dropdown
    private void loadMajorsIntoSpinner()
    {
        //Get an instance of the DatabaseHelper to retrieve majors
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        //Retrieve all majors from the database
        Cursor cursor = dbHelper.getAllMajors();

        //Create a list to store majors in the format: "Major Name (Prefix)"
        ArrayList<String> majorsList = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String majorName = cursor.getString(cursor.getColumnIndex("majorName"));
            String majorPrefix = cursor.getString(cursor.getColumnIndex("majorPrefix"));
            majorsList.add(majorName + " (" + majorPrefix + ")");
        }

        //Ensure the cursor is closed after use
        cursor.close();

        //Set up the adapter with the updated list and set it to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, majorsList);
        sp_j_newStudentDropDown.setAdapter(adapter);
    }

    //Sets the selected major based on the users choice in the Spinner
    private void spinnerEventListener()
    {
        sp_j_newStudentDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedMajorWithPrefix = adapterView.getItemAtPosition(i).toString();
                //Extract and store only the major prefix (e.g., "CIS" from "Computer Info Systems (CIS)")
                selectedMajor = selectedMajorWithPrefix.substring(selectedMajorWithPrefix.indexOf("(") + 1, selectedMajorWithPrefix.indexOf(")")).trim();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { /* no action needed */ }
        });
    }

    //Adds a new student using the input fields and returns to MainActivity
    private void addStudentButtonListener()
    {
        btn_j_newStudentAddNewStudentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                tv_j_newStudentAddUsername_error.setVisibility(View.INVISIBLE);
                tv_j_newStudentAddEmail_error.setVisibility(View.INVISIBLE);
                tv_j_newStudentAddStudent_error.setVisibility(View.INVISIBLE);

                //Get input values
                String username = et_j_newStudentAddUserName.getText().toString().trim();
                String firstName = et_j_newStudentAddFirstName.getText().toString().trim();
                String lastName = et_j_newStudentAddLastName.getText().toString().trim();
                String email = et_j_newStudentAddEmail.getText().toString().trim();
                String ageText = et_j_newStudentAddAge.getText().toString().trim();
                String gpaText = et_j_newStudentAddGPA.getText().toString().trim();

                //Check for empty fields and display error message if any are missing
                if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || ageText.isEmpty() || gpaText.isEmpty())
                {
                    tv_j_newStudentAddStudent_error.setText("Error: All fields must be filled out");
                    tv_j_newStudentAddStudent_error.setVisibility(View.VISIBLE);
                    return;
                }

                //Check for duplicate username and email
                if (dbHelper.isUsernameTaken(username))
                {
                    tv_j_newStudentAddUsername_error.setText("Error: Username already exists");
                    tv_j_newStudentAddUsername_error.setVisibility(View.VISIBLE);
                    return;
                }
                if (dbHelper.isEmailTaken(email))
                {
                    tv_j_newStudentAddEmail_error.setText("Error: Email already exists");
                    tv_j_newStudentAddEmail_error.setVisibility(View.VISIBLE);
                    return;
                }

                //Convert and validate age and GPA
                int age;
                double gpa;
                try
                {
                    age = Integer.parseInt(ageText);
                    gpa = Double.parseDouble(gpaText);
                }
                catch (NumberFormatException e)
                {
                    tv_j_newStudentAddStudent_error.setText("Error: Enter valid numbers for age and GPA");
                    tv_j_newStudentAddStudent_error.setVisibility(View.VISIBLE);
                    return;
                }

                //Add the student to the database
                boolean isInserted = dbHelper.addStudent(username, firstName, lastName, email, age, gpa, selectedMajor);
                if (isInserted)
                {
                    startActivity(new Intent(NewStudentScreen.this, MainActivity.class));
                }
                else
                {
                    tv_j_newStudentAddStudent_error.setText("Error: Failed to add student");
                    tv_j_newStudentAddStudent_error.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addBackButtonListener()
    {
        btn_j_newStudentBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NewStudentScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
