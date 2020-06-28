package com.alphaCoaching.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.Note;
import com.alphaCoaching.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class QuizDetailActivity extends AppCompatActivity {

    private String mQuizDocId, mQuizTime, mQuizName, mQuizQuestions;
    private TextView quizName, quizQuestionNumber, quizTime, quizMissMessage;
    private Button quizStartButton;
    private FirebaseFirestore firestoreDb;
    private LinearLayout mProgressbar;
    private Long quizStartDate;
    private Handler mCheckQuizActiveHandler;
    private long FIFTEEN_MINUTES = 15 * 60 * 1000;
    private long ONE_DAY_IN_MILLIS = 1 * 24 * 60 * 60 * 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        Bundle bundle = getIntent().getExtras();
        mQuizDocId = bundle.getString("docID");
        mQuizTime = bundle.getString("quizTime");
        mQuizName = bundle.getString("quizName");
        mQuizQuestions = bundle.getString("quizQuestions");
        quizStartDate = bundle.getLong("quizStartTime");
        mProgressbar = findViewById(R.id.quizDetailProgressbar);
        quizMissMessage = findViewById(R.id.quizMissMessage);
        mCheckQuizActiveHandler = new Handler();
        if ((mQuizTime == null || mQuizTime.isEmpty()) || (mQuizName == null || mQuizName.isEmpty()) || (mQuizQuestions == null || mQuizQuestions.isEmpty())) {
            fetchQuizData(mQuizDocId);
        } else {
            initUi();
            setButtonClick();
            checkQuizIsActiveOrNot();
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

    private void checkQuizIsActiveOrNot() {
        mCheckQuizActiveHandler.post(new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                if (quizStartDate > currentTime) {
                    if (quizStartDate - currentTime >= ONE_DAY_IN_MILLIS) {
                        quizMissMessage.setText("Quiz will starts at: " + getStringDate(quizStartDate, "dd/MM hh:mm"));
                        quizMissMessage.setTextColor(Color.BLACK);
                        quizMissMessage.setVisibility(View.VISIBLE);
                    } else {
                        quizMissMessage.setText("Quiz will starts in: " + getDate(quizStartDate - currentTime, "hh:mm:ss"));
                        quizMissMessage.setTextColor(Color.BLACK);
                        quizMissMessage.setVisibility(View.VISIBLE);
                    }
                    quizStartButton.setEnabled(false);
                } else if (currentTime - quizStartDate < FIFTEEN_MINUTES) {
                    quizMissMessage.setText("Start the quiz in " + getDate(quizStartDate + FIFTEEN_MINUTES - currentTime, "mm:ss") + ". Othserwise you will miss the quiz!");
                    quizMissMessage.setTextColor(Color.GREEN);
                    quizMissMessage.setVisibility(View.VISIBLE);
                    quizStartButton.setEnabled(true);
                } else {
                    quizStartButton.setEnabled(false);
                    quizMissMessage.setText("You missed the quiz.");
                    quizMissMessage.setTextColor(Color.RED);
                    quizMissMessage.setVisibility(View.VISIBLE);
                    mCheckQuizActiveHandler.removeCallbacksAndMessages(null);
                }
                mCheckQuizActiveHandler.postDelayed(this, 1000);
            }
        });
    }

    private String getDate(long milliSeconds, String dateFormat) {
        String str = "";
        long secs = TimeUnit.MILLISECONDS.toSeconds(milliSeconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliSeconds);
        if (hours >= 1) {
            str = (hours) + ":" + (minutes % 60) + ":" + (secs % 60);
        } else {
            str = (secs / 60) + ":" + (secs % 60);
        }
        return str;
    }

    private String getStringDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String str = formatter.format(calendar.getTime());

        return str;
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
                        Note quizModel = documentSnapshot.toObject(Note.class);
                        mQuizQuestions = documentSnapshot.get(Constant.QuizCollectionFields.QUESTION_NUMBER).toString();
                        mQuizTime = documentSnapshot.get(Constant.QuizCollectionFields.QUIZ_TIME).toString();
                        mQuizName = documentSnapshot.get(Constant.QuizCollectionFields.QUIZ_NAME).toString();
                        quizStartDate = quizModel.getQuizDate().getSeconds() * 1000;

                        initUi();
                        setButtonClick();
                        checkQuizIsActiveOrNot();
                        mProgressbar.setVisibility(View.GONE);
                    }
                });
    }
}
