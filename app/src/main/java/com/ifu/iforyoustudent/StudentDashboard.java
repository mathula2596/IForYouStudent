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
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

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

    private WorkRequest notificationWorkRequest;

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
                        Toast.makeText(getApplicationContext(), "Settings is Clicked",
                                Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.nav_view_timetable:
                        timetableFragment = new TimetableFragment();
                        replaceFragment(timetableFragment);
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
        fragmentTransaction.replace(R.id.frame_layout,fragment);
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
}