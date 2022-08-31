package com.ifu.iforyoustudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Random;

public class ForgotPasswordFragment extends Fragment {
    private Button btnLogin, btnSend;
    private View view;
    private FragmentTransaction fragmentTransaction;
    private LoginFragment loginFragment;
    private TextInputLayout txtEmail;
    final String username = "mathula2011@gmail.com";
    final String password = "jmwwdizaqbgabsse";
    private String randomPassword;
    private Random random;
    private boolean validEmail = false;
    private Uri CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/forgotpassword");
    private Cursor cursor = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view1 -> {
            loginFragment = new LoginFragment();
            replaceFragment(loginFragment);
        });

        btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(view1 -> {

            txtEmail = view.findViewById(R.id.email);
            if(emailValidator(txtEmail)) {
                cursor = getActivity().getContentResolver().query(CONTENT_URI, null, null,
                        new String[]{txtEmail.getEditText().getText().toString()}, null);

                if (cursor != null) {

                    if (cursor.getCount() > 0) {
                        Toast.makeText(getActivity(), "Please check your mail for the temporary password",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "Failed to reset! Try again",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, You are not allowed to login!",
                            Toast.LENGTH_SHORT).show();
                }
                loginFragment = new LoginFragment();
                replaceFragment(loginFragment);
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

    public boolean emailValidator(TextInputLayout textField){
        validEmail = false;
        String email = textField.getEditText().getText().toString();
        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            textField.setError(null);
            validEmail = true;
        }
        else
        {
            textField.setError("Please fill the correct value");
            validEmail = false;
        }
        return validEmail;
    }
}