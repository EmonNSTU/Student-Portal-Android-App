package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

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

                    if (isLogin()){
                        openHomeActivity();
                    }
                    else {
                        Intent i=new Intent(getBaseContext(),LoginActivity.class);
                        startActivity(i);
                    }
                    finish();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        background.start();
    }

    private boolean isLogin(){
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF,MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean(Config.LOGIN_STATUS,false);
        return status;
    }

    public void openHomeActivity(){
        Intent intent = new Intent(this, MainFragmentActivity.class);
        startActivity(intent);
    }

}