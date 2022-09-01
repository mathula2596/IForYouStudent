package com.ifu.iforyoustudent;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AttendanceReminderBroadcast extends BroadcastReceiver {
    private Cursor cursor;
    private Uri ATTENDANCE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/attendancenotification");

    private Uri UPDATE_ATTENDANCE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/attendancenotificationupdate");

    private Uri TIMETABLE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/timetablenotificationreminder");
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";
    String loginName, loginRole = null;
    @Override
    public void onReceive(Context context, Intent intent) {

        String alertTiming = "14:07", alertDay = "5";
        String message = null;

        sharedPreferences = context.getSharedPreferences(SHARED_PRE_NAME, MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);



        cursor = context.getContentResolver().query(ATTENDANCE_CONTENT_URI, null, null,
                new String[]{loginName}, null);

        if(cursor != null)
        {
            if(cursor.getCount() > 0)
            {
                while (cursor.moveToNext())
                {
                    message = cursor.getString(cursor.getColumnIndex("message"));
                    alertTiming =  cursor.getString(cursor.getColumnIndex(
                            "timeAlert"));
                    alertDay =  cursor.getString(cursor.getColumnIndex(
                            "dayAlert"));
                }
            }
        }

        if(checkDay(alertDay)) {

            if(checkTime(alertTiming))
            {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                        "notifyTimetable").setSmallIcon(R.drawable.ic_baseline_front_hand_24).setContentTitle("Your " +
                        "Attendance").setContentText(message).setPriority(NotificationCompat.PRIORITY_DEFAULT);


                NotificationManagerCompat notificationManagerCompat =
                        NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(200,builder.build());
                            cursor = context.getContentResolver().query(UPDATE_ATTENDANCE_CONTENT_URI
                                    , null, null,
                    new String[]{loginName}, null);
            }


        }

        cursor = context.getContentResolver().query(TIMETABLE_CONTENT_URI, null, null,
                new String[]{loginName}, null);

        String timetableDate = null, timetableStartTime = null;
        int splitedTime = 0;
        String [] splitedArray = null;
        //Log.d("TAG", "onReceive: "+"true");
        if(cursor != null)
        {
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                while (cursor.moveToNext())
                {
                    timetableDate = cursor.getString(cursor.getColumnIndex("date"));
                    timetableStartTime =  cursor.getString(cursor.getColumnIndex(
                            "startTime"));

                    splitedArray = timetableStartTime.split(":");
                    splitedTime = Integer.parseInt(splitedArray[0]) - 3;
                    String newTime = Integer.toString(splitedTime) + ":" + splitedArray[1];
                    String [] splitedDate = timetableDate.split("-");
                    String dateforCheck = splitedDate[2]+"-"+splitedDate[1]+"-"+splitedDate[0];
                    Log.d("TAG",
                            "onReceive: "+"false"+newTime+checkTime(newTime) + checkDate(dateforCheck) + timetableDate);
                    if(checkDate(dateforCheck) && checkTime(newTime))
                    {
                        Log.d("TAG", "onReceive: "+"true");

                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(context,
                                "notifyTimetable").setSmallIcon(R.drawable.ic_baseline_calendar_month_24).setContentTitle("Your upcoming schedule").setContentText("You have a scheduled class at "+timetableStartTime).setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManagerCompat1 =
                                NotificationManagerCompat.from(context);
                        notificationManagerCompat1.notify(200,notificationBuilder.build());
                    }
                }
            }
        }


    }
    public boolean checkDate(String date){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(new Date());
        try {
            Date date1 = format.parse(date);
            Date date2 = format.parse(currentDate);
            Log.d("TAG", "checkDate: "+date1 + " " +date2);
            if(date1.equals(date2)){
                return true;
            }
            else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkDay(String day){

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        if(currentDay == Integer.parseInt(day))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean checkTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String currentDate = format.format(new Date());
        try {
            Date time1 = format.parse(time);
            Date time2 = format.parse(currentDate);
            if(time1.equals(time2)){
                return true;
            }
            else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
