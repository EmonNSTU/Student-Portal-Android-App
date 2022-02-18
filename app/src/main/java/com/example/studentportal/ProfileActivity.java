package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView pr_name, pr_id, pr_batch, pr_email, pr_phone, pr_blood, pr_occupation, pr_gender;
    Button pr_edit_btn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    private static final String fireFolder = "Users";
    private static final String fireName = "Name";
    private static final String fireMail = "Email";
    private static final String fireBatch = "Batch";
    private static final String firePhone = "Phone";
    private static final String fireGender = "Gender";
    private static final String fireNone = "Unavailable";
    private static final String fireVerify = "Verified";
    private static final String fireRoll = "Student ID";
    private static final String fireBlood = "Blood Group";
    private static final String fireOccupation = "Occupation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        firestore.collection(fireFolder).document(userId)
                .addSnapshotListener(this, (documentSnapshot,e)->{
                    pr_name.setText(documentSnapshot.getString(fireName));
                    pr_id.setText(documentSnapshot.getString(fireRoll));
                    pr_batch.setText(documentSnapshot.getString(fireBatch));
                    pr_email.setText(documentSnapshot.getString(fireMail));
                    pr_phone.setText(documentSnapshot.getString(firePhone));
                    pr_blood.setText(documentSnapshot.getString(fireBlood));
                    pr_occupation.setText(documentSnapshot.getString(fireOccupation));
                    pr_gender.setText(documentSnapshot.getString(fireGender));
                });

    }
}