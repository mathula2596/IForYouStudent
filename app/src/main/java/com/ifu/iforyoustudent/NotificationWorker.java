package com.ifu.iforyoustudent;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class NotificationWorker extends Worker {

    private Cursor cursor = null;
    private Cursor returnCursor = null;
    private Uri CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/notification");
    private Uri RETURN_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/notificationreturn");

    private Uri ATTENDANCE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/attendancenotification");

    private Uri UPDATE_ATTENDANCE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/attendancenotificationupdate");

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";
    String loginName, loginRole = null;
    Calendar date;
    ArrayList <String> returnIds;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);


    }

    @NonNull
    @Override
    public Result doWork() {
        ContextCompat.getMainExecutor(getApplicationContext()).execute(new Runnable() {
            @Override
            public void run() {

                sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PRE_NAME,MODE_PRIVATE);
                loginName = sharedPreferences.getString(KEY_NAME,null);
                loginRole = sharedPreferences.getString(KEY_ROLE,null);

                if(loginName != null){

                    cursor = getApplicationContext().getContentResolver().query(CONTENT_URI, null, null,
                            new String[]{loginName}, null);
                    returnIds = new ArrayList<>();
                    String x = null;
                    String alertDate, currentDate= null;
                    Date formattedAlertDate = null, formattedCurrentDate = null, todayDate =
                            Calendar.getInstance().getTime();

                    if (cursor != null) {
                        if (cursor.getCount() > 0) {
                            returnIds = new ArrayList<>();
                            while (cursor.moveToNext())
                            {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm",
                                        Locale.ENGLISH);
                                alertDate = cursor.getString(cursor.getColumnIndex(
                                        "dateAlert")) + " "+ cursor.getString(cursor.getColumnIndex(
                                        "timeAlert"));
                                currentDate = formatter.format(new Date());

                                try {

                                    formattedAlertDate = formatter.parse(alertDate);
                                    formattedCurrentDate = formatter.parse(currentDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if(formattedCurrentDate.after(formattedAlertDate))
                                {
                                    returnIds.add(cursor.getString(cursor.getColumnIndex(
                                            "alertId")));

                                    Intent intent = new Intent(getApplicationContext(),
                                        ReminderBroadcast.class);
                                    PendingIntent pendingIntent =
                                            PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                    AlarmManager alarmManager =
                                            null;
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                                    }

                                    long currentTimeInMilliSeconds = System.currentTimeMillis();

                                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                                            currentTimeInMilliSeconds, pendingIntent);
                                }
                                else
                                {

                                }


                            }


                        }
                    }
                    String[] returnIdsArray = returnIds.toArray(new String[returnIds.size()]);
                    returnCursor = getApplicationContext().getContentResolver().query(RETURN_CONTENT_URI, null,
                            null,
                            returnIdsArray, null);

                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm",
                            Locale.ENGLISH);

                    String currentTime = simpleDateFormat.format(new Date());

                    Date currentTimeinDate = null, checkTimeinDate = null ;
                    String alertTiming = "14:07", alertDay = "5";
                    String message = null;


                    cursor = getApplicationContext().getContentResolver().query(ATTENDANCE_CONTENT_URI, null, null,
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


                    try {
                        currentTimeinDate = simpleDateFormat.parse(currentTime);
                        checkTimeinDate = simpleDateFormat.parse(alertTiming);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    long differenceInMilliSeconds
                            = Math.abs(currentTimeinDate.getTime() - checkTimeinDate.getTime());
                    long differenceInMinutes
                            = (differenceInMilliSeconds / (60 * 1000)) % 60;

                    Log.d("TAG", "run: "+differenceInMinutes);

                        if(Integer.parseInt(alertDay) == day) {

                            if(currentTimeinDate.equals(checkTimeinDate))
                            {
                                Intent intent = new Intent(getApplicationContext(),
                                        AttendanceReminderBroadcast.class);
                                intent.putExtra("message", message);

                                PendingIntent pendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                AlarmManager alarmManager =
                                        null;
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                                }

                                long currentTimeInMilliSeconds = System.currentTimeMillis();

                                alarmManager.set(AlarmManager.RTC_WAKEUP,
                                        currentTimeInMilliSeconds, pendingIntent);

                                cursor = getApplicationContext().getContentResolver().query(UPDATE_ATTENDANCE_CONTENT_URI, null, null,
                                        new String[]{loginName}, null);

                            }

                        }


                }
            }

        });

        return null;
    }
}
