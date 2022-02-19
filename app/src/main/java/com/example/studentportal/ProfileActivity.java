package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    TextView pr_name, pr_id, pr_batch, pr_email, pr_phone, pr_blood, pr_occupation, pr_gender;
    Button pr_edit_btn;
    ImageView pr_image;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");

        pr_image = findViewById(R.id.profile_image);
        pr_name = findViewById(R.id.profile_name);
        pr_id = findViewById(R.id.profile_id);
        pr_batch = findViewById(R.id.profile_batch);
        pr_email = findViewById(R.id.profile_email);
        pr_phone = findViewById(R.id.profile_phone);
        pr_blood = findViewById(R.id.profile_blood);
        pr_occupation = findViewById(R.id.profile_occupation);
        pr_gender = findViewById(R.id.profile_gender);
        pr_edit_btn = findViewById(R.id.edit_profile_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        String userId = firebaseUser.getUid();

        firestore.collection(Config.fireFolder).document(userId)
                .addSnapshotListener(this, (documentSnapshot,e)->{
                    pr_name.setText(documentSnapshot.getString(Config.fireName));
                    pr_id.setText(documentSnapshot.getString(Config.fireRoll));
                    pr_batch.setText(documentSnapshot.getString(Config.fireBatch));
                    pr_email.setText(documentSnapshot.getString(Config.fireMail));
                    pr_phone.setText(documentSnapshot.getString(Config.firePhone));
                    pr_blood.setText(documentSnapshot.getString(Config.fireBlood));
                    pr_occupation.setText(documentSnapshot.getString(Config.fireOccupation));
                    pr_gender.setText(documentSnapshot.getString(Config.fireGender));
                });

        pr_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditActivity.class));
            }
        });
    }
}