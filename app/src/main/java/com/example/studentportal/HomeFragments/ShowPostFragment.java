package com.example.studentportal.HomeFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studentportal.Config;
import com.example.studentportal.ProfileActivity;
import com.example.studentportal.R;
import com.example.studentportal.adapter.PostAdapter;
import com.example.studentportal.adapter.ShowPostAdapter;
import com.example.studentportal.modelClasses.PostModelClass;
import com.example.studentportal.modelClasses.UserPostModel;
import com.example.studentportal.utils.SpManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Queue;

public class ShowPostFragment extends Fragment implements PostAdapter.OnItemClickListener {

    private onButtonClick listener;
    private ShowPostAdapter showPostAdapter;
    private RecyclerView recyclerView;
    private List<PostModelClass> postModelClassList;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private String userId;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private ImageView userImg;

    private ArrayList<UserPostModel> postList = new ArrayList<>();
    private PostAdapter adapter;
    private String TAG = "show_post";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_post, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        userImg = view.findViewById(R.id.currentUserImg);

        if(firebaseUser.getPhotoUrl() != null){
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(userImg);
        }

        TextView postFragBtn = view.findViewById(R.id.postFragmentBtn);
        postFragBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.buttonClicked();
            }
        });

        userImg.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), ProfileActivity.class)));


        recyclerView = view.findViewById(R.id.post_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true));
        postModelClassList = new ArrayList<>();

        setPostRecycler();
        return view;
    }

    private void setPostRecycler() {

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

                    if (snp.child("like").exists()) {
                        Log.d(TAG, "onDataChange: like exist");
                        long totalLike = 0;
                        for (DataSnapshot snpLike: snp.child("like").getChildren()) {
                            totalLike += snpLike.getChildrenCount();
                            String userId = snpLike.child("user_id").getValue().toString();
                            Log.d(TAG, "onDataChange: userId: "+userId);
                            if (userId.equals(SpManager.getString(requireContext(),SpManager.PREF_USER_ID))) {
                                Log.d(TAG, "onDataChange: is found");
                                item.setLiked(true);
                            }
                        }
                        item.setTotalLike(totalLike);
                    }

                    postList.add(item);
                }
                adapter = new PostAdapter(postList,requireContext(),ShowPostFragment.this);
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);

        if (context instanceof onButtonClick) {
            listener = (onButtonClick) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement Listener");
        }
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
                                    .equalTo(SpManager.getString(requireContext(),SpManager.PREF_USER_ID));

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
                                                .setValue(SpManager.getString(requireContext(),SpManager.PREF_USER_ID));

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
                .setValue(SpManager.getString(requireContext(),SpManager.PREF_USER_ID));
    }

    private void commentPost(UserPostModel item) {
        // comment post
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

    @Override
    public void onLikeClicked(UserPostModel item) {
        Log.d(TAG, "onLikeClicked: ");
        listPost(item);
    }

    @Override
    public void onCommentClicked(UserPostModel item) {
        Log.d(TAG, "onCommentClicked: ");
        commentPost(item);
    }



    @Override
    public void onItemDelete(String id,int position) {
        Log.d(TAG, "onItemDelete: ");
        deletePost(id,position);
    }



    public interface onButtonClick{
        void buttonClicked();
    }
}
