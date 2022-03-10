package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.studentportal.utils.Config;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

public class ResetLoginEmailActivity extends AppCompatActivity {

    EditText oldMail, oldPass, newMail;
    Button updateBtn;
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_login_email);
        getSupportActionBar().setTitle("Reset Email");

        oldMail = findViewById(R.id.old_mail);
        oldPass = findViewById(R.id.old_password);
        newMail = findViewById(R.id.new_mail);
        updateBtn = findViewById(R.id.update_bn);
        progressBar = findViewById(R.id.update_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                String oldEmail = oldMail.getText().toString().toLowerCase(Locale.ROOT).trim();
                String oldPassword = oldPass.getText().toString().trim();
                String newEmail = newMail.getText().toString().toLowerCase(Locale.ROOT).trim();

                if(oldEmail.isEmpty()){
                    oldMail.setError("Email is Required!");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } else oldMail.setError(null);

                if(oldPassword.isEmpty()){
                    oldPass.setError("Password is Required!");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } else oldPass.setError(null);

                if(newEmail.isEmpty()){
                    newMail.setError("Email is Required!");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } else newMail.setError(null);

                if(oldEmail.equals(newEmail)){
                    newMail.setError("Both Email Addresses are Same!");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } else newMail.setError(null);

                if(Objects.equals(firebaseUser.getEmail(), oldEmail)){
                    firebaseAuth.signInWithEmailAndPassword(oldEmail,oldPassword).addOnSuccessListener(authResult -> {

                        firebaseUser.updateEmail(newEmail).addOnSuccessListener(unused -> {

                            Toast.makeText(ResetLoginEmailActivity.this, "Login Email Updated Successfully!"
                                    , Toast.LENGTH_SHORT).show();

                            firestore.collection(Config.fireFolder)
                                    .document(Objects.requireNonNull(firebaseAuth.getUid()))
                                    .update(Config.fireMail, newEmail);

                            firebaseUser.sendEmailVerification().addOnSuccessListener(unused1 -> {
                                    Toast.makeText(ResetLoginEmailActivity.this,
                                            "Verify New Email to Continue!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ResetLoginEmailActivity.this, EmailVerifyActivity.class));
                                    progressBar.setVisibility(View.INVISIBLE);

                            });

                        }).addOnFailureListener(e -> {

                            Toast.makeText(ResetLoginEmailActivity.this, "Error Occurred, " +
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        });

                    }).addOnFailureListener(e -> {

                        Toast.makeText(ResetLoginEmailActivity.this, "Error Occurred! Network " +
                                "Error or Password Incorrect", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                }
                else{
                    Toast.makeText(ResetLoginEmailActivity.this, "Incorrect Email Given!"
                            , Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}