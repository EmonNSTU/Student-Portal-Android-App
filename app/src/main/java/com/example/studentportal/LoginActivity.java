package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button login_btn;
    private TextView signup_txtBtn;
    private EditText mail, password;
    private ProgressBar progressBar;
    FirebaseAuth firebaseAuth;


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
        firebaseAuth = FirebaseAuth.getInstance();

        signup_txtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openSignupActivity();
            }
        });

        login_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String logMail = mail.getText().toString().trim();
                String logPass = password.getText().toString().trim();

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
                        openHomeActivity();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    public void openSignupActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
    public void openHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            openHomeActivity();
            finish();
        }
    }
}