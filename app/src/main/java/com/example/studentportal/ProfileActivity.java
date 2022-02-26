package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView pr_name, pr_id, pr_batch, pr_email, pr_phone, pr_blood, pr_occupation, pr_gender;
    Button pr_edit_btn;
    ImageView pr_image;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    SharedPreferences sharedPreferences;


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

        if(firebaseUser.getPhotoUrl() != null){
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(pr_image);
        }

        firestore.collection(Config.fireFolder).document(userId)
                .addSnapshotListener(this, (documentSnapshot,e)->{

                    int b = documentSnapshot.getLong(Config.fireBatch).intValue();
                    pr_name.setText(documentSnapshot.getString(Config.fireName));
                    pr_id.setText(documentSnapshot.getString(Config.fireRoll));
                    pr_batch.setText(String.valueOf(b));
                    pr_email.setText(documentSnapshot.getString(Config.fireMail));
                    pr_phone.setText(documentSnapshot.getString(Config.firePhone));
                    pr_blood.setText(documentSnapshot.getString(Config.fireBlood));
                    pr_occupation.setText(documentSnapshot.getString(Config.fireOccupation));
                    pr_gender.setText(documentSnapshot.getString(Config.fireGender));
                });

        pr_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bar_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent i;
        if(item.getItemId() == R.id.menu_home){
            i = new Intent(this, MainFragmentActivity.class);
            startActivity(i);
        }
        if(item.getItemId() == R.id.menu_profile){
            i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        }
        if(item.getItemId() == R.id.menu_logout){

            sharedPreferences = getSharedPreferences(Config.SHARED_PREF,MODE_PRIVATE);
            firebaseAuth = FirebaseAuth.getInstance();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Config.LOGIN_STATUS,false);
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId() == R.id.menu_students){
            i = new Intent(this, BatchesActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}