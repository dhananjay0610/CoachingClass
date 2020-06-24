package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class QuizDetailActivity extends AppCompatActivity {

    private String mQuizDocId, mQuizTime, mQuizName, mQuizQuestions;
    private TextView quizName, quizQuestionNumber, quizTime;
    private Button quizStartButton;
    private FirebaseFirestore firestoreDb;
    private LinearLayout mProgressbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        Bundle bundle = getIntent().getExtras();
        mQuizDocId = bundle.getString("docID");
        mQuizTime = bundle.getString("quizTime");
        mQuizName = bundle.getString("quizName");
        mQuizQuestions = bundle.getString("quizQuestions");
        mProgressbar = findViewById(R.id.quizDetailProgressbar);
        if ((mQuizTime == null || mQuizTime.isEmpty()) || (mQuizName == null || mQuizName.isEmpty()) || (mQuizQuestions == null || mQuizQuestions.isEmpty())) {
            fetchQuizData(mQuizDocId);
        } else {
            initUi();
            setButtonClick();
        }
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

//    this function get the quiz information when activity open from the notification.
    private void fetchQuizData(String docId) {
        mProgressbar.setVisibility(View.VISIBLE);
        firestoreDb = FirebaseFirestore.getInstance();
        firestoreDb.collection(Constant.QUIZ_COLLECTION)
                .document(docId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        mQuizQuestions = documentSnapshot.get(Constant.QuizCollectionFields.QUESTION_NUMBER).toString();
                        mQuizTime = documentSnapshot.get(Constant.QuizCollectionFields.QUIZ_TIME).toString();
                        mQuizName = documentSnapshot.get(Constant.QuizCollectionFields.QUIZ_NAME).toString();

                        initUi();
                        setButtonClick();
                        mProgressbar.setVisibility(View.GONE);
                    }
                });
    }
}
