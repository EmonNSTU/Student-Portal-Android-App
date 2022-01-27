package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Stack;

public class SignupActivity extends AppCompatActivity {

    private Button reg_bn;
    private EditText fullName, userName, email, phone, pass, rePass, batch;
    private ProgressBar progressBar;
    private RadioGroup gender;
    private RadioButton signMale, signFemale;
    private TextView radioText;
    private String regGender;
    FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Signup Form");

        reg_bn = findViewById(R.id.register_bn);
        fullName = findViewById(R.id.sign_fullName);
        userName = findViewById(R.id.sign_userName);
        email = findViewById(R.id.sign_email);
        phone = findViewById(R.id.sign_phone);
        batch = findViewById(R.id.sign_batch);
        pass = findViewById(R.id.sign_setPass);
        rePass = findViewById(R.id.sign_confirmPass);
        gender = (RadioGroup) findViewById(R.id.sign_gender);
        progressBar = (ProgressBar) findViewById(R.id.signup_progressBar);
        radioText = findViewById(R.id.radio_Text);
        signFemale = findViewById(R.id.sign_gender_female);
        signMale = findViewById(R.id.sign_gender_male);

        fireAuth = FirebaseAuth.getInstance();

        reg_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String regUserName = userName.getText().toString().trim();
                String regName = fullName.getText().toString().trim();
                String regMail = email.getText().toString().trim();
                String regPhone = phone.getText().toString().trim();
                String regBatch = batch.getText().toString().trim();
                String regPassword = pass.getText().toString().trim();
                String regConfirmPass = rePass.getText().toString().trim();
                String regGender;

                if(regName.isEmpty()){
                    fullName.setError("Full Name is Required!");
                    return;
                } else fullName.setError(null);

                if(regUserName.isEmpty()){
                    userName.setError("Username is Required!");
                    return;
                } else userName.setError(null);

                if(regMail.isEmpty()){
                    email.setError("Email Address is Required!");
                    return;
                } else email.setError(null);

                if(regPhone.isEmpty()){
                    phone.setError("Phone Number is Required!");
                    return;
                } else phone.setError(null);

                if(regBatch.isEmpty()){
                    batch.setError("Batch Number is Required!");
                    return;
                } else batch.setError(null);

                if (!isGenderSelected()) {
                    signFemale.setError("Select your Gender!");
                    return;
                } else signFemale.setError(null);

                if(regPassword.isEmpty()){
                    pass.setError("Enter A Password!");
                    return;
                } else pass.setError(null);

                if(regConfirmPass.isEmpty()){
                    rePass.setError("Confirm the Password!");
                    return;
                }
                else{
                    if(!regConfirmPass.equals(regPassword)){
                        rePass.setError("Password doesn't match!");
                        return;
                    }else rePass.setError(null);
                }

                fireAuth.createUserWithEmailAndPassword(regMail,regPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        openLoginActivity();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    public void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public boolean isGenderSelected() {
        if (gender.getCheckedRadioButtonId() == -1){
            return false;
        }else {
            if (gender.getCheckedRadioButtonId() == R.id.sign_gender_male) regGender = "Male";
            else regGender = "Female";
            return true;
        }
    }

}