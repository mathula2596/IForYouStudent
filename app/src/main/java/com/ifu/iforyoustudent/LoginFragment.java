package com.ifu.iforyoustudent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.security.GeneralSecurityException;

public class LoginFragment extends Fragment {
    private Button btnLogin, btnLogin2;
    private View view;
    private FragmentTransaction fragmentTransaction;
    private TextView txtForgotPassword;
    private ForgotPasswordFragment forgotPasswordFragment;
    private TextInputLayout txtUsername, txtPassword;
    private String userRole;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";

    private Uri CONTENT_URI = Uri.parse("content://com.ifu.contentprovider.provider/login");

    String loginName, loginRole = null;
    private Cursor cursor = null;
    private Boolean validField = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_login, container, false);


        txtForgotPassword = view.findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(view1 -> {
            forgotPasswordFragment = new ForgotPasswordFragment();
            replaceFragment(forgotPasswordFragment);

        });


        sharedPreferences = getActivity().getSharedPreferences(SHARED_PRE_NAME, Context.MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        if(loginName != null || loginRole != null)
        {
            startActivity(new Intent(getActivity(),StudentDashboard.class));
            getActivity().finish();
        }

        txtUsername = view.findViewById(R.id.username);
        txtPassword = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view1 -> {
            boolean res = checkFieldEmpty(txtUsername);
            boolean res2 = checkFieldEmpty(txtPassword);
            if(res && res2) {
                cursor = getActivity().getContentResolver().query(CONTENT_URI, null, null,
                        new String[]{txtUsername.getEditText().getText().toString(), txtPassword.getEditText().getText().toString()}, null);

                if (cursor != null) {

                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(KEY_NAME, txtUsername.getEditText().getText().toString());
                            editor.putString(KEY_ROLE,
                                    cursor.getString(cursor.getColumnIndex("userRole")));
                            editor.apply();

                            startActivity(new Intent(getActivity(), StudentDashboard.class));
                            getActivity().finish();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Sorry, You are not allowed to login or " +
                                        "please check your account details",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, You are not allowed to login!",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

        return view;
    }

    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homeFrameLayout,fragment);
        fragmentTransaction.commit();
    }
    public boolean checkFieldEmpty(TextInputLayout textField){
        validField = false;
        if(!textField.getEditText().getText().toString().isEmpty())
        {
            textField.setError(null);
            validField = true;
        }
        else
        {
            textField.setError("Please fill the value");
            validField = false;
        }
        return validField;
    }

}