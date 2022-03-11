package com.example.studentportal;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    String mail;
    String pass;
    String rePass;
    String name;
    String batch;
    String roll;
    private String deptCheck;


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

            mail   = vfyEmail.getText().toString().toLowerCase().trim();
            pass   = vfyPass.getText().toString().trim();
            rePass = vfyRePass.getText().toString().trim();
            name    = vfyName.getText().toString().trim();
            batch   = vfyBatch.getText().toString().trim();
            roll    = regRoll.getText().toString().toUpperCase().trim();

            deptCheck = roll.substring(5,7);

            if (isValidate()) {

                progressBar.setVisibility(View.VISIBLE);
                Log.d("TAG", "onCreate: Else Block");
                firestore.collection(Config.fireFolder).whereEqualTo(Config.fireRoll, roll)
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                if (task.getResult().isEmpty()) {
                                    Log.d("TAG", "onCreate:  not Duplicate");
                                    regRoll.setError(null);
                                    sendDataToDatabase();
                                }
                                else {
                                    Log.d("TAG", "onCreate:  Duplicate");
                                    regRoll.setError("Student already exists!");
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }).addOnFailureListener(e -> Log.d("TAG", "onCreate: failer: "+e.getLocalizedMessage()));

            }

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

    private void sendDataToDatabase() {
        int intBatch = Integer.parseInt(batch);

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
                progressBar.setVisibility(View.GONE);
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
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                vfyRetry.setVisibility(View.VISIBLE);
            });

        }).addOnFailureListener(e -> {

            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }

    private boolean isValidate() {

        if(name.isEmpty()){
            vfyName.setError("Name is Required!");
            return false;
        } else vfyName.setError(null);

        if(mail.isEmpty()){
            vfyEmail.setError("Email Address is Required!");
            return false;
        } else vfyEmail.setError(null);

        if(batch.isEmpty()){
            vfyBatch.setError("Batch Number is Required!");
            return false;
        } else vfyBatch.setError(null);

        if(roll.isEmpty()){
            regRoll.setError("Student ID is Required!");
            return false;
        } else regRoll.setError(null);

        if(!deptCheck.equals(Config.deptCode) || roll.length() != 11){
            regRoll.setError("Invalid Student ID!");
            return false;
        } else regRoll.setError(null);

        if(pass.isEmpty()){
            vfyPass.setError("Enter A Password!");
            return false;
        } else vfyPass.setError(null);

        if(rePass.isEmpty()){
            vfyRePass.setError("Confirm the Password!");
            return false;
        } else vfyRePass.setError(null);

        if(!rePass.equals(pass)){
            vfyRePass.setError("Password doesn't match!");
            return false;
        }else vfyRePass.setError(null);


        return true;
    }

}