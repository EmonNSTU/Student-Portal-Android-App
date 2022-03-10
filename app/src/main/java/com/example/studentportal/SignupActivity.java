package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentportal.utils.Config;
import com.example.studentportal.utils.SpManager;
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
    boolean b = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle("User Registration");

        vfyEmail = findViewById(R.id.verify_mail);
        regBtn = findViewById(R.id.register_bn);
        vfyName = findViewById(R.id.reg_name);
        vfyRetry = findViewById(R.id.verify_retry);
        vfyPass = findViewById(R.id.verify_setPass);
        vfyRePass = findViewById(R.id.verify_confirmPass);
        progressBar = findViewById(R.id.verify_progressBar);
        vfyBatch = findViewById(R.id.reg_batch);
        regRoll = findViewById(R.id.sign_id);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF,MODE_PRIVATE);

        regBtn.setOnClickListener(view -> {

            String mail = vfyEmail.getText().toString().toLowerCase().trim();
            String pass = vfyPass.getText().toString().trim();
            String rePass = vfyRePass.getText().toString().trim();
            String name = vfyName.getText().toString().trim();
            String batch = vfyBatch.getText().toString().trim();
            String roll = regRoll.getText().toString().toUpperCase().trim();

            String deptCheck = roll.substring(5,7);
            Thread thread = new Thread(()-> isRollDuplicate(roll));
            thread.start();

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
            }
            else if(b){
                regRoll.setError("Student is Already Registered!");
                return;
            } else {
                Log.d("TAG", "onCreate: false");
                regRoll.setError(null);
            }

            if(!deptCheck.equals(Config.deptCode)){
                regRoll.setError("Invalid Student ID!");
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

            auth.createUserWithEmailAndPassword(mail,pass).addOnSuccessListener(authResult -> {
                SpManager.saveString(SignupActivity.this,SpManager.PREF_BATCH,batch);
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
                user.put(Config.fireProfileImageUrl, null);

                firestore.collection(Config.fireFolder).document(userId).set(user);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Config.LOGIN_STATUS,false);
                editor.apply();

                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(unused -> {
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
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    vfyRetry.setVisibility(View.VISIBLE);
                });

            }).addOnFailureListener(e -> {

                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            });

        });

        vfyRetry.setOnClickListener(view ->
                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Verification Email Successfully Sent!",
                            Toast.LENGTH_SHORT).show();
                    vfyRetry.setVisibility(View.INVISIBLE);
                    try {
                        Toast.makeText(SignupActivity.this, "Verify your Email before Login",
                                Toast.LENGTH_SHORT).show();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    vfyRetry.setVisibility(View.VISIBLE);
                }));

    }

    public synchronized void isRollDuplicate(String roll){

        firestore.collection(Config.fireFolder).whereEqualTo(Config.fireRoll, roll)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(task.getResult() != null){
                            b = true;
                            Log.d("TAG", "onCreate: true");
                        }
                    }
                });
    }
}