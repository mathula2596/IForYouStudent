package com.ifu.iforyoustudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StudentDashboard extends AppCompatActivity {

    private MaterialToolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";
    String loginName, loginRole = null;
    private View navHeader;
    public TextView username;
    private FragmentTransaction fragmentTransaction;
    private TimetableFragment timetableFragment;
    private AttendanceFragment attendanceFragment;
    private ResetPasswordFragment resetPasswordFragment;
    private Cursor cursor;
    private WorkRequest notificationWorkRequest;
    private ProfileFragment profileFragment;
    private Uri ATTENDANCE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/attendancenotification");

    private Uri UPDATE_ATTENDANCE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/attendancenotificationupdate");

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        sharedPreferences = getSharedPreferences(SHARED_PRE_NAME,MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        navigationView =findViewById(R.id.navigation_view);

        navHeader = navigationView.findViewById(R.layout.nav_header);

        appbar= findViewById(R.id.appbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        appbar.setNavigationOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
            username = navigationView.findViewById(R.id.username);
            username.setText(loginName);
        });

        notificationWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class,
                15*60*1000,
                TimeUnit.MILLISECONDS)
                .build();
//        notificationWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
//         .build();

        WorkManager
                .getInstance(this)
                .enqueue(notificationWorkRequest);

        createNotificationChannel();


////////////////////////






//        String alertTiming = "14:07", alertDay = "5";
//        String message = null;
//
//
//        cursor = getApplicationContext().getContentResolver().query(ATTENDANCE_CONTENT_URI, null, null,
//                new String[]{loginName}, null);
//
//        if(cursor != null)
//        {
//            if(cursor.getCount() > 0)
//            {
//                while (cursor.moveToNext())
//                {
//                    message = cursor.getString(cursor.getColumnIndex("message"));
//                    alertTiming =  cursor.getString(cursor.getColumnIndex(
//                            "timeAlert"));
//                    alertDay =  cursor.getString(cursor.getColumnIndex(
//                            "dayAlert"));
//                }
//            }
//        }

//        if(checkDay(alertDay)) {
//
//
//            if(!checkTime(alertTiming))
//            {
//                message = null;
//            }
////            cursor = getApplicationContext().getContentResolver().query(UPDATE_ATTENDANCE_CONTENT_URI, null, null,
////                    new String[]{loginName}, null);
//
//        }

        Intent intent = new Intent(getApplicationContext(),
                AttendanceReminderBroadcast.class);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        intent.putExtra("message",message);
//        intent.putExtra("user",loginName);


        AlarmManager alarmManager =
                null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        }

        long currentTimeInMilliSeconds = System.currentTimeMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                currentTimeInMilliSeconds,5,pendingIntent);




//////////////////////////////////


        timetableFragment = new TimetableFragment();
        replaceFragment(timetableFragment);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id)
                {
                    case R.id.nav_logout:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                        break;
                    case R.id.nav_home:
                        timetableFragment = new TimetableFragment();
                        replaceFragment(timetableFragment);
                        break;
                    case R.id.nav_view_timetable:
                        timetableFragment = new TimetableFragment();
                        replaceFragment(timetableFragment);
                        break;

                    case R.id.nav_attendance:
                        attendanceFragment = new AttendanceFragment();
                        replaceFragment(attendanceFragment);
                        break;
                    case R.id.nav_change_password:
                        resetPasswordFragment = new ResetPasswordFragment();
                        replaceFragment(resetPasswordFragment);
                        break;

                    case R.id.nav_profile:
                        profileFragment = new ProfileFragment();
                        replaceFragment(profileFragment);
                        break;
                    default:
                        return true;

                }
                return true;
            }
        });

    }
    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment).addToBackStack("back");
        fragmentTransaction.commit();
    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "IForYouReminderChannel";
            String description = "Channel for reminding the changes";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("notifyTimetable",name,
                    importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
//    public boolean checkDate(String date){
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String currentDate = format.format(new Date());
//        try {
//            Date date1 = format.parse(date);
//            Date date2 = format.parse(currentDate);
//            if(date1.equals(date2)){
//                return true;
//            }
//            else {
//                return false;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//    public boolean checkDay(String day){
//
//        Calendar calendar = Calendar.getInstance();
//        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
//
//        if(currentDay == Integer.parseInt(day))
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }
//    public boolean checkTime(String time){
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
//        String currentDate = format.format(new Date());
//        try {
//            Date time1 = format.parse(time);
//            Date time2 = format.parse(currentDate);
//            if(time1.equals(time2)){
//                return true;
//            }
//            else {
//                return false;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
}