package com.example.studentportal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportal.R;
import com.example.studentportal.modelClasses.BatchModelClass;

import java.util.List;


public class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.ViewHolder> {

    private List<BatchModelClass> batchList;
    private ItemClickListener itemClickListener;

    public BatchAdapter(List<BatchModelClass> batchList,ItemClickListener itemClickListener){
        this.batchList = batchList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public BatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_design_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String adapter_batch = batchList.get(position).getBatch();
        String adapter_session = batchList.get(position).getSession();

        holder.setData(adapter_batch, adapter_session);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return batchList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView batch;
        private TextView session;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            batch = itemView.findViewById(R.id.batch_number);
            session = itemView.findViewById(R.id.session_number);
        }

        public void setData(String adapter_batch, String adapter_session) {

            batch.setText(adapter_batch);
            session.setText(adapter_session);
        }
    }

    public interface ItemClickListener {
        public void onItemClicked(int position);
    }
}
