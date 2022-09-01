package com.ifu.iforyoustudent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static String[] PERMISSIONS  = null;
    LoginFragment loginFragment;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginFragment = new LoginFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homeFrameLayout,loginFragment);
        fragmentTransaction.commit();

        PERMISSIONS = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
        };
        if(!hasPermission(MainActivity.this,PERMISSIONS))
        {
            ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,1);
        }
    }

    private boolean hasPermission(Context context, String...PERMISSIONS)
    {
        if (context != null && PERMISSIONS != null)
        {
            for(String permission: PERMISSIONS)
            {
                if(ActivityCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED)
                {
                    return  false;
                }
            }

        }
        return true;
    }



}