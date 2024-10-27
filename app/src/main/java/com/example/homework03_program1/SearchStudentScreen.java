package com.example.homework03_program1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchStudentScreen extends AppCompatActivity
{
    TextView tv_j_searchStudentHeader;
    EditText et_j_searchStudentUsername;
    EditText et_j_searchStudentName;
    EditText et_j_searchStudentGPARange;
    Spinner sp_j_searchStudentMajor;
    Button btn_j_searchStudentSearchButton;
    ListView lv_j_searchStudentResults;
    Button btn_j_searchStudentBackButton;

    StudentAdapter resultsAdapter;
    DatabaseHelper dbHelper;
    HashMap<String, String> majorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.search_student_screen_activity);

        // Initialize views
        tv_j_searchStudentHeader = findViewById(R.id.tv_v_searchStudentHeader);
        et_j_searchStudentUsername = findViewById(R.id.et_v_searchStudentUsername);
        et_j_searchStudentName = findViewById(R.id.et_v_searchStudentName);
        et_j_searchStudentGPARange = findViewById(R.id.et_v_searchStudentGPARange);
        sp_j_searchStudentMajor = findViewById(R.id.sp_v_searchStudentMajor);
        btn_j_searchStudentSearchButton = findViewById(R.id.btn_v_searchStudentSearchButton);
        lv_j_searchStudentResults = findViewById(R.id.lv_v_searchStudentResults);
        btn_j_searchStudentBackButton = findViewById(R.id.btn_v_searchStudentBackButton);

        dbHelper = new DatabaseHelper(this);

        //Set up major spinner with options
        loadMajorsIntoSpinner();

        //Initialize major map for abbreviation matching
        initializeMajorMap();

        //Initialize the adapter with a null cursor, as results will load on search
        resultsAdapter = new StudentAdapter(this, null);
        lv_j_searchStudentResults.setAdapter(resultsAdapter);

        //Set listeners
        addBackButtonListener();
        addSearchButtonListener();

        //Set item click listener for ListView to open ViewStudentScreen
        setListViewClickListener();
    }

    private void loadMajorsIntoSpinner()
    {
        Cursor cursor = dbHelper.getAllMajors();
        ArrayList<String> majorsList = new ArrayList<>();

        //Add a blank option for optional major selection
        //This will appear as a blank item in the dropdown
        majorsList.add("");

        while (cursor.moveToNext())
        {
            String majorName = cursor.getString(cursor.getColumnIndexOrThrow("majorName"));
            String majorPrefix = cursor.getString(cursor.getColumnIndexOrThrow("majorPrefix"));
            majorsList.add(majorName + " (" + majorPrefix + ")");
        }
        cursor.close();

        ArrayAdapter<String> majorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, majorsList);
        sp_j_searchStudentMajor.setAdapter(majorAdapter);

        //Set the blank option as the default selection
        sp_j_searchStudentMajor.setSelection(0);
    }

    private void initializeMajorMap()
    {
        majorMap = new HashMap<>();
        majorMap.put("Computer Info Systems", "CIS");
        majorMap.put("Business", "BUS");
        majorMap.put("Engineering", "ENG");
    }

    private void addBackButtonListener()
    {
        btn_j_searchStudentBackButton.setOnClickListener(view ->
        {
            Intent intent = new Intent(SearchStudentScreen.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void addSearchButtonListener()
    {
        btn_j_searchStudentSearchButton.setOnClickListener(view -> performSearch());
    }

    private void performSearch()
    {
        String name = et_j_searchStudentName.getText().toString().trim();
        String username = et_j_searchStudentUsername.getText().toString().trim();
        String gpaRange = et_j_searchStudentGPARange.getText().toString().trim();

        //Get selected major, including the blank option
        String selectedMajorWithPrefix = sp_j_searchStudentMajor.getSelectedItem() != null ? sp_j_searchStudentMajor.getSelectedItem().toString() : "";
        String majorPrefix = selectedMajorWithPrefix;

        if (selectedMajorWithPrefix.contains("(") && selectedMajorWithPrefix.contains(")"))
        {
            majorPrefix = selectedMajorWithPrefix.substring(selectedMajorWithPrefix.indexOf("(") + 1, selectedMajorWithPrefix.indexOf(")")).trim();
        }
        else if (majorMap.containsKey(selectedMajorWithPrefix))
        {
            majorPrefix = majorMap.get(selectedMajorWithPrefix);
        }

        //Only include major in search if it is not blank
        if (majorPrefix.isEmpty())
        {
            majorPrefix = "";
        }

        Log.d("SearchStudentScreen", "Searching with major prefix: " + (majorPrefix.isEmpty() ? "None" : majorPrefix));

        Cursor cursor = dbHelper.searchStudents(name, username, gpaRange, majorPrefix);
        resultsAdapter.changeCursor(cursor);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //Close the adapter cursor when the activity is destroyed
        if (resultsAdapter.getCursor() != null)
        {
            resultsAdapter.getCursor().close();
        }
    }

    private void setListViewClickListener()
    {
        lv_j_searchStudentResults.setOnItemClickListener((parent, view, position, id) ->
        {
            //Get the clicked student's username from the cursor
            Cursor cursor = (Cursor) resultsAdapter.getItem(position);
            String username = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

            //Create an intent to open ViewStudentScreen and pass the username
            Intent viewStudentIntent = new Intent(SearchStudentScreen.this, ViewStudentScreen.class);
            viewStudentIntent.putExtra("username", username);
            startActivity(viewStudentIntent);
        });
    }
}