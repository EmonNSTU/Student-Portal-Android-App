package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class DrawerLayout extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    ImageView n_image;
    TextView n_name, n_mail;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private View navHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawyer_layout);

        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        navigationView = findViewById(R.id.navigation_view);
        navHeader = navigationView.getHeaderView(0);

        n_image = navHeader.findViewById(R.id.nev_image);
        n_name = navHeader.findViewById(R.id.nev_name);
        n_mail = navHeader.findViewById(R.id.nev_mail);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);

        if(firebaseUser.getPhotoUrl() != null){
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(n_image);
        }
        String userId = firebaseUser.getUid();
        firestore.collection(Config.fireFolder).document(userId)
                .addSnapshotListener(this, (documentSnapshot,e)->{
                    n_name.setText(documentSnapshot.getString(Config.fireName));
                    n_mail.setText(documentSnapshot.getString(Config.fireMail));
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i;
        if(item.getItemId() == R.id.home_menu){
            i = new Intent(this,HomeActivity.class);
            startActivity(i);
        }
        if(item.getItemId() == R.id.profile_menu){
            i = new Intent(this,ProfileActivity.class);
            startActivity(i);
        }
        return false;
    }
}