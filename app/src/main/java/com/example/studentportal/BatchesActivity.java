package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BatchesActivity extends AppCompatActivity implements BatchAdapter.ItemClickListener {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<BatchModelClass>batchList;
    BatchAdapter batchAdapter;

    SharedPreferences sharedPreferences;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batches);
        getSupportActionBar().setTitle("Batches of ICE");

        initData();
        initRecyclerView();
    }

    private void initData() {

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        batchList = new ArrayList<>();

        getBatches();

    }

    private void getBatches() {

        firestore.collection("Batches")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            String batchNo = document.getId();
                            String batchSession = document.getString("session");
                            Log.d("error_db", "onComplete: id: "+batchNo);
                            BatchModelClass model = new BatchModelClass(batchNo,batchSession);
                            batchList.add(model);
                            batchAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d("error_db", "onDataChange: called");
            Log.d("error_db", "onDataChange: isExist: "+snapshot.exists());
            if (snapshot.exists()){
                for (DataSnapshot snp: snapshot.getChildren()) {
                    String batchNo = snp.getKey();
                    String batchSession = snp.getValue().toString();
                    BatchModelClass model = new BatchModelClass(batchNo,batchSession);
                    batchList.add(model);
                    batchAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d("error_db", "onCancelled: error: "+error.getMessage());
        }
    };

    private void initRecyclerView() {

        recyclerView = findViewById(R.id.batch_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(recyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        batchAdapter = new BatchAdapter(batchList,this);
        recyclerView.setAdapter(batchAdapter);
        batchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(int position) {
        Log.d("error_db", "onItemClicked: clicked session: "+batchList.get(position).getSession());
        Toast.makeText(this, batchList.get(position).getSession(), Toast.LENGTH_SHORT).show();
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
        }
        if(item.getItemId() == R.id.menu_students){
            i = new Intent(this, BatchesActivity.class);
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

        return super.onOptionsItemSelected(item);
    }
}