package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Home");

        logoutBtn = (Button) findViewById(R.id.logout_bn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                openLoginActivity();
                finish();
            }
        });
    }
    public void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}