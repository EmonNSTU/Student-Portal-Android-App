package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.studentportal.adapter.MyPostAdapter;
import com.example.studentportal.adapter.ShowPostAdapter;
import com.example.studentportal.modelClasses.PostModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyPostActivity extends AppCompatActivity {

    private MyPostAdapter myPostAdapter;
    private RecyclerView recyclerView;
    private List<PostModelClass> postModelClassList;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private String userId;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        getSupportActionBar().setTitle("My Posts");

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        recyclerView = findViewById(R.id.myPostRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        postModelClassList = new ArrayList<>();

        myPostAdapter = new MyPostAdapter(getApplicationContext(), postModelClassList);
        recyclerView.setAdapter(myPostAdapter);
        myPostAdapter.notifyDataSetChanged();

        setPostRecycler(userId);
    }

    private void setPostRecycler(String userId) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Post Data").child(userId);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postModelClassList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    PostModelClass postModelClass = dataSnapshot.getValue(PostModelClass.class);
                    postModelClassList.add(postModelClass);
                    myPostAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MyPostActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }



    //menu Bar
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