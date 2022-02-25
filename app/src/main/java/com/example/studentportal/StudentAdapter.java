package com.example.studentportal;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<StudentModelClass> studentList;
    private StudentAdapter.ItemClickListener studentClickListener;

    public StudentAdapter(List<StudentModelClass> studentList, StudentAdapter.ItemClickListener studentClickListener){
        this.studentList = studentList;
        this.studentClickListener = studentClickListener;
    }

    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_info_design, parent, false);
        return new StudentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String adapter_name = studentList.get(position).getName();
        String adapter_roll = studentList.get(position).getRoll();

        holder.setData(adapter_name, adapter_roll);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentClickListener.onItemClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView roll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.list_student_name);
            roll = itemView.findViewById(R.id.list_student_id);
        }

        public void setData(String adapter_name, String adapter_roll) {

            name.setText(adapter_name);
            roll.setText(adapter_roll);
        }
    }

    interface ItemClickListener {
        public void onItemClicked(int position);
    }
}
