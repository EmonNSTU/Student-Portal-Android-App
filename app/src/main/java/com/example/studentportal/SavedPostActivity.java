package com.example.studentportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.studentportal.adapter.MyPostAdapter;
import com.example.studentportal.adapter.PostAdapter;
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

public class SavedPostActivity extends AppCompatActivity implements PostAdapter.OnItemClickListener {

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
        setContentView(R.layout.activity_saved_post);

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
    
    public void setPostRecycler(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Config.USER_POSTS).addListenerForSingleValueEvent(postListener);
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                postList = new ArrayList<>();
                for (DataSnapshot snp: snapshot.getChildren()) {
                    UserPostModel item = snp.getValue(UserPostModel.class);

                    if (!SpManager.getString(SavedPostActivity.this,item.getId()).equals("DNF")){
                        if (snp.child("like").exists()) {
                            Log.d(TAG, "onDataChange: like exist");
                            long totalLike = 0;
                            for (DataSnapshot snpLike: snp.child("like").getChildren()) {
                                totalLike += snpLike.getChildrenCount();
                                String userId = snpLike.child("user_id").getValue().toString();
                                Log.d(TAG, "onDataChange: userId: "+userId);
                                if (userId.equals(SpManager.getString(SavedPostActivity.this,SpManager.PREF_USER_ID))) {
                                    Log.d(TAG, "onDataChange: is found");
                                    item.setLiked(true);
                                }
                            }
                            item.setTotalLike(totalLike);
                        }

                        postList.add(item);
                    }
                }
                adapter = new PostAdapter(postList,SavedPostActivity.this, SavedPostActivity.this);
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

    private void listPost(UserPostModel item) {

        databaseReference.child(Config.USER_POSTS).child(item.getId()).child("like")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            Query query = databaseReference.child(Config.USER_POSTS).child(item.getId())
                                    .child("like")
                                    .orderByChild("user_id")
                                    .equalTo(SpManager.getString(SavedPostActivity.this,SpManager.PREF_USER_ID));

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
                                                .setValue(SpManager.getString(SavedPostActivity.this,SpManager.PREF_USER_ID));

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
                .setValue(SpManager.getString(SavedPostActivity.this, SpManager.PREF_USER_ID));

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

    }
}