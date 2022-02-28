package com.example.studentportal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportal.R;
import com.example.studentportal.modelClasses.PostUploadModel;

import java.util.List;

public class ShowPostAdapter extends RecyclerView.Adapter<ShowPostAdapter.MyPostShowViewHolder> {

    Context context;
    List<PostUploadModel> postList;

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

        holder.showPost.setText(model.getPostText());

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyPostShowViewHolder extends RecyclerView.ViewHolder {

        TextView showPost;
        public MyPostShowViewHolder(@NonNull View itemView) {
            super(itemView);

            showPost = itemView.findViewById(R.id.postTextId);
        }
    }
}
