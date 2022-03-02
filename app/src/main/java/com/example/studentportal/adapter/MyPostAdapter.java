package com.example.studentportal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportal.Config;
import com.example.studentportal.R;
import com.example.studentportal.modelClasses.PostModelClass;
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

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {

    private List<PostModelClass>myPostList;
    private Context context;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference postImgReference, profileImgReference;

    public MyPostAdapter(Context context, List<PostModelClass> myPostList){
        this.myPostList = myPostList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        PostModelClass model = myPostList.get(position);

        String userID = model.getUserId();
        String postID = model.getPostId();
        String imageLoc = model.getImageUrl();
        String postStr = model.getPostText();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        String pathProfileImg = Config.StorageProfileFolder + "/" + userID + ".jpeg";
        String pathPostImg = Config.StoragePostFolder + "/" + userID + "/" + postID + ".jpeg";
        postImgReference = FirebaseStorage.getInstance().getReference().child(pathPostImg);
        profileImgReference = FirebaseStorage.getInstance().getReference().child(pathProfileImg);

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
                        holder.creator_name.setText(value.getString(Config.fireName));
                        holder.creator_batch.setText(String.valueOf(b));
                    }
                });
        if(postStr != null){
            holder.text_Post.setVisibility(View.VISIBLE);
            holder.text_Post.setText(postStr);
        }

        if(firebaseUser.getUid().equals(userID)){
            holder.delBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return myPostList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_Post, creator_name, creator_batch;
        ImageView image_post, image_creator;
        ImageButton delBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_creator = itemView.findViewById(R.id.CreatorImg);
            creator_name = itemView.findViewById(R.id.CreatorName);
            creator_batch = itemView.findViewById(R.id.CreatorBatch);
            text_Post = itemView.findViewById(R.id.postTextId);
            image_post = itemView.findViewById(R.id.showPostImage);
            delBtn = itemView.findViewById(R.id.delete_Post);

        }

    }
}
