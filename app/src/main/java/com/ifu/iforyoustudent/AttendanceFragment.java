package com.ifu.iforyoustudent;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;

public class AttendanceFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Attendance> attendanceArrayList;
    private AttendanceAdapter attendanceAdapter;

    private Cursor cursor = null;
    private Uri CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/attendance");

    private Uri MODULE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/module");

    private Uri ATTENDANCE_PERCENT_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/attendancepertageoverall");

    private Uri ATTENDANCE_PERCENT_LAST_CONTENT_URI = Uri.parse("content://com.ifu" +
            ".contentprovider" +
            ".provider/attendancepertagelastweek");
    private Uri ATTENDANCE_PERCENT_THIS_CONTENT_URI = Uri.parse("content://com.ifu" +
            ".contentprovider" +
            ".provider/attendancepertagethisweek");
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";
    String loginName, loginRole = null;

    private Button btnSearch, btnOverallAttendance, btnLastWeekAttendance, btnThisWeekAttendance;
    private boolean validDropdown = false;
    private AutoCompleteTextView module;
    private String attendance = null;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> moduleName;

    int noOfClassesThisWeek = 0;
    int attendedClassThisWeek = 0;
    float attendancePercentageThisWeek = 0;

    int noOfClasses = 0;
    int attendedClass = 0;
    float attendancePercentage = 0;

    int noOfClassesLastWeek = 0;
    int attendedClassLastWeek = 0;
    float attendancePercentageLastWeek = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_attendance, container, false);


        btnOverallAttendance = view.findViewById(R.id.btnOverall);
        btnLastWeekAttendance = view.findViewById(R.id.btnLastWeek);
        btnThisWeekAttendance = view.findViewById(R.id.btnThisWeek);

        attendanceArrayList = new ArrayList<>();

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PRE_NAME,MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        btnSearch = view.findViewById(R.id.btnSearch);
        module = view.findViewById(R.id.module);

        moduleName = new ArrayList<>();

        cursor = getActivity().getContentResolver().query(MODULE_CONTENT_URI, null, null,
                new String[]{loginName}, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext())
                {
                    moduleName.add(cursor.getString(cursor.getColumnIndex("moduleName")));
                }
            }
        }

        cursor = getActivity().getContentResolver().query(ATTENDANCE_PERCENT_CONTENT_URI, null, null,
                new String[]{loginName,"0"}, null);



        if (cursor != null) {
            noOfClasses = cursor.getCount();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext())
                {


                    if(cursor.getString(cursor.getColumnIndex("attendance")).equals("1"))
                    {
                        attendedClass = attendedClass + 1;
                    }
                }
            }

            attendancePercentage =
                    (Float.parseFloat(String.valueOf(attendedClass))/Float.parseFloat(String.valueOf(noOfClasses)) * 100);
        }


        btnOverallAttendance.setText(Math.round(attendancePercentage) + " %");


        cursor = getActivity().getContentResolver().query(ATTENDANCE_PERCENT_LAST_CONTENT_URI, null, null,
                new String[]{loginName,"0"}, null);



        if (cursor != null) {
            noOfClassesLastWeek = cursor.getCount();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext())
                {

                    if(cursor.getString(cursor.getColumnIndex("attendance")).equals("1"))
                    {
                        attendedClassLastWeek = attendedClassLastWeek + 1;
                    }
                }
            }

            attendancePercentageLastWeek =
                    (Float.parseFloat(String.valueOf(attendedClassLastWeek))/Float.parseFloat(String.valueOf(noOfClassesLastWeek)) * 100);
        }

        btnLastWeekAttendance.setText(Math.round(attendancePercentageLastWeek) + " %");

        cursor = getActivity().getContentResolver().query(ATTENDANCE_PERCENT_THIS_CONTENT_URI, null, null,
                new String[]{loginName,"0"}, null);



        if (cursor != null) {
            noOfClassesThisWeek = cursor.getCount();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext())
                {

                    if(cursor.getString(cursor.getColumnIndex("attendance")).equals("1"))
                    {
                        attendedClassThisWeek = attendedClassThisWeek + 1;
                    }
                }
            }

            //Log.d("TAG", "onCreateView: " + attendedClassThisWeek + " " + noOfClassesThisWeek);
            attendancePercentageThisWeek =
                    (Float.parseFloat(String.valueOf(attendedClassThisWeek))/Float.parseFloat(String.valueOf(noOfClassesThisWeek)) * 100);
        }

        btnThisWeekAttendance.setText(Math.round(attendancePercentageThisWeek) + " %");

        recyclerView = view.findViewById(R.id.recyclerView);
        arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.list_item,moduleName);
        module.setAdapter(arrayAdapter);


        btnSearch.setOnClickListener(view->{

            attendanceAdapter = new AttendanceAdapter(attendanceArrayList);

            attendanceArrayList.clear();
            if(checkDropDownEmpty(module))
            {
                cursor = getActivity().getContentResolver().query(CONTENT_URI, null, null,
                        new String[]{loginName, module.getText().toString()}, null);

                if (cursor != null) {
                    //Log.d("TAG", "onCreateView: "+cursor.getCount());
                    if (cursor.getCount() > 0) {

                        while (cursor.moveToNext())
                        {
                            if(cursor.getString(cursor.getColumnIndex("attendance")).equals("0"))
                            {
                                attendance = "Absent";
                            }
                            else {
                                attendance = "Present";
                            }
                            attendanceArrayList.add(new Attendance(cursor.getString(cursor.getColumnIndex("date")),attendance));
                        }
                    }
                }


            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(attendanceAdapter);



            attendedClass = 0;
            noOfClasses = 0;
            attendancePercentage = 0;
            cursor = getActivity().getContentResolver().query(ATTENDANCE_PERCENT_CONTENT_URI, null, null,
                    new String[]{loginName,module.getText().toString()}, null);



            if (cursor != null) {
                noOfClasses = cursor.getCount();
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext())
                    {


                        if(cursor.getString(cursor.getColumnIndex("attendance")).equals("1"))
                        {
                            attendedClass = attendedClass + 1;
                        }
                    }
                }

                attendancePercentage =
                        (Float.parseFloat(String.valueOf(attendedClass))/Float.parseFloat(String.valueOf(noOfClasses)) * 100);
            }
            //Log.d("TAG", "onCreateView: "+attendedClass + " " + noOfClasses);

            btnOverallAttendance.setText(Math.round(attendancePercentage) + " %");


            noOfClassesLastWeek = 0;
            attendancePercentageLastWeek = 0;
            attendedClassLastWeek = 0;

            cursor = getActivity().getContentResolver().query(ATTENDANCE_PERCENT_LAST_CONTENT_URI, null, null,
                    new String[]{loginName,module.getText().toString()}, null);

            if (cursor != null) {
                noOfClassesLastWeek = cursor.getCount();
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext())
                    {
                        if(cursor.getString(cursor.getColumnIndex("attendance")).equals("1"))
                        {
                            attendedClassLastWeek = attendedClassLastWeek + 1;
                        }
                    }

                }
                Log.d("TAG",
                        "onCreateView: "+attendedClassLastWeek + " " + noOfClassesLastWeek +
                                " " +cursor.getCount());


                attendancePercentageLastWeek =
                        (Float.parseFloat(String.valueOf(attendedClassLastWeek))/Float.parseFloat(String.valueOf(noOfClassesLastWeek)) * 100);
            }

            btnLastWeekAttendance.setText(Math.round(attendancePercentageLastWeek) + " %");

            noOfClassesThisWeek = 0;
            attendancePercentageThisWeek = 0;
            attendedClassThisWeek = 0;

            cursor = getActivity().getContentResolver().query(ATTENDANCE_PERCENT_THIS_CONTENT_URI, null, null,
                    new String[]{loginName,module.getText().toString()}, null);

            if (cursor != null) {
                noOfClassesThisWeek = cursor.getCount();
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext())
                    {

                        if(cursor.getString(cursor.getColumnIndex("attendance")).equals("1"))
                        {
                            attendedClassThisWeek = attendedClassThisWeek + 1;
                        }
                    }
                }

                //Log.d("TAG", "onCreateView: " + attendedClassThisWeek + " " +
                // noOfClassesThisWeek);
                attendancePercentageThisWeek =
                        (Float.parseFloat(String.valueOf(attendedClassThisWeek))/Float.parseFloat(String.valueOf(noOfClassesThisWeek)) * 100);
            }

            btnThisWeekAttendance.setText(Math.round(attendancePercentageThisWeek) + " %");





        });





        return view;
    }

    public boolean checkDropDownEmpty(AutoCompleteTextView autoCompleteTextView){
        validDropdown = false;
        if(autoCompleteTextView.getVisibility() == View.VISIBLE){
            if(!autoCompleteTextView.getText().toString().isEmpty())
            {
                autoCompleteTextView.setError(null);
                validDropdown = true;
            }
            else
            {
                autoCompleteTextView.setError("Please select the value");
                validDropdown = false;
            }
        }
        else
        {
            validDropdown = true;
        }
        return validDropdown;
    }
}