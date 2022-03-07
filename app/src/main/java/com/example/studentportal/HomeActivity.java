package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.studentportal.HomeFragments.CreatePostFragment;
import com.example.studentportal.HomeFragments.ShowPostFragment;
import com.example.studentportal.utils.Config;
import com.example.studentportal.utils.SpManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ShowPostFragment.onButtonClick {

    androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    ImageView n_image;
    TextView n_name, n_mail;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    SharedPreferences sharedPreferences;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private View navHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Home");

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

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

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


        fragmentTransaction.add(R.id.container_fragment, new ShowPostFragment());
        fragmentTransaction.commit();

        getProfile();
    }

    private void getProfile() {
        String userId = Objects.requireNonNull(firebaseAuth.getUid());
        firestore.collection(Config.fireFolder).document(userId)
                .addSnapshotListener(this, (documentSnapshot,e)->{

                    int batch = documentSnapshot.getLong(Config.fireBatch).intValue();
                    String userName = documentSnapshot.getString(Config.fireName);
                    String userProfileImageUrl = documentSnapshot.getString(Config.fireProfileImageUrl);

                    SpManager.saveString(HomeActivity.this,SpManager.PREF_BATCH,String.valueOf(batch));
                    SpManager.saveString(HomeActivity.this,SpManager.PREF_USER_ID,userId);
                    SpManager.saveString(HomeActivity.this,SpManager.PREF_USER_NAME,userName);
                    //SpManager.saveString(HomeActivity.this,SpManager.PREF_USER_PROFILE_IMAGE,userProfileImageUrl);
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
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.home_nev){
            finish();
            startActivity(getIntent());
        }
        if(item.getItemId() == R.id.profile_nev){
            i = new Intent(this,ProfileActivity.class);
            startActivity(i);
        }
        if(item.getItemId() == R.id.myPost_nev){
            i = new Intent(this,MyPostActivity.class);
            startActivity(i);
        }

        if(item.getItemId() == R.id.savePost_nev){
            i = new Intent(this,SavedPostActivity.class);
            startActivity(i);
        }

        if(item.getItemId() == R.id.students_nev){
            i = new Intent(this,BatchesActivity.class);
            startActivity(i);
        }
        if(item.getItemId() == R.id.contact_nev){
            i = new Intent(this,ContractUsActivity.class);
            startActivity(i);
        }

        return true;
    }

    @Override
    public void buttonClicked() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new CreatePostFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}