package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditActivity extends AppCompatActivity {

    private EditText ed_name, ed_batch, ed_email, ed_phone, ed_blood, ed_occupation;
    private RadioGroup ed_gender;
    private Button ed_update;
    private ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Profile Update");

        ed_name = findViewById(R.id.edit_profile_name);
        ed_batch = findViewById(R.id.edit_batch);
        ed_email = findViewById(R.id.edit_email);
        ed_phone = findViewById(R.id.edit_phone);
        ed_blood = findViewById(R.id.edit_blood_group);
        ed_occupation = findViewById(R.id.edit_occupation);
        ed_gender = findViewById(R.id.edit_gender);
        ed_update = findViewById(R.id.edit_profile_btn);
        progressBar = findViewById(R.id.edit_progressbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        String userId = firebaseUser.getUid();
        final String gender = Config.fireNone ;

        firestore.collection(Config.fireFolder).document(userId)
                .addSnapshotListener(this, (documentSnapshot,e)->{
                    ed_name.setHint(documentSnapshot.getString(Config.fireName));
                    ed_batch.setHint(documentSnapshot.getString(Config.fireBatch));
                    ed_email.setHint(documentSnapshot.getString(Config.fireMail));
                    ed_phone.setHint(documentSnapshot.getString(Config.firePhone));
                    ed_blood.setHint(documentSnapshot.getString(Config.fireBlood));
                    ed_occupation.setHint(documentSnapshot.getString(Config.fireOccupation));

                });

        ed_update.setOnClickListener(new View.OnClickListener() {

            String gender;

            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                String name = ed_name.getText().toString().trim();
                String batch = ed_batch.getText().toString().trim();
                String phone = ed_phone.getText().toString().trim();
                String email = ed_email.getText().toString().trim();
                String blood = ed_blood.getText().toString().trim();
                String occupation = ed_occupation.getText().toString().trim();

                String userId = firebaseUser.getUid();

                if (!name.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireName, name);
                }

                if (!batch.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireBatch, batch);
                }
                if (!email.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireMail, email);
                }

                if (!phone.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.firePhone, phone);
                }
                if (!blood.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireBlood, blood);
                }
                if (!occupation.isEmpty()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireOccupation, occupation);
                }

                if (isGenderSelected()) {
                    firestore.collection(Config.fireFolder).document(userId).update(Config.fireGender, gender);
                }

                firebaseUser.reload();
                startActivity(new Intent(EditActivity.this,ProfileActivity.class));
                finish();

            }

            public boolean isGenderSelected() {
                if (ed_gender.getCheckedRadioButtonId() == -1) {
                    return false;
                } else {
                    if (ed_gender.getCheckedRadioButtonId() == R.id.radio_male)
                        gender = "Male";
                    else gender = "Female";
                    return true;
                }
            }

        });

    }
}