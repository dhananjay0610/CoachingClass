package com.alphaCoaching.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.alphaCoaching.R;
import com.alphaCoaching.activity.QuestionReview;

public class QuizAnalysis extends Fragment {
    View view;
    String quizId;
    String quizTakenId;


    public QuizAnalysis() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        assert getArguments() != null;
//        String quizId = savedInstanceState.getString("quizId");
//        String quizTakenId = getArguments().getString("quizTakenId");

        view = inflater.inflate(R.layout.fragment_quiz_analysis, container, false);
        Button button = view.findViewById(R.id.seeClassResultButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Log.d("fragment", quizId + " " + quizTakenId);
//                Intent i = new Intent(getActivity().getApplication(), QuestionReview.class);
//                i.putExtra("QuizId", quizId);
////              i.putExtra("quickened", quizTakenId);
//                startActivity(i);
            }
        });

        return view;
    }
}