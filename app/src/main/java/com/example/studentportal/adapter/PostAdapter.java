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
import com.example.studentportal.utils.SpManager;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    public ArrayList<UserPostModel> postList;
    public Context context;
    public OnItemClickListener listener;

    public PostAdapter(ArrayList<UserPostModel> postList, Context context,OnItemClickListener listener) {
        this.postList = postList;
        this.context = context;
        this.listener = listener;
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

        if (item.getPost().isEmpty())
            holder.showPost.setVisibility(View.GONE);
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

        if (item.getUser_id().equals(SpManager.getString(context,SpManager.PREF_USER_ID))){
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }
        else holder.deleteBtn.setVisibility(View.GONE);

        if (item.isLiked() == null) {
            holder.likePost.setVisibility(View.VISIBLE);
            holder.notLikePost.setVisibility(View.GONE);
        }
        else {
            if (item.isLiked()) {
                holder.likePost.setVisibility(View.GONE);
                holder.notLikePost.setVisibility(View.VISIBLE);
            }
        }

        if (item.getTotalLike() == null) holder.likeCount.setText("0");
        else holder.likeCount.setText(String.valueOf(item.getTotalLike()));

        clickEvents(holder,item,position);
    }

    private void clickEvents(MyViewHolder holder, UserPostModel item,int position) {
        holder.likePost.setOnClickListener(view -> {
            listener.onLikeClicked(item);
            holder.likePost.setVisibility(View.GONE);
            holder.notLikePost.setVisibility(View.VISIBLE);

            long currentLike = Long.parseLong(holder.likeCount.getText().toString());
            holder.likeCount.setText(String.valueOf(currentLike+1));

        });

        holder.notLikePost.setOnClickListener(view -> {
            listener.onLikeClicked(item);
            holder.notLikePost.setVisibility(View.GONE);
            holder.likePost.setVisibility(View.VISIBLE);

            long currentLike = Long.parseLong(holder.likeCount.getText().toString());
            holder.likeCount.setText(String.valueOf(currentLike-1));


        });

        holder.commentPost.setOnClickListener(view -> {
            listener.onCommentClicked(item);
        });

        holder.deleteBtn.setOnClickListener(view -> {
            listener.onItemDelete(item.getId(),position);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView showPost, name_creator, batch_creator,likeCount;
        ImageView image_post, image_creator;
        ImageButton deleteBtn,likePost,notLikePost,commentPost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image_creator = itemView.findViewById(R.id.CreatorImg);
            name_creator = itemView.findViewById(R.id.CreatorName);
            batch_creator = itemView.findViewById(R.id.CreatorBatch);
            showPost = itemView.findViewById(R.id.postTextId);
            image_post = itemView.findViewById(R.id.showPostImage);
            deleteBtn = itemView.findViewById(R.id.delete_Post);
            likePost = itemView.findViewById(R.id.post_like);
            likeCount = itemView.findViewById(R.id.like_count);
            notLikePost = itemView.findViewById(R.id.post_not_like);
            commentPost = itemView.findViewById(R.id.post_comment);
        }
    }

    public interface OnItemClickListener {
        void onLikeClicked(UserPostModel item);
        void onCommentClicked(UserPostModel item);
        void onItemDelete(String id, int position);

    }
}
