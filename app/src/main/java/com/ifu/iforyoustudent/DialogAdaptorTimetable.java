package com.ifu.iforyoustudent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DialogAdaptorTimetable extends BaseAdapter {
    Activity activity;

    private Activity context;
    private ArrayList<DialogTimetable> dialogTimetableArrayList;
    private String url;


    public DialogAdaptorTimetable(Activity context, ArrayList<DialogTimetable> alCustom) {
        this.context = context;
        this.dialogTimetableArrayList = alCustom;

    }

    @Override
    public int getCount() {
        return dialogTimetableArrayList.size();

    }

    @Override
    public Object getItem(int i) {
        return dialogTimetableArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.row_details, null, true);

        TextView date=(TextView)listViewItem.findViewById(R.id.date);
        TextView module=(TextView)listViewItem.findViewById(R.id.module);
        TextView lecturer=(TextView)listViewItem.findViewById(R.id.lecturer);
        TextView startTime=(TextView)listViewItem.findViewById(R.id.startTime);
        TextView endTime=(TextView)listViewItem.findViewById(R.id.endTime);
        TextView type=(TextView)listViewItem.findViewById(R.id.type);
        TextView location=(TextView)listViewItem.findViewById(R.id.location);


        date.setText("Date : "+dialogTimetableArrayList.get(position).getDate());
        module.setText("Module : "+dialogTimetableArrayList.get(position).getModule());
        lecturer.setText("Lecturer : "+dialogTimetableArrayList.get(position).getLecturer());
        startTime.setText("Start Time : "+dialogTimetableArrayList.get(position).getStartTime());
        endTime.setText("End Time : "+dialogTimetableArrayList.get(position).getEndTime());
        type.setText("Type : "+dialogTimetableArrayList.get(position).getType());
        location.setText("Location : "+dialogTimetableArrayList.get(position).getLocation());


        return  listViewItem;
    }

}
