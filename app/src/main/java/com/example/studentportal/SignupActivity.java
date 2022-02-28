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

public class SignupActivity extends AppCompatActivity {

    private EditText vfyEmail,vfyPass,vfyRePass,vfyName,vfyBatch, regRoll;
    private TextView vfyRetry;
    private Button regBtn;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    private String userId;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF,MODE_PRIVATE);

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

                int intBatch = Integer.parseInt(batch);

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(mail,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        regBtn.setEnabled(false);
                        userId = auth.getUid();
                        Map<String ,Object> user = new HashMap<>();
                        user.put(Config.fireMail, mail);
                        user.put(Config.fireName, name);
                        user.put(Config.fireBatch, intBatch);
                        user.put(Config.fireRoll, roll);
                        user.put(Config.fireGender, Config.fireNone);
                        user.put(Config.fireBlood, Config.fireNone);
                        user.put(Config.firePhone, Config.fireNone);
                        user.put(Config.fireOccupation, Config.fireNone);
                        user.put(Config.fireVerify, false);

                        firestore.collection(Config.fireFolder).document(userId).set(user);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Config.LOGIN_STATUS,false);
                        editor.apply();

                        auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SignupActivity.this, "Verification Email Successfully Sent!", Toast.LENGTH_SHORT).show();
                                try {
                                    Toast.makeText(SignupActivity.this, "Verify your Email to Continue...", Toast.LENGTH_SHORT).show();
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                auth.signInWithEmailAndPassword(mail, pass);
                                startActivity(new Intent(SignupActivity.this, EmailVerifyActivity.class));
                                finish();
                                progressBar.setVisibility(View.INVISIBLE);
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

                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SignupActivity.this, "Verify your Email before Login", Toast.LENGTH_SHORT).show();
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
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