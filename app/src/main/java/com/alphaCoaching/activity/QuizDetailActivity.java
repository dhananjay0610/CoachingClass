package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alphaCoaching.R;

public class QuizDetailActivity extends AppCompatActivity {

    private String mQuizDocId, mQuizTime, mQuizName, mQuizQuestions;
    private TextView quizName, quizQuestionNumber, quizTime;
    private Button quizStartButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        Bundle bundle = getIntent().getExtras();
        mQuizDocId = bundle.getString("docID");
        mQuizTime = bundle.getString("quizTime");
        mQuizName = bundle.getString("quizName");
        mQuizQuestions = bundle.getString("quizQuestions");
        initUi();
        setButtonClick();
    }

    private void initUi() {
        quizName = findViewById(R.id.quizDetailName);
        quizQuestionNumber = findViewById(R.id.quizDetailQuestionsNumber);
        quizTime = findViewById(R.id.quizDetailTime);
        quizStartButton = findViewById(R.id.quizDetailStartButton);

        quizName.setText(mQuizName);
        quizTime.setText(mQuizTime + " minutes");
        quizQuestionNumber.setText(mQuizQuestions + " questions");
    }

    private void setButtonClick() {
        quizStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuizDetailActivity.this, QuestionDetailActivity.class);
                i.putExtra("docID", mQuizDocId);
                i.putExtra("quizTime", mQuizTime);
                startActivity(i);
            }
        });
    }
}
