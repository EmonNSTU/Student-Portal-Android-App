package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(2000);

                    Intent i=new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(i);

                    finish();
                } catch (Exception e) {
                }
            }
        };
        background.start();
    }

}