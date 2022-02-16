package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EmailVarificationActivity extends AppCompatActivity {

    private EditText vfyEmail,vfyPass,vfyRePass,vfyName,vfyBatch, regRoll;
    private TextView vfyRetry;
    private Button regBtn;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    private String userId;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREF = "Email_verification";
    private static final String fireFolder = "Users";
    private static final String fireName = "Name";
    private static final String fireMail = "Email";
    private static final String fireBatch = "Batch";
    private static final String firePhone = "Phone";
    private static final String fireGender = "Gender";
    private static final String fireNone = "Unavailable";
    private static final String fireVerify = "Verified";
    private static final String fireRoll = "Student ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_varification);

        getSupportActionBar().setTitle("User Registration");

        vfyEmail = findViewById(R.id.verify_mail);
        regBtn = (Button) findViewById(R.id.register_bn);
        vfyName = findViewById(R.id.reg_name);
        vfyRetry = findViewById(R.id.verify_retry);
        vfyPass = findViewById(R.id.verify_setPass);
        vfyRePass = findViewById(R.id.verify_confirmPass);
        progressBar = (ProgressBar) findViewById(R.id.verify_progressBar);
        vfyBatch = findViewById(R.id.reg_batch);
        regRoll = findViewById(R.id.sign_id);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = vfyEmail.getText().toString().toLowerCase().trim();
                String pass = vfyPass.getText().toString().trim();
                String rePass = vfyRePass.getText().toString().trim();
                String name = vfyName.getText().toString().trim();
                String batch = vfyBatch.getText().toString().trim();
                String roll = regRoll.getText().toString().toUpperCase().trim();

                if(name.isEmpty()){
                    vfyName.setError("Name is Required!");
                    return;
                } else vfyName.setError(null);

                if(mail.isEmpty()){
                    vfyEmail.setError("Email Address is Required!");
                    return;
                } else vfyEmail.setError(null);

                if(batch.isEmpty()){
                    vfyBatch.setError("Batch Number is Required!");
                    return;
                } else vfyBatch.setError(null);

                if(roll.isEmpty()){
                    regRoll.setError("Student ID is Required!");
                    return;
                } else regRoll.setError(null);

                if(pass.isEmpty()){
                    vfyPass.setError("Enter A Password!");
                    return;
                } else vfyPass.setError(null);

                if(rePass.isEmpty()){
                    vfyRePass.setError("Confirm the Password!");
                    return;
                } else{
                    if(!rePass.equals(pass)){
                        vfyRePass.setError("Password doesn't match!");
                        return;
                    }else vfyRePass.setError(null);
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(mail,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        regBtn.setEnabled(false);
                        userId = auth.getUid();
                        Map<String ,Object> user = new HashMap<>();
                        user.put(fireMail, mail);
                        user.put(fireName, name);
                        user.put(fireBatch, batch);
                        user.put(fireRoll, roll);
                        user.put(fireGender, fireNone);
                        user.put(firePhone, fireNone);
                        user.put(fireVerify, false);

                        firestore.collection(fireFolder).document(userId).set(user);

                        auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EmailVarificationActivity.this, "Verification Email Successfully Sent!", Toast.LENGTH_SHORT).show();
                                try {
                                    Toast.makeText(EmailVarificationActivity.this, "Verify your Email before Login", Toast.LENGTH_SHORT).show();
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startActivity(new Intent(EmailVarificationActivity.this, LoginActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                vfyRetry.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(EmailVarificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

        vfyRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Verification Email Successfully Sent!", Toast.LENGTH_SHORT).show();
                        vfyRetry.setVisibility(View.INVISIBLE);
                        try {
                            Toast.makeText(EmailVarificationActivity.this, "Verify your Email before Login", Toast.LENGTH_SHORT).show();
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(EmailVarificationActivity.this, LoginActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        vfyRetry.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

    }
}