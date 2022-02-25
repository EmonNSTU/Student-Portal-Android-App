package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView pr_name, pr_id, pr_batch, pr_email, pr_phone, pr_blood, pr_occupation, pr_gender;
    private ImageView pr_image;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        pr_image = findViewById(R.id.view_profile_image);
        pr_name = findViewById(R.id.view_profile_name);
        pr_id = findViewById(R.id.view_profile_id);
        pr_batch = findViewById(R.id.view_profile_batch);
        pr_email = findViewById(R.id.view_profile_email);
        pr_phone = findViewById(R.id.view_profile_phone);
        pr_blood = findViewById(R.id.view_profile_blood);
        pr_occupation = findViewById(R.id.view_profile_occupation);
        pr_gender = findViewById(R.id.view_profile_gender);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        String user = null;
        if(bundle != null) {
            String userId = bundle.getString("USER_ID");

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

        }

    }
    //menu
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
            i = new Intent(this, HomeActivity.class);
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
            firebaseAuth.signOut();
            i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        if(item.getItemId() == R.id.menu_students){
            i = new Intent(this, BatchesActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}