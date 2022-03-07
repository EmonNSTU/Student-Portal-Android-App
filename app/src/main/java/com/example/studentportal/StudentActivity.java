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

import com.example.studentportal.adapter.StudentAdapter;
import com.example.studentportal.modelClasses.StudentModelClass;
import com.example.studentportal.utils.Config;
import com.example.studentportal.utils.SpManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentActivity extends AppCompatActivity implements StudentAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    List<StudentModelClass>studentList;
    StudentAdapter studentAdapter;

    SharedPreferences sharedPreferences;

    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        getSupportActionBar().setTitle("Students");

        initData();
        initRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerView() {

        recyclerView = findViewById(R.id.student_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        studentAdapter = new StudentAdapter(studentList,this);
        recyclerView.setAdapter(studentAdapter);
        studentAdapter.notifyDataSetChanged();

    }

    private void initData() {
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        studentList = new ArrayList<>();

        getStudents();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getStudents() {

        Bundle b = getIntent().getExtras();
        String user;
        if(b != null){
            user = b.getString(Config.fireBatch);

            int batch = Integer.parseInt(user);
            fireStore.collection(Config.fireFolder).whereEqualTo(Config.fireBatch, batch)
                    .get()
                    .addOnCompleteListener(task -> {
                        for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {
                            String name = document.getString(Config.fireName);
                            String roll = document.getString(Config.fireRoll);
                            String userId = document.getId();
                            StudentModelClass model = new StudentModelClass(name, roll, userId);
                            studentList.add(model);
                            studentAdapter.notifyDataSetChanged();
                        }
                    });

        }

    }

    @Override
    public void onItemClicked(int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        String id1 = studentList.get(position).getUserId();
        String id2 = firebaseAuth.getCurrentUser().getUid();

        if(id1.equals(id2)){
            startActivity(new Intent(this, ProfileActivity.class));
        }
        else {
            Intent intent = new Intent(this, ViewProfileActivity.class);
            Bundle b = new Bundle();
            b.putString("USER_ID", studentList.get(position).getUserId());
            intent.putExtras(b);
            startActivity(intent);
        }
    }
    //menu bar
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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

            SpManager.clearData(this);
        }
        if(item.getItemId() == R.id.menu_students){
            i = new Intent(this, BatchesActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}