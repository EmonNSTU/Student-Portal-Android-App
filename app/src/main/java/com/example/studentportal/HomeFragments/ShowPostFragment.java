package com.example.studentportal.HomeFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentportal.R;
import com.example.studentportal.adapter.ShowPostAdapter;
import com.example.studentportal.modelClasses.PostUploadModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowPostFragment extends Fragment {

    private onButtonClick listener;
    private ShowPostAdapter showPostAdapter;
    private RecyclerView recyclerView;
    private List<PostUploadModel> postUploadModelList;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_post, container, false);

        TextView postFragBtn = view.findViewById(R.id.postFragmentBtn);
        postFragBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.buttonClicked();
            }
        });


        recyclerView = (RecyclerView) view.findViewById(R.id.post_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postUploadModelList = new ArrayList<>();

        showPostAdapter = new ShowPostAdapter(getActivity(), postUploadModelList);
        recyclerView.setAdapter(showPostAdapter);

        setPostRecycler();




        return view;
    }

    private void setPostRecycler() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Post Data");

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postUploadModelList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                        PostUploadModel postUploadModel = dataSnapshot1.getValue(PostUploadModel.class);
                        postUploadModelList.add(postUploadModel);
                    }

                }

                showPostAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);

        if (context instanceof onButtonClick) {
            listener = (onButtonClick) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement Listener");
        }
    }

    public interface onButtonClick{
        public void buttonClicked();
    }
}
