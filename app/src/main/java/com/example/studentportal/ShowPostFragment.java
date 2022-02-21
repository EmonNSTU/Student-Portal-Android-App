package com.example.studentportal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ShowPostFragment extends Fragment {

    private onButtonClick listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_post, container, false);

        Button postFragBtn = view.findViewById(R.id.postFragmentBtn);
        postFragBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.buttonClicked();
            }
        });
        return view;
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
