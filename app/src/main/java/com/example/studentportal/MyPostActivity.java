package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.studentportal.HomeFragments.ShowPostFragment;
import com.example.studentportal.adapter.MyPostAdapter;
import com.example.studentportal.adapter.PostAdapter;
import com.example.studentportal.adapter.ShowPostAdapter;
import com.example.studentportal.modelClasses.PostModelClass;
import com.example.studentportal.modelClasses.UserPostModel;
import com.example.studentportal.utils.SpManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyPostActivity extends AppCompatActivity implements PostAdapter.OnItemClickListener {

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

    private ArrayList<UserPostModel> postList = new ArrayList<>();
    private PostAdapter adapter;
    private String TAG = "my_post";

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

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child(Config.USER_POSTS)
                .orderByChild("user_id")
                .equalTo(userId);

        query.addListenerForSingleValueEvent(postListener);

    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                postList = new ArrayList<>();
                for (DataSnapshot snp: snapshot.getChildren()) {
                    UserPostModel item = snp.getValue(UserPostModel.class);

                    if (snp.child("like").exists()) {
                        Log.d(TAG, "onDataChange: like exist");
                        long totalLike = 0;
                        for (DataSnapshot snpLike: snp.child("like").getChildren()) {
                            totalLike += snpLike.getChildrenCount();
                            String userId = snpLike.child("user_id").getValue().toString();
                            Log.d(TAG, "onDataChange: userId: "+userId);
                            if (userId.equals(SpManager.getString(MyPostActivity.this,SpManager.PREF_USER_ID))) {
                                Log.d(TAG, "onDataChange: is found");
                                item.setLiked(true);
                            }
                        }
                        item.setTotalLike(totalLike);
                    }

                    postList.add(item);
                }
                adapter = new PostAdapter(postList,MyPostActivity.this, MyPostActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else toast("No data found");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    private void deletePost(String id,int position) {
        databaseReference.child(Config.USER_POSTS).child(id).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.remove(position);
                        adapter.notifyDataSetChanged();
                        toast("Post is deleted");
                    }
                });
    }

    private void listPost(UserPostModel item) {

        databaseReference.child(Config.USER_POSTS).child(item.getId()).child("like")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            Query query = databaseReference.child(Config.USER_POSTS).child(item.getId())
                                    .child("like")
                                    .orderByChild("user_id")
                                    .equalTo(SpManager.getString(MyPostActivity.this,SpManager.PREF_USER_ID));

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot snp: snapshot.getChildren()){
                                            databaseReference.child(Config.USER_POSTS).child(item.getId()).child("like")
                                                    .child(snp.getKey())
                                                    .removeValue();
                                        }

                                    }
                                    else {
                                        String key = databaseReference.child(Config.USER_POSTS).child(item.getId()).child("like").push().getKey();
                                        databaseReference.child(Config.USER_POSTS).child(item.getId()).child("like")
                                                .child(key)
                                                .child("user_id")
                                                .setValue(SpManager.getString(MyPostActivity.this,SpManager.PREF_USER_ID));

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else {
                            storeLikeData(item.getId());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void storeLikeData(String id) {
        String key = databaseReference.child(Config.USER_POSTS).child(id).child("like").push().getKey();
        databaseReference.child(Config.USER_POSTS).child(id).child("like")
                .child(key)
                .child("user_id")
                .setValue(SpManager.getString(MyPostActivity.this, SpManager.PREF_USER_ID));

    }

        @Override
    public void onLikeClicked(UserPostModel item) {
        listPost(item);
    }

    @Override
    public void onCommentClicked(UserPostModel item) {

    }

    @Override
    public void onItemDelete(String id, int position) {
        deletePost(id,position);
    }
}