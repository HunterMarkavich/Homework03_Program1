package com.example.homework03_program1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateStudentInfoScreen extends AppCompatActivity
{
    TextView tv_j_updateInfoHeader;
    TextView tv_j_updateInfoUsername;
    TextView tv_j_updateInfoFirstName;
    TextView tv_j_updateInfoLastName;
    TextView tv_j_updateInfoEmail;
    TextView tv_j_updateInfoAge;
    TextView tv_j_updateInfoGPA;
    TextView tv_j_updateInfoMajor;
    EditText et_j_updateInfoFirstNameInput;
    EditText et_j_updateInfoLastNameInput;
    EditText et_j_updateInfoEmailInput;
    EditText et_j_updateInfoAgeInput;
    EditText et_j_updateInfoGPAInput;
    Spinner sp_j_updateInfoMajorDropdown;
    Button btn_j_updateInfoUpdateInfoButton;
    Button btn_j_updateInfoBackButton;
    TextView tv_j_updateInfoUpdateInfo_error;

    @Override
    protected void onCreate(Bundle savedInstantState)
    {
        super.onCreate(savedInstantState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.update_student_info_screen_activity);

        //Initialize views
        tv_j_updateInfoHeader = findViewById(R.id.tv_v_updateInfoHeader);
        tv_j_updateInfoUsername = findViewById(R.id.tv_v_updateInfoUsername);
        tv_j_updateInfoFirstName = findViewById(R.id.tv_v_updateInfoFirstName);
        tv_j_updateInfoLastName = findViewById(R.id.tv_v_updateInfoLastName);
        tv_j_updateInfoEmail = findViewById(R.id.tv_v_updateInfoEmail);
        tv_j_updateInfoAge = findViewById(R.id.tv_v_updateInfoAge);
        tv_j_updateInfoGPA = findViewById(R.id.tv_v_updateInfoGPA);
        tv_j_updateInfoMajor = findViewById(R.id.tv_v_updateInfoMajor);
        et_j_updateInfoFirstNameInput = findViewById(R.id.et_v_updateInfoFirstNameInput);
        et_j_updateInfoLastNameInput = findViewById(R.id.et_v_updateInfoLastNameInput);
        et_j_updateInfoEmailInput = findViewById(R.id.et_v_updateInfoEmailInput);
        et_j_updateInfoAgeInput = findViewById(R.id.et_v_updateInfoAgeInput);
        et_j_updateInfoGPAInput = findViewById(R.id.et_v_updateInfoGPAInput);
        sp_j_updateInfoMajorDropdown = findViewById(R.id.sp_v_updateInfoMajorDropdown);
        btn_j_updateInfoUpdateInfoButton = findViewById(R.id.btn_v_updateInfoUpdateInfoButton);
        btn_j_updateInfoBackButton = findViewById(R.id.btn_v_updateInfoBackButton);

        //Set error message TextView to invisible initially
        tv_j_updateInfoUpdateInfo_error = findViewById(R.id.tv_v_updateInfoUpdateInfo_error);
        tv_j_updateInfoUpdateInfo_error.setVisibility(View.INVISIBLE);

        //Retrieve the username from the intent
        String username = getIntent().getStringExtra("username");

        //Fetch and display the student info
        loadStudentInfo(username);

        //Load available majors into the Spinner
        loadMajorsIntoSpinner();

        //Back Button and Update Button Listener
        addBackButtonListener();
        addUpdateButtonListener(username);
    }

    private void loadStudentInfo(String username)
    {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getStudentByUsername(username);

        if (cursor != null && cursor.moveToFirst())
        {
            tv_j_updateInfoUsername.setText(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            et_j_updateInfoFirstNameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("fname")));
            et_j_updateInfoLastNameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("lname")));
            et_j_updateInfoEmailInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            et_j_updateInfoAgeInput.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("age"))));
            et_j_updateInfoGPAInput.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("GPA"))));

            //Set the student's current major in the Spinner after loading majors
            String currentMajor = cursor.getString(cursor.getColumnIndexOrThrow("major"));
            setSpinnerToMajor(currentMajor);
        }
        cursor.close();
    }

    private void loadMajorsIntoSpinner()
    {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getAllMajors();
        ArrayList<String> majorsList = new ArrayList<>();

        // Populate the list with majors in the format "Major Name (Prefix)"
        while (cursor.moveToNext())
        {
            String majorName = cursor.getString(cursor.getColumnIndexOrThrow("majorName"));
            String majorPrefix = cursor.getString(cursor.getColumnIndexOrThrow("majorPrefix"));
            majorsList.add(majorName + " (" + majorPrefix + ")");
        }
        cursor.close();

        // Create adapter and set it to the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, majorsList);
        sp_j_updateInfoMajorDropdown.setAdapter(adapter);
    }

    private void setSpinnerToMajor(String majorPrefix)
    {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) sp_j_updateInfoMajorDropdown.getAdapter();
        if (adapter != null)
        {
            for (int i = 0; i < adapter.getCount(); i++)
            {
                String item = adapter.getItem(i);
                if (item != null && item.contains("(" + majorPrefix + ")"))
                {
                    sp_j_updateInfoMajorDropdown.setSelection(i);
                    break;
                }
            }
        }
    }


    private void addBackButtonListener()
    {
        btn_j_updateInfoBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(UpdateStudentInfoScreen.this, ViewStudentScreen.class);
                startActivity(intent);
            }
        });
    }

    private void addUpdateButtonListener(final String originalUsername) {
        btn_j_updateInfoUpdateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset error message visibility
                tv_j_updateInfoUpdateInfo_error.setVisibility(View.INVISIBLE);

                // Retrieve and validate input values
                String firstName = capitalizeWords(et_j_updateInfoFirstNameInput.getText().toString().trim());
                String lastName = capitalizeWords(et_j_updateInfoLastNameInput.getText().toString().trim());
                String newUsername = tv_j_updateInfoUsername.getText().toString().trim();
                String newEmail = et_j_updateInfoEmailInput.getText().toString().trim();
                String ageStr = et_j_updateInfoAgeInput.getText().toString().trim();
                String gpaStr = et_j_updateInfoGPAInput.getText().toString().trim();
                String major = sp_j_updateInfoMajorDropdown.getSelectedItem().toString();

                // Extract only the major prefix, e.g., "BUS" from "Business (BUS)"
                if (major.contains("(") && major.contains(")")) {
                    major = major.substring(major.indexOf("(") + 1, major.indexOf(")")).trim();
                }

                // Check for empty fields
                if (firstName.isEmpty() || lastName.isEmpty() || newUsername.isEmpty() || newEmail.isEmpty() || ageStr.isEmpty() || gpaStr.isEmpty()) {
                    tv_j_updateInfoUpdateInfo_error.setText("Error: All fields must be filled out");
                    tv_j_updateInfoUpdateInfo_error.setVisibility(View.VISIBLE);
                    return;
                }

                // Validate numeric values for age and GPA
                int age;
                double gpa;
                try {
                    age = Integer.parseInt(ageStr);
                    gpa = Double.parseDouble(gpaStr);
                } catch (NumberFormatException e) {
                    tv_j_updateInfoUpdateInfo_error.setText("Error: Enter valid numbers for age and GPA");
                    tv_j_updateInfoUpdateInfo_error.setVisibility(View.VISIBLE);
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(UpdateStudentInfoScreen.this);

                // Check if the new username is taken by another student
                if (!originalUsername.equals(newUsername) && dbHelper.isUsernameTakenByOtherStudent(newUsername, originalUsername)) {
                    tv_j_updateInfoUpdateInfo_error.setText("Error: Username already exists");
                    tv_j_updateInfoUpdateInfo_error.setVisibility(View.VISIBLE);
                    return;
                }

                // Check if the new email is taken by another student
                if (dbHelper.isEmailTakenByOtherStudent(newEmail, originalUsername)) {
                    tv_j_updateInfoUpdateInfo_error.setText("Error: Email already exists");
                    tv_j_updateInfoUpdateInfo_error.setVisibility(View.VISIBLE);
                    return;
                }

                // Update in database
                boolean isUpdated = dbHelper.updateStudent(newUsername, firstName, lastName, newEmail, age, gpa, major);

                if (isUpdated) {
                    Intent intent = new Intent(UpdateStudentInfoScreen.this, MainActivity.class);
                    intent.putExtra("Updated", true);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UpdateStudentInfoScreen.this, "Update failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Method to capitalize first letter of each word
    private String capitalizeWords(String input) {
        String[] words = input.toLowerCase().split(" ");
        StringBuilder capitalizedWords = new StringBuilder();
        for (String word : words)
        {
            if (!word.isEmpty())
            {
                capitalizedWords.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return capitalizedWords.toString().trim();
    }
}