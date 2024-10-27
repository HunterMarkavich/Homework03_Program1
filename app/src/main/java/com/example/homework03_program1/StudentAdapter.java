package com.example.homework03_program1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CursorAdapter;

public class StudentAdapter extends CursorAdapter
{
    public StudentAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.custom_cell_student_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView usernameView = view.findViewById(R.id.tv_v_cc_username);
        TextView fullNameView = view.findViewById(R.id.tv_v_cc_fullname);
        TextView emailView = view.findViewById(R.id.tv_v_cc_email);
        TextView ageView = view.findViewById(R.id.tv_v_cc_age);
        TextView gpaView = view.findViewById(R.id.tv_v_cc_gpa);
        TextView majorView = view.findViewById(R.id.tv_v_cc_major);

        String username = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String fname = cursor.getString(cursor.getColumnIndexOrThrow("fname"));
        String lname = cursor.getString(cursor.getColumnIndexOrThrow("lname"));
        String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
        int age = cursor.getInt(cursor.getColumnIndexOrThrow("age"));
        double gpa = cursor.getDouble(cursor.getColumnIndexOrThrow("GPA"));
        String major = cursor.getString(cursor.getColumnIndexOrThrow("major")); // Display only the prefix

        usernameView.setText(username);
        fullNameView.setText(fname + " " + lname);
        emailView.setText(email);
        ageView.setText(String.valueOf(age));
        gpaView.setText(String.valueOf(gpa));
        majorView.setText(major); // Directly set major prefix
    }
}
