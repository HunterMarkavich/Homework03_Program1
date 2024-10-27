package com.example.homework03_program1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class NewMajorScreen extends AppCompatActivity
{
    TextView tv_j_newMajorHeader;
    TextView tv_j_newMajorName;
    TextView tv_j_newMajorPrefix;
    EditText et_j_newMajorNameInput;
    EditText et_j_newMajorPrefixInput;
    Button btn_j_newMajorAddMajorButton;
    Button btn_j_newMajorBackButton;
    TextView tv_j_newMajorAddMajor_error;

    //Database helper instance
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_major_screen_activity);

        tv_j_newMajorHeader = findViewById(R.id.tv_v_newMajorHeader);
        tv_j_newMajorName = findViewById(R.id.tv_v_newMajorName);
        tv_j_newMajorPrefix = findViewById(R.id.tv_v_newMajorPrefix);
        et_j_newMajorNameInput = findViewById(R.id.et_v_newMajorNameInput);
        et_j_newMajorPrefixInput = findViewById(R.id.et_v_newMajorPrefixInput);
        btn_j_newMajorAddMajorButton = findViewById(R.id.btn_v_newMajorAddMajorButton);
        btn_j_newMajorBackButton = findViewById(R.id.btn_v_newMajorBackButton);

        //Set error TextView to invisible initially
        tv_j_newMajorAddMajor_error = findViewById(R.id.tv_v_newMajorAddMajor_error);
        tv_j_newMajorAddMajor_error.setVisibility(View.INVISIBLE);

        //Initialize database helper
        dbHelper = new DatabaseHelper(this);

        //Add Back Button listener
        addMajorButtonListener();
        addBackButtonListener();
    }

    //Listener for the Add Major button
    private void addMajorButtonListener()
    {
        btn_j_newMajorAddMajorButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Reset error message visibility
                tv_j_newMajorAddMajor_error.setVisibility(View.INVISIBLE);

                //Retrieve input values
                String majorName = et_j_newMajorNameInput.getText().toString().trim();
                String majorPrefix = et_j_newMajorPrefixInput.getText().toString().trim();

                //Validate inputs for empty fields
                if (majorName.isEmpty() || majorPrefix.isEmpty())
                {
                    tv_j_newMajorAddMajor_error.setText("Error: Both major name and prefix are required.");
                    tv_j_newMajorAddMajor_error.setVisibility(View.VISIBLE);
                    return;
                }

                //Capitalize the first letter of each word in majorName and set prefix to uppercase
                majorName = capitalizeWords(majorName);
                majorPrefix = majorPrefix.toUpperCase();

                //Check if the major or prefix already exists
                if (dbHelper.isMajorNameTaken(majorName) || dbHelper.isMajorPrefixTaken(majorPrefix))
                {
                    tv_j_newMajorAddMajor_error.setText("Error: This major or prefix already exists.");
                    tv_j_newMajorAddMajor_error.setVisibility(View.VISIBLE);
                    return;
                }

                //Insert new major into database
                boolean isInserted = dbHelper.addMajor(majorName, majorPrefix);

                if (isInserted)
                {
                    //Display success message
                    Intent intent = new Intent(NewMajorScreen.this, MainActivity.class);
                    intent.putExtra("NewMajorAdded", true);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    tv_j_newMajorAddMajor_error.setText("Error: Failed to add major.");
                    tv_j_newMajorAddMajor_error.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Method to capitalize the first letter of each word in a string
    private String capitalizeWords(String input)
    {
        String[] words = input.split(" ");
        StringBuilder capitalizedWords = new StringBuilder();
        for (String word : words)
        {
            if (!word.isEmpty())
            {
                capitalizedWords.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return capitalizedWords.toString().trim();
    }

    private void addBackButtonListener()
    {
        btn_j_newMajorBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NewMajorScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
