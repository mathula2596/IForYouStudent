package com.ifu.iforyoustudent;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimetableFragment extends Fragment {

    private View view;
    public GregorianCalendar calMonth, calMonthCopy;
    private TimetableAdapter timetableAdapter;
    private TextView txtMonth;
    private Cursor cursor = null;
    private Uri CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/viewtimetable");

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";
    String loginName, loginRole = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_timetable, container, false);

        Timetable.timetableArrayList=new ArrayList<Timetable>();

        sharedPreferences = getActivity().getSharedPreferences(SHARED_PRE_NAME,MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        cursor = getActivity().getContentResolver().query(CONTENT_URI, null, null,
                new String[]{loginName}, null);

        if (cursor != null) {


            if (cursor.getCount() > 0) {
                while (cursor.moveToNext())
                {

                    Date formattedDate= null;
                    String lecturerName = null;
                    String calendarDate = null;
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                        formattedDate = formatter.parse(cursor.getString(cursor.getColumnIndex(
                                        "date")));
                                formatter.applyPattern("yyyy-mm-dd");
                        calendarDate = formatter.format(formattedDate);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    lecturerName = cursor.getString(cursor.getColumnIndex(
                            "firstName")) + " " + cursor.getString(cursor.getColumnIndex(
                            "lastname"));

                    Timetable.timetableArrayList.add( new Timetable(calendarDate,cursor.getString(cursor.getColumnIndex("moduleName")), cursor.getString(cursor.getColumnIndex("startTime")), cursor.getString(cursor.getColumnIndex("endTime")),lecturerName,cursor.getString(cursor.getColumnIndex("classType")),cursor.getString(cursor.getColumnIndex("location"))));

                    Date currentDate = null,EndDate = null;
                    SimpleDateFormat dateForamt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        currentDate = dateForamt.parse(calendarDate+" " + cursor.getString(cursor.getColumnIndex("startTime")));
                        EndDate =
                                dateForamt.parse(calendarDate+" " + cursor.getString(cursor.getColumnIndex("endTime")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                Calendar cc = Calendar.getInstance();
                cc.setTime(currentDate);

                    Calendar cc2 = Calendar.getInstance();
                    cc2.setTime(EndDate);

                    ContentResolver contentResolver = this.getContext().getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(CalendarContract.Events.TITLE,cursor.getString(cursor.getColumnIndex("moduleName")));
                    contentValues.put(CalendarContract.Events.DESCRIPTION,
                            lecturerName);
                    contentValues.put(CalendarContract.Events.EVENT_LOCATION,
                            cursor.getString(cursor.getColumnIndex("location")));
                    contentValues.put(CalendarContract.Events.DTSTART,cc.getTimeInMillis());
                    contentValues.put(CalendarContract.Events.DTEND,cc.getTimeInMillis());
                    contentValues.put(CalendarContract.Events.CALENDAR_ID,
                           1);
                    contentValues.put(CalendarContract.Events.EVENT_TIMEZONE,
                            Calendar.getInstance().getTimeZone().getID());
                     Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI,
                                    contentValues);
                    Log.d("TAG1", "onCreateView: "+uri);

                }


            } else {
                Toast.makeText(getActivity(), "No allocated schedule available for you",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No allocated schedule available for you",
                    Toast.LENGTH_SHORT).show();
        }

        calMonth = (GregorianCalendar) GregorianCalendar.getInstance();
        calMonthCopy = (GregorianCalendar) calMonth.clone();
        timetableAdapter = new TimetableAdapter(getActivity(), calMonth,Timetable.timetableArrayList);

        txtMonth = view.findViewById(R.id.txtMonth);
        txtMonth.setText(DateFormat.format("MMMM yyyy", calMonth));
        ImageButton previous = view.findViewById(R.id.ib_prev);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calMonth.get(GregorianCalendar.MONTH) == 4&&calMonth.get(GregorianCalendar.YEAR)==2017) {
                    Toast.makeText(getActivity(), "Event Detail is available for current session only.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    setPreviousMonth();
                    refreshCalendar();
                }


            }
        });
        ImageButton next = view.findViewById(R.id.Ib_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calMonth.get(GregorianCalendar.MONTH) == 5&&calMonth.get(GregorianCalendar.YEAR)==2018) {
                    Toast.makeText(getActivity(), "Event Detail is available for current session only.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    setNextMonth();
                    refreshCalendar();
                }
            }
        });
        GridView gridview = view.findViewById(R.id.grid_calendar);
        gridview.setAdapter(timetableAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String selectedGridDate = TimetableAdapter.day_string.get(position);
                ((TimetableAdapter) parent.getAdapter()).getPositionList(selectedGridDate, getActivity());
            }

        });
        return view;
    }
    protected void setNextMonth() {
        if (calMonth.get(GregorianCalendar.MONTH) == calMonth.getActualMaximum(GregorianCalendar.MONTH)) {
            calMonth.set((calMonth.get(GregorianCalendar.YEAR) + 1), calMonth.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            calMonth.set(GregorianCalendar.MONTH,
                    calMonth.get(GregorianCalendar.MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        if (calMonth.get(GregorianCalendar.MONTH) == calMonth.getActualMinimum(GregorianCalendar.MONTH)) {
            calMonth.set((calMonth.get(GregorianCalendar.YEAR) - 1), calMonth.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            calMonth.set(GregorianCalendar.MONTH, calMonth.get(GregorianCalendar.MONTH) - 1);
        }
    }


    public void refreshCalendar() {
        timetableAdapter.refreshDays();
        timetableAdapter.notifyDataSetChanged();
        txtMonth.setText(android.text.format.DateFormat.format("MMMM yyyy", calMonth));
    }
}