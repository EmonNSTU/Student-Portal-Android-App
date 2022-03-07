package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentportal.utils.Config;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmailVerifyActivity extends AppCompatActivity {

    private TextView verifiedBtn, resendBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore;
    private FirebaseUser firebaseUser;
    SharedPreferences sharedPreferences;
    private boolean verified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);
        getSupportActionBar().setTitle("Verify your Email");

        verifiedBtn = findViewById(R.id.emailVerified);
        resendBtn = findViewById(R.id.resendEmail);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF,MODE_PRIVATE);

        firebaseUser.reload();

        verifiedBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String userId = firebaseUser.getUid();
                firebaseUser.reload().addOnSuccessListener(unused -> {
                    if (firebaseUser.isEmailVerified()) {
                        fireStore.collection(Config.fireFolder).document(userId).update(Config.fireVerify, true);
                    }
                });

                fireStore.collection(Config.fireFolder).document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()){
                                verified = documentSnapshot.getBoolean(Config.fireVerify);
                                if (verified){
                                    saveLoginStatus(true);
                                    startActivity(new Intent(EmailVerifyActivity.this, HomeActivity.class));
                                    finish();
                                    Toast.makeText(EmailVerifyActivity.this, "WELCOME!", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(EmailVerifyActivity.this, "Email is not verified yet!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }


        });

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EmailVerifyActivity.this, "Verification Mail Sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EmailVerifyActivity.this, "Verification Mail not sent, "
                                +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void saveLoginStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Config.LOGIN_STATUS,status);
        editor.apply();
    }
}