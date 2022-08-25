package com.ifu.iforyoustudent;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;


public class ProfileFragment extends Fragment {

    private View view;
    private Cursor cursor = null;
    private Uri CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/profile");

    private Uri UPDATE_PROFILE_CONTENT_URI = Uri.parse("content://com.ifu.contentprovider" +
            ".provider/profileupdate");

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PRE_NAME = "myPreference";
    private static final String KEY_NAME = "username";
    private static final String KEY_ROLE = "userType";
    String loginName, loginRole = null;

    private FragmentTransaction fragmentTransaction;
    private Boolean validEmail, validField = false;

    private TextInputLayout txtFirstname, txtLastname, txtEmail, txtUniversityId, txtDegreeLevel,
    txtCourse, txtBatch;
    private Button btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        txtFirstname = view.findViewById(R.id.firstname);
        txtLastname = view.findViewById(R.id.lastname);
        txtEmail = view.findViewById(R.id.email);

        txtUniversityId = view.findViewById(R.id.universityId);
        txtDegreeLevel = view.findViewById(R.id.degreeLevel);
        txtCourse = view.findViewById(R.id.course);
        txtBatch = view.findViewById(R.id.batchNumber);


        sharedPreferences = getActivity().getSharedPreferences(SHARED_PRE_NAME,MODE_PRIVATE);
        loginName = sharedPreferences.getString(KEY_NAME,null);
        loginRole = sharedPreferences.getString(KEY_ROLE,null);

        cursor = getActivity().getContentResolver().query(CONTENT_URI, null, null,
                new String[]{loginName},
                null);
        if(cursor!=null) {
            if(cursor.getCount()>0)
            {
                while (cursor.moveToNext())
                {
                    txtUniversityId.getEditText().setText(cursor.getString(cursor.getColumnIndex(
                            "universityId")));
                    txtFirstname.getEditText().setText(cursor.getString(cursor.getColumnIndex(
                            "firstName")));
                    txtLastname.getEditText().setText(cursor.getString(cursor.getColumnIndex(
                            "lastName")));
                    txtDegreeLevel.getEditText().setText(cursor.getString(cursor.getColumnIndex(
                            "degreeLevel")));
                    txtEmail.getEditText().setText(cursor.getString(cursor.getColumnIndex(
                            "email")));
                    txtCourse.getEditText().setText(cursor.getString(cursor.getColumnIndex(
                            "courseName")));
                    txtBatch.getEditText().setText(cursor.getString(cursor.getColumnIndex(
                            "name")));
                }
            }
        }

        btnSubmit = view.findViewById(R.id.btnRegister);

        btnSubmit.setOnClickListener(v -> {
            if(checkFieldEmpty(txtFirstname) && checkFieldEmpty(txtLastname) && emailValidator(txtEmail))
            {
                cursor = getActivity().getContentResolver().query(UPDATE_PROFILE_CONTENT_URI, null, null,
                        new String[]{loginName,txtFirstname.getEditText().getText().toString(),
                                txtLastname.getEditText().getText().toString(),
                                txtEmail.getEditText().getText().toString()},
                        null);

                if(cursor!=null && cursor.getCount()>0)
                {
                    Toast.makeText(getActivity(),"Profile Updated Successfully!",
                            Toast.LENGTH_SHORT).show();

                    replaceFragment(new ProfileFragment());
                }
                else
                {
                    Toast.makeText(getActivity(),"Profile Updated Failed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
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
    public void replaceFragment(Fragment fragment)
    {
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment).addToBackStack("back");
        fragmentTransaction.commit();
    }

}