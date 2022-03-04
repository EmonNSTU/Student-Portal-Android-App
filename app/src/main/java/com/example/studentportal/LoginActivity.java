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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {

    private Button login_btn;
    private TextView signup_txtBtn,resetPass;
    private EditText mail, password;
    private ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    private String userId;
    boolean verified = false;
    SharedPreferences sharedPreferences;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login Page");

        mail = findViewById(R.id.login_mail);
        password = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_bn);
        signup_txtBtn = findViewById(R.id.login_signup_textbtn);
        progressBar = findViewById(R.id.login_progressBar);
        resetPass = findViewById(R.id.resetPass_textbtn);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF,MODE_PRIVATE);
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            firebaseUser.reload();

            if (firebaseUser.isEmailVerified()) {
                userId = firebaseUser.getUid();
                firestore.collection(Config.fireFolder).document(userId).update(Config.fireVerify, true);
            }
        }

        signup_txtBtn.setOnClickListener(view -> openSignupActivity());

        resetPass.setOnClickListener(view -> {

            String logMail = mail.getText().toString().trim();

            if(logMail.isEmpty()){
                mail.setError("Email is Required!");
                return;
            } else mail.setError(null);

            firebaseAuth.sendPasswordResetEmail(logMail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(LoginActivity.this, "A mail is sent with Password Reset Link", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Reset Link couldn't be sent, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        login_btn.setOnClickListener(view -> {

            String logMail = mail.getText().toString().trim();
            String logPass = password.getText().toString().trim();

            progressBar.setVisibility(View.VISIBLE);

            if(logMail.isEmpty()){
                mail.setError("Email is Required!");
                return;
            } else mail.setError(null);

            if(logPass.isEmpty()){
                password.setError("Password is Required!");
                return;
            } else password.setError(null);

            firebaseAuth.signInWithEmailAndPassword(logMail,logPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    userId = authResult.getUser().getUid();
                    firebaseUser = authResult.getUser();
                    firebaseUser.reload().addOnSuccessListener(unused -> {
                        if (firebaseUser.isEmailVerified()) {
                            firestore.collection(Config.fireFolder).document(userId).update(Config.fireVerify, true);
                            saveLoginStatus(true);
                        }
                    });

                    firestore.collection(Config.fireFolder).document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()){
                                    progressBar.setVisibility(View.GONE);
                                    verified = documentSnapshot.getBoolean(Config.fireVerify);
                                    if (verified){
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    }else {
                                        Toast.makeText(LoginActivity.this, "Email is not verified yet!", Toast.LENGTH_SHORT).show();
                                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(LoginActivity.this, "Verification Mail Sent", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                startActivity(new Intent(LoginActivity.this, EmailVerifyActivity.class));
                                                try {
                                                    Toast.makeText(LoginActivity.this, "Verify your Email to Continue...", Toast.LENGTH_SHORT).show();
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this, "Verification Mail not sent, "
                                                        +e.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }

                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(LoginActivity.this,"Sign in failed, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "Signup first if you don't have an Account yet!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            });

        });
    }

    private void saveLoginStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Config.LOGIN_STATUS,status);
        editor.apply();
    }

    public void openSignupActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}