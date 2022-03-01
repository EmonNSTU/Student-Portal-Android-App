package com.example.studentportal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportal.Config;
import com.example.studentportal.R;
import com.example.studentportal.modelClasses.PostUploadModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {

    private List<PostUploadModel>myPostList;
    private ItemClickListener itemClickListener;

    public MyPostAdapter(List<PostUploadModel> myPostList, MyPostAdapter.ItemClickListener itemClickListener){
        this.myPostList = myPostList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design, parent, false);
        return new MyPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String adapter_textPost = myPostList.get(position).getPostText();
        String adapter_userId = myPostList.get(position).getUserId();
        String adapter_postId = myPostList.get(position).getPostId();

        holder.setData(adapter_textPost, adapter_userId, adapter_postId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myPostList.size();
    }

    public interface ItemClickListener{
        void onItemClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textPost, creator, creatorBatch;
        private FirebaseAuth firebaseAuth;
        private FirebaseUser firebaseUser;
        private FirebaseFirestore firestore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textPost = itemView.findViewById(R.id.postTextId);
            creator = itemView.findViewById(R.id.CreatorName);
            creatorBatch = itemView.findViewById(R.id.CreatorBatch);

        }


        public void setData(String adapter_textPost, String adapter_userId, String adapter_postId) {

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            firestore = FirebaseFirestore.getInstance();

            textPost.setText(adapter_textPost);

        }
    }
}
