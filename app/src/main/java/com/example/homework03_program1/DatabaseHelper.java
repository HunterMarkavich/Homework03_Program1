package com.example.homework03_program1;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "School.db";
    private static final String STUDENTS_TABLE_NAME = "Students";
    private static final String MAJORS_TABLE_NAME = "Majors";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 22);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create tables
        db.execSQL("CREATE TABLE " + STUDENTS_TABLE_NAME + " (username TEXT PRIMARY KEY NOT NULL, fname TEXT, lname TEXT, email TEXT, age INTEGER, GPA REAL, major TEXT);");
        db.execSQL("CREATE TABLE " + MAJORS_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, majorName TEXT, majorPrefix TEXT);");

        //Initialize tables with data
        initStudents(db);
        initMajors(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Drop and recreate tables
        db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MAJORS_TABLE_NAME);
        onCreate(db);
    }

    //Initialize students with dummy data if table is empty
    public void initStudents(SQLiteDatabase db)
    {
        if (countRecordsFromTable(STUDENTS_TABLE_NAME, db) == 0)
        {
            db.execSQL("INSERT INTO " + STUDENTS_TABLE_NAME + " (username, fname, lname, email, age, GPA, major) VALUES ('dcantan', 'Dan', 'Cantan', 'dcantan@gmail.com', 20, 3.5, 'CIS');");
            db.execSQL("INSERT INTO " + STUDENTS_TABLE_NAME + " (username, fname, lname, email, age, GPA, major) VALUES ('asmith', 'Alex', 'Smith', 'alexsmith@yahoo.mail', 22, 3.8, 'BUS');");
            db.execSQL("INSERT INTO " + STUDENTS_TABLE_NAME + " (username, fname, lname, email, age, GPA, major) VALUES ('bmazinger', 'Bob', 'Mazinger', 'mazingerb@hotmail.com', 19, 3.2, 'ENG');");
            db.execSQL("INSERT INTO " + STUDENTS_TABLE_NAME + " (username, fname, lname, email, age, GPA, major) VALUES ('jdoe', 'John', 'Doe', 'johndoe@example.com', 21, 3.6, 'CIS');");
            db.execSQL("INSERT INTO " + STUDENTS_TABLE_NAME + " (username, fname, lname, email, age, GPA, major) VALUES ('mjackson', 'Mary', 'Jackson', 'maryj@example.com', 23, 3.9, 'BUS');");
            db.execSQL("INSERT INTO " + STUDENTS_TABLE_NAME + " (username, fname, lname, email, age, GPA, major) VALUES ('tclark', 'Tom', 'Clark', 'tomclark@example.com', 20, 2.9, 'ENG');");
            db.execSQL("INSERT INTO " + STUDENTS_TABLE_NAME + " (username, fname, lname, email, age, GPA, major) VALUES ('slee', 'Sarah', 'Lee', 'slee@example.com', 24, 3.1, 'CIS');");
            db.execSQL("INSERT INTO " + STUDENTS_TABLE_NAME + " (username, fname, lname, email, age, GPA, major) VALUES ('pmartin', 'Paul', 'Martin', 'pmartin@example.com', 22, 3.7, 'BUS');");
        }
    }

    //Initialize majors with dummy data if table is empty
    public void initMajors(SQLiteDatabase db)
    {
        if (countRecordsFromTable(MAJORS_TABLE_NAME, db) == 0)
        {
            db.execSQL("INSERT INTO " + MAJORS_TABLE_NAME + " (majorName, majorPrefix) VALUES ('Computer Info Systems', 'CIS');");
            db.execSQL("INSERT INTO " + MAJORS_TABLE_NAME + " (majorName, majorPrefix) VALUES ('Business', 'BUS');");
            db.execSQL("INSERT INTO " + MAJORS_TABLE_NAME + " (majorName, majorPrefix) VALUES ('Engineering', 'ENG');");
        }
    }

    //Count the number of records in a specified table
    public int countRecordsFromTable(String tableName, SQLiteDatabase db)
    {
        return (int) DatabaseUtils.queryNumEntries(db, tableName);
    }

    //Add a new student to the database
    public boolean addStudent(String username, String fname, String lname, String email, int age, double GPA, String major)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("fname", capitalizeWords(fname));
        values.put("lname", capitalizeWords(lname));
        values.put("email", email);
        values.put("age", age);
        values.put("GPA", GPA);
        //Store only the major prefix
        values.put("major", major);

        long result = db.insert(STUDENTS_TABLE_NAME, null, values);

        if (result == -1)
        {
            Log.e("Database Error:", "Failed to insert student: " + username);
            return false;
        }
        else
        {
            Log.d("DatabaseHelper", "Student added successfully: " + username);
            return true;
        }
    }

    //Add a new major to the database
    public boolean addMajor(String majorName, String majorPrefix)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //Normalize input: capitalize major name and make prefix uppercase
        values.put("majorName", capitalizeWords(majorName));
        values.put("majorPrefix", majorPrefix.toUpperCase());

        //Check if major already exists
        Cursor cursor = db.query(MAJORS_TABLE_NAME, new String[]{"id"}, "majorName = ? COLLATE NOCASE OR majorPrefix = ? COLLATE NOCASE", new String[]{majorName, majorPrefix}, null, null, null);

        if (cursor != null && cursor.getCount() > 0)
        {
            Log.d("DatabaseHelper", "Major already exists: " + majorName);
            cursor.close();
            db.close();
            //Major or prefix already exists
            return false;
        }

        if (cursor != null) cursor.close();

        //Insert the new major
        long result = db.insert(MAJORS_TABLE_NAME, null, values);

        //Check insert result
        if (result == -1)
        {
            Log.e("Database Error:", "Failed to insert major: " + majorName);
            db.close();
            return false;
        }
        else
        {
            Log.d("DatabaseHelper", "Major added successfully: " + majorName + " with prefix: " + majorPrefix + " | Insert result ID: " + result);
            db.close();
            return true;
        }
    }

    public Cursor getAllStudents()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        //Select columns directly from Students table
        return db.rawQuery("SELECT username AS _id, fname, lname, email, age, GPA, major FROM " + STUDENTS_TABLE_NAME, null);
    }

    //Retrieve all majors
    public Cursor getAllMajors()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(MAJORS_TABLE_NAME, new String[]{"id", "majorName", "majorPrefix"}, null, null, null, null, "majorName ASC");
    }

    //Helper to capitilize each word in the major name
    private String capitalizeWords(String input)
    {
        String[] words = input.toLowerCase().split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words)
        {
            if (word.length() > 0)
            {
                capitalized.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    //Check if a username is already in the Students table
    public boolean isUsernameTaken(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Students WHERE username = ?", new String[]{username});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    //Check if an email is already in the Students table
    public boolean isEmailTaken(String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Students WHERE email = ?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    //Method to check if a major name already exists
    public boolean isMajorNameTaken(String majorName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MAJORS_TABLE_NAME + " WHERE majorName = ?", new String[]{majorName});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    //Method to check if a major prefix already exists
    public boolean isMajorPrefixTaken(String majorPrefix)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MAJORS_TABLE_NAME + " WHERE majorPrefix = ?", new String[]{majorPrefix});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public Cursor getStudentByUsername(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + STUDENTS_TABLE_NAME + " WHERE username = ?", new String[]{username});
    }

    public boolean updateStudent(String username, String fname, String lname, String email, int age, double gpa, String major)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fname", fname);
        values.put("lname", lname);
        values.put("email", email);
        values.put("age", age);
        values.put("GPA", gpa);
        values.put("major", major);

        int rowsAffected = db.update(STUDENTS_TABLE_NAME, values, "username = ?", new String[]{username});
        return rowsAffected > 0;
    }

    public boolean isEmailTakenByOtherStudent(String email, String currentUsername) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Students WHERE email = ? AND username != ?", new String[]{email, currentUsername});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean isUsernameTakenByOtherStudent(String newUsername, String currentUsername)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Students WHERE username = ? AND username != ?", new String[]{newUsername, currentUsername});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean deleteStudent(String username)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("Students", "username = ?", new String[]{username});
        db.close();
        return rowsAffected > 0;
    }

    public Cursor searchStudents(String name, String username, String gpaRange, String major)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder query = new StringBuilder("SELECT username AS _id, fname, lname, email, age, GPA, major FROM Students WHERE 1=1");
        List<String> argsList = new ArrayList<>();

        if (!name.isEmpty())
        {
            query.append(" AND (fname LIKE ? COLLATE NOCASE OR lname LIKE ? COLLATE NOCASE)");
            argsList.add("%" + name + "%");
            argsList.add("%" + name + "%");
        }

        if (!username.isEmpty())
        {
            query.append(" AND username = ? COLLATE NOCASE");
            argsList.add(username);
        }

        if (!gpaRange.isEmpty())
        {
            String[] gpaBounds = gpaRange.split("-");
            if (gpaBounds.length == 2)
            {
                query.append(" AND GPA BETWEEN ? AND ?");
                argsList.add(gpaBounds[0].trim());
                argsList.add(gpaBounds[1].trim());
            }
        }

        //Only add major to the query if it is not blank
        if (!major.isEmpty())
        {
            query.append(" AND major = ? COLLATE NOCASE");
            argsList.add(major);
        }

        Log.d("DatabaseHelper", "Query: " + query.toString());
        Log.d("DatabaseHelper", "Args: " + Arrays.toString(argsList.toArray()));

        String[] args = argsList.toArray(new String[0]);
        return db.rawQuery(query.toString(), args);
    }
}