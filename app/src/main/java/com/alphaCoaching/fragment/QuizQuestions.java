package com.alphaCoaching.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.alphaCoaching.R;
import com.alphaCoaching.activity.QuestionReview;

public class QuizQuestions extends Fragment {
    View view;
    String quizId;
    String quizTakenId;


    public QuizQuestions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_quiz_questions, container, false);


        Button button = view.findViewById(R.id.ButtonQuestionReview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplication(), QuestionReview.class);
                i.putExtra("QuizId", quizId);
                i.putExtra("quickened", quizTakenId);
                startActivity(i);
            }
        });





        return view;
    }
}