package com.example.studentportal.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentportal.R;
import com.example.studentportal.modelClasses.PostUploadModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class PostFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    EditText postEditText;
    String strPost, userId, myCurrentDateTime;
    Button postButton;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        postEditText = (EditText) view.findViewById(R.id.postSpaceId);
        postButton = view.findViewById(R.id.postButton);

        myCurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());

        databaseReference = FirebaseDatabase.getInstance().getReference();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strPost = postEditText.getText().toString();

                PostUploadModel model = new PostUploadModel(
                  strPost,
                  userId,
                  myCurrentDateTime

                );

                databaseReference.child("Post Data")
                        .child(userId)
                        .child(myCurrentDateTime).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Post Created Successfully", Toast.LENGTH_LONG).show();
                            postEditText.setText("");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
            }

        });


        return view;
    }

}
