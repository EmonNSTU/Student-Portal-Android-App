package com.example.studentportal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studentportal.R;
import com.example.studentportal.modelClasses.UserPostModel;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    public ArrayList<UserPostModel> postList;
    public Context context;

    public PostAdapter(ArrayList<UserPostModel> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_design, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserPostModel item = postList.get(position);

        holder.name_creator.setText(item.getUser_name());
        holder.batch_creator.setText(item.getBatch());

        if (item.getPost().isEmpty()) holder.showPost.setVisibility(View.GONE);
        else {
            holder.showPost.setVisibility(View.VISIBLE);
            holder.showPost.setText(item.getPost());
        }

        if (!item.getImage_url().equals("")) {
            holder.image_post.setVisibility(View.VISIBLE);
            Log.d("adapter_ss", "onBindViewHolder: image_url: "+item.getImage_url());
            Glide.with(context).load(item.getImage_url()).into(holder.image_post);
        }
        else {
            Log.d("adapter_ss", "onBindViewHolder: gone");
            holder.image_post.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView showPost, name_creator, batch_creator;
        ImageView image_post, image_creator;
        ImageButton deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image_creator = itemView.findViewById(R.id.CreatorImg);
            name_creator = itemView.findViewById(R.id.CreatorName);
            batch_creator = itemView.findViewById(R.id.CreatorBatch);
            showPost = itemView.findViewById(R.id.postTextId);
            image_post = itemView.findViewById(R.id.showPostImage);
            deleteBtn = itemView.findViewById(R.id.delete_Post);
        }
    }
}
