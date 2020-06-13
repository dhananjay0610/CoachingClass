package com.alphaCoaching.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alphaCoaching.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SingleQuestionDetailActivity extends AppCompatActivity {
    private TextView question, qCount, TimeTaken, AverageTime, TotalScore;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int questionNumber;
    private FirebaseFirestore FireStore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String score;
    Button previous;
    Button next;
    String[] QuestionId = new String[45];
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_question_detail);

        Toolbar toolbar = findViewById(R.id.ToolbarOfSingleQuestionActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        question = findViewById(R.id.questionSingleQuestion);
        qCount = findViewById(R.id.quesNumSingleQuestion);
        option1 = findViewById(R.id.option1SingleQuestion);
        option2 = findViewById(R.id.option2SingleQuestion);
        option3 = findViewById(R.id.option3SingleQuestion);
        option4 = findViewById(R.id.option4SingleQuestion);

        TimeTaken = findViewById(R.id.TimeTakenSingleQuestion);
        AverageTime = findViewById(R.id.AverageTimeSingleQuestion);
        imageView = findViewById(R.id.imageViewForTickSingleQuestion);
        TotalScore = findViewById(R.id.TotalScoreSingleQuestion);
        FireStore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String quizId = intent.getStringExtra("QuizId");
        String quizTakenId = intent.getStringExtra("quickened");
        int questionNumber = intent.getIntExtra("questionNumber", 0);
        Log.d("SingleQuestionActivity", "-=-" + quizId + "-=-" + quizTakenId + "-=-" + questionNumber);
        getQuestionsList();
    }


    private void getQuestionsList() {
        String docId = getIntent().getStringExtra("QuizId");
        questionList = new ArrayList<>();
        FireStore.collection("questions").whereEqualTo("quizID", docId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot questions = task.getResult();
                assert questions != null;
                int i = 0;
                for (QueryDocumentSnapshot doc : questions) {
                    Log.d("SingleQuestionActivity", "document id" + doc.getId());
                    QuestionId[i] = doc.getId();
                    i++;
                    questionList.add(new Question(doc.getId(),
                            doc.getString("question"),
                            doc.getString("option1"),
                            doc.getString("option2"),
                            doc.getString("option3"),
                            doc.getString("option4"),
                            doc.getLong("correctOption"),
                            doc.getId()));
                }
                setCurrentQuestion();
            } else {
                Toast.makeText(SingleQuestionDetailActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        String quizTakenId = getIntent().getStringExtra("quickened");
        final String[] MaxScore = {null};
        assert quizTakenId != null;
        DocumentReference documentReference = db.collection("quizTaken").document(quizTakenId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    score = (String) documentSnapshot.get("score");
                    MaxScore[0] = (String) documentSnapshot.get("TotalScore");
                }
                TotalScore.setText("Score : " + (score) + "/" + MaxScore[0]);
            }
        });
    }

    private void setCurrentQuestion() {

        int questionNumber = getIntent().getIntExtra("questionNumber", 0);
        question.setText(questionList.get(questionNumber).getQuestion());
        option1.setText(questionList.get(questionNumber).getOptionA());
        option2.setText(questionList.get(questionNumber).getOptionB());
        option3.setText(questionList.get(questionNumber).getOptionC());
        option4.setText(questionList.get(questionNumber).getOptionD());
        qCount.setText(questionNumber + 1 + "/" + (questionList.size()));

//        questionNumber = 0;
        //To fetch from the quizTaken collection
        int n = (int) questionList.get(questionNumber).getCorrectOption();
        String ans = "";
        switch (n) {
            case 1:
                ans = (questionList.get(questionNumber).getOptionA());
                option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            case 2:
                ans = (questionList.get(questionNumber).getOptionB());
                option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            case 3:
                ans = (questionList.get(questionNumber).getOptionC());
                option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            case 4:
                ans = (questionList.get(questionNumber).getOptionD());
                option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
        }
        String QuestionId = questionList.get(questionNumber).getQuestionId();
        //fetching average time

        DocumentReference documentReference = db.collection("questions").document(QuestionId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    AverageTime.setText("Average : " + documentSnapshot.get("time"));
                    // score = (String) documentSnapshot.get("score");
                    Log.d("SingleQuestionActivity", "Average Time is :  " + documentSnapshot.get("time"));
                }
            }
        });
        String quizTakenId = getIntent().getStringExtra("quickened");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
        Query query = yourCollRef.whereEqualTo("quizTakenId", quizTakenId)
                .whereEqualTo("questionId", questionList.get(questionNumber).getQuestionId());
        final Object[] AttemptedAnswer = {null};
        final Object[] timeTaken = {""};
        String finalAns = ans;
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    AttemptedAnswer[0] = documentSnapshot.get("attemptedAnswer");
                    timeTaken[0] = documentSnapshot.get("timeTaken");
                    Log.d("SingleQuestionActivity", timeTaken[0] + "  Attempted ans is : " + AttemptedAnswer[0] + "document snapshot get " + documentSnapshot.get("attemptedAnswer"));

                }

                TimeTaken.setText("Time : " + timeTaken[0]);
                Log.d("SingleQuestionActivity", timeTaken[0] + " " + AttemptedAnswer[0]);
                if (AttemptedAnswer[0] == null) {
                    Toast.makeText(SingleQuestionDetailActivity.this, "Not attempted any answer", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.ic_close_black_24dp);
                } else {
                    Log.d("SingleQuestionActivity", finalAns + "  " + AttemptedAnswer[0]);

                    if (AttemptedAnswer[0].equals(finalAns)) {
                        imageView.setImageResource(R.drawable.ic_done_black_24dp);
                    } else {
                        imageView.setImageResource(R.drawable.ic_close_black_24dp);
                        if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionA())) {
                            option1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionB())) {
                            option2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionC())) {
                            option3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionD())) {
                            option4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        }
                    }
                }
            }
        });
    }
}