package com.example.studentportal.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studentportal.Config;
import com.example.studentportal.R;
import com.example.studentportal.modelClasses.PostUploadModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

public class ShowPostAdapter extends RecyclerView.Adapter<ShowPostAdapter.MyPostShowViewHolder> {

    Context context;
    List<PostUploadModel> postList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference profileImgReference, postImgReference;

    public ShowPostAdapter(Context context, List<PostUploadModel> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyPostShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post_design, parent, false);
        return new MyPostShowViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyPostShowViewHolder holder, int position) {

        PostUploadModel model = postList.get(position);

        String userID = model.getUserId();
        String postID = model.getPostId();
        String imageLoc = model.getImageUrl();
        String postStr = model.getPostText();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        String pathProfileImg = Config.StorageProfileFolder + "/" + userID + ".jpeg";
        String pathPostImg = Config.StoragePostFolder + "/" + userID + "/" + postID + ".jpeg";
        profileImgReference = FirebaseStorage.getInstance().getReference().child(pathProfileImg);
        postImgReference = FirebaseStorage.getInstance().getReference().child(pathPostImg);

        try {
            final File profileFile = File.createTempFile(userID, "jpeg");
            final File postFile = File.createTempFile(postID, "jpeg");
            profileImgReference.getFile(profileFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(profileFile.getAbsolutePath());
                holder.image_creator.setImageBitmap(bitmap);
            });
            postImgReference.getFile(postFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(postFile.getAbsolutePath());

                    if(bitmap != null){
                        holder.image_post.setVisibility(View.VISIBLE);
                        holder.image_post.setImageBitmap(bitmap);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        firestore.collection(Config.fireFolder).document(userID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        int b = value.getLong(Config.fireBatch).intValue();
                        holder.name_creator.setText(value.getString(Config.fireName));
                        holder.batch_creator.setText(String.valueOf(b));
                    }
                });
        if(postStr != null){
            holder.showPost.setVisibility(View.VISIBLE);
            holder.showPost.setText(postStr);
        }

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyPostShowViewHolder extends RecyclerView.ViewHolder {

        TextView showPost, name_creator, batch_creator;
        ImageView image_post, image_creator;

        public MyPostShowViewHolder(@NonNull View itemView) {
            super(itemView);
            image_creator = itemView.findViewById(R.id.CreatorImg);
            name_creator = itemView.findViewById(R.id.CreatorName);
            batch_creator = itemView.findViewById(R.id.CreatorBatch);
            showPost = itemView.findViewById(R.id.postTextId);
            image_post = itemView.findViewById(R.id.showPostImage);
        }
    }
}
