package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class EditProfileActivity extends AppCompatActivity {

    private Button reg_bn;
    private EditText fullName, email, phone, pass, rePass, batch;
    private ProgressBar progressBar;
    private RadioGroup gender;
    private RadioButton signFemale;
    private String regGender,userId;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    private static final String fireFolder = "Users";
    private static final String fireName = "Name";
    private static final String fireMail = "Email";
    private static final String fireBatch = "Batch";
    private static final String firePhone = "Phone";
    private static final String fireGender = "Gender";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Edit Profile");

        reg_bn = findViewById(R.id.register_bn);
        fullName = findViewById(R.id.sign_fullName);
        email = findViewById(R.id.sign_email);
        phone = findViewById(R.id.sign_phone);
        batch = findViewById(R.id.sign_batch);
        pass = findViewById(R.id.sign_setPass);
        rePass = findViewById(R.id.sign_confirmPass);
        gender = (RadioGroup) findViewById(R.id.sign_gender);
        progressBar = (ProgressBar) findViewById(R.id.signup_progressBar);
        signFemale = findViewById(R.id.sign_gender_female);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = auth.getCurrentUser();

        userId = firebaseUser.getUid();

        reg_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String regName = fullName.getText().toString().trim();
                String regMail = email.getText().toString().trim();
                String regPhone = phone.getText().toString().trim();
                String regBatch = batch.getText().toString().trim();
                String regPassword = pass.getText().toString().trim();
                String regConfirmPass = rePass.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);

                if (regName.isEmpty()) {
                    fullName.setError("Full Name is Required!");
                    return;
                } else fullName.setError(null);

                if (regMail.isEmpty()) {
                    email.setError("Email Address is Required!");
                    return;
                } else email.setError(null);

                if (regPhone.isEmpty()) {
                    phone.setError("Phone Number is Required!");
                    return;
                } else phone.setError(null);

                if (regBatch.isEmpty()) {
                    batch.setError("Batch Number is Required!");
                    return;
                } else batch.setError(null);

                if (!isGenderSelected()) {
                    signFemale.setError("Select your Gender!");
                    return;
                } else signFemale.setError(null);

                if (regPassword.isEmpty()) {
                    pass.setError("Enter A Password!");
                    return;
                } else pass.setError(null);

                if (regConfirmPass.isEmpty()) {
                    rePass.setError("Confirm the Password!");
                    return;
                } else {
                    if (!regConfirmPass.equals(regPassword)) {
                        rePass.setError("Password doesn't match!");
                        return;
                    } else rePass.setError(null);
                }

                firestore.collection(fireFolder).document(userId).update(fireName, regName);
                firestore.collection(fireFolder).document(userId).update(fireMail, regMail);
                firestore.collection(fireFolder).document(userId).update(firePhone, regPhone);
                firestore.collection(fireFolder).document(userId).update(fireGender, regGender);
                firestore.collection(fireFolder).document(userId).update(fireBatch, regBatch);

                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
            }

            public void openLoginActivity() {
                Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            public boolean isGenderSelected() {
                if (gender.getCheckedRadioButtonId() == -1) {
                    return false;
                } else {
                    if (gender.getCheckedRadioButtonId() == R.id.sign_gender_male)
                        regGender = "Male";
                    else regGender = "Female";
                    return true;
                }
            }
        });
    }
}