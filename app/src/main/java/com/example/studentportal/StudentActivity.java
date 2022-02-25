package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

    private void getStudents() {

        Bundle b = getIntent().getExtras();
        String user = null;
        if(b != null){
            user = b.getString(Config.fireBatch);

            int userId = Integer.valueOf(user);
            fireStore.collection(Config.fireFolder).whereEqualTo(Config.fireBatch, userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                String name = document.getString(Config.fireName);
                                String roll = document.getString(Config.fireRoll);
                                String userId = document.getId();
                                StudentModelClass model = new StudentModelClass(name, roll, userId);
                                studentList.add(model);
                                studentAdapter.notifyDataSetChanged();
                            }
                        }
                    });

        }

    }

    @Override
    public void onItemClicked(int position) {
        Toast.makeText(this, studentList.get(position).getUserId(), Toast.LENGTH_SHORT).show();
    }
}