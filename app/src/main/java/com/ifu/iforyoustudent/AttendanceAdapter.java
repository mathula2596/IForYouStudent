package com.ifu.iforyoustudent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private ArrayList<Attendance> attendanceArrayList;

    public AttendanceAdapter(ArrayList<Attendance> attendanceArrayList) {
        this.attendanceArrayList = attendanceArrayList;
    }

    @NonNull
    @Override
    public AttendanceAdapter.AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_attendance,
                parent,false);

        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.AttendanceViewHolder holder, int position) {

        String date = attendanceArrayList.get(position).getDate();
        String attendance = attendanceArrayList.get(position).getAttendance();
        holder.txtAttendance.setText(attendance);
        holder.txtDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return attendanceArrayList.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDate, txtAttendance;
        public AttendanceViewHolder(final View view) {
            super(view);
            txtDate = view.findViewById(R.id.date);
            txtAttendance = view.findViewById(R.id.attendance);
        }
    }
}
