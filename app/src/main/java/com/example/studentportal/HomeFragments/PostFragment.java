package com.example.studentportal.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.studentportal.Config;
import com.example.studentportal.R;
import com.example.studentportal.modelClasses.PostUploadModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

public class PostFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private EditText postEditText;
    private String strPost, userId, myCurrentDateTime;
    private Button postButton;
    private DatabaseReference databaseReference;
    private ImageView creatorImg, postImg;
    private TextView addImg, removeImg;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        creatorImg = view.findViewById(R.id.postCreatorImg);
        postImg = view.findViewById(R.id.upPostImage);
        addImg = view.findViewById(R.id.upAddImage);
        removeImg = view.findViewById(R.id.upRemoveImg);
        postEditText = view.findViewById(R.id.postSpaceId);
        postButton = view.findViewById(R.id.postButton);
        progressBar = view.findViewById(R.id.upPostProgressBar);

        myCurrentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());

        databaseReference = FirebaseDatabase.getInstance().getReference();

        if(firebaseUser.getPhotoUrl() != null){
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(creatorImg);
        }

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                strPost = postEditText.getText().toString();
                if(!strPost.isEmpty()){
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

                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Write Something or Add Image to Post!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }

        });


        return view;
    }

}
