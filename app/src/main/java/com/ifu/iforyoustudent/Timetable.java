package com.ifu.iforyoustudent;

import java.util.ArrayList;

public class Timetable {
    public String date="";
    public String module="";
    public String startTime="";
    public String endTime="";
    public String lecturer="";
    public String type="";
    public String location="";

    public static ArrayList<Timetable> timetableArrayList;

    public Timetable(String date, String module, String startTime, String endTime,
                     String lecturer, String type, String location){

        this.date=date;
        this.module=module;
        this.startTime=startTime;
        this.endTime= endTime;
        this.lecturer = lecturer;
        this.type = type;
        this.location = location;

    }
}
