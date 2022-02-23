package com.example.studentportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class BatchesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<BatchModelClass>batchList;
    BatchAdapter batchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batches);
        getSupportActionBar().setTitle("Batches of ICE");

        initData();
        initRecyclerView();
    }

    private void initData() {

        batchList = new ArrayList<>();
        batchList.add(new BatchModelClass("8", "2019-2020"));
        batchList.add(new BatchModelClass("9", "2020-2021"));
        batchList.add(new BatchModelClass("10", "2021-2022"));
        batchList.add(new BatchModelClass("11", "2022-2023"));
        batchList.add(new BatchModelClass("7", "2018-2019"));
        batchList.add(new BatchModelClass("8", "2019-2020"));
        batchList.add(new BatchModelClass("9", "2020-2021"));
        batchList.add(new BatchModelClass("10", "2021-2022"));
        batchList.add(new BatchModelClass("11", "2022-2023"));
    }

    private void initRecyclerView() {

        recyclerView = findViewById(R.id.batch_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(recyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        batchAdapter = new BatchAdapter(batchList);
        recyclerView.setAdapter(batchAdapter);
        batchAdapter.notifyDataSetChanged();
    }
}