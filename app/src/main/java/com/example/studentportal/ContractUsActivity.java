package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class ContractUsActivity extends AppCompatActivity {

    private TextView facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_us);
        getSupportActionBar().hide();

        facebook = findViewById(R.id.facebook_link);
        facebook.setMovementMethod(LinkMovementMethod.getInstance());
    }
}