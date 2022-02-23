package com.example.studentportal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.ViewHolder> {

    private List<BatchModelClass> batchList;

    public BatchAdapter(List<BatchModelClass> batchList){
        this.batchList = batchList;
    }

    @NonNull
    @Override
    public BatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_design_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchAdapter.ViewHolder holder, int position) {

        String adapter_batch = batchList.get(position).getBatch();
        String adapter_session = batchList.get(position).getSession();

        holder.setData(adapter_batch, adapter_session);

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
}
