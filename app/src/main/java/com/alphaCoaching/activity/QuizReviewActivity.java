package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.QuizTakenQuestion;
import com.alphaCoaching.R;
import com.alphaCoaching.adapter.GridAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizReviewActivity extends AppCompatActivity {

    private TextView textViewMarks;
    private TextView TotalAttempt;
    private ProgressBar progressBar;
    private FirebaseFirestore FireStore;
    private DocumentReference documentReference;
    private static List<Question> questionList;
    private static List<QuizTakenQuestion> takenQuestionList;
    private Button ButtonQuestionReview;
    private String quizId, quizTakenId;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_review);
        textViewMarks = findViewById(R.id.TotalMarks);
        ButtonQuestionReview = findViewById(R.id.ButtonQuestionReview);
        gridView = findViewById(R.id.grid);
        FireStore = FirebaseFirestore.getInstance();
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbarOfQuizReviewActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        quizId = intent.getStringExtra("QuizId");
        quizTakenId = intent.getStringExtra("quickened");
        Log.d("QuizReviewActivity", "Quiz id : " + quizId + " quizTaken id : " + quizTakenId);

        getQuestionsList();

        //Going to the questionReview Activity
        ButtonQuestionReview.setOnClickListener(view -> {
            Intent i = new Intent(QuizReviewActivity.this, QuestionReview.class);
            i.putExtra("QuizId", quizId);
            i.putExtra("quickened", quizTakenId);
            startActivity(i);
        });


    }

    private void getQuestionsList() {
        //ArrayList to store the question class variable
        questionList = new ArrayList<>();
        FireStore.collection(Constant.QUESTION_COLLECTION).whereEqualTo(Constant.QuestionCollectionFields.QUIZ_ID, quizId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot questions = task.getResult();
                assert questions != null;
                for (QueryDocumentSnapshot doc : questions) {
                    Question question = doc.toObject(Question.class);
                    question.setId(doc.getId());
                    questionList.add(question);
                }
                getTakenQuestionList();
            } else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        final String[] MaxScore = {null};
        final String[] score = new String[1];
        documentReference = FireStore.collection(Constant.QUIZ_TAKEN_COLLECTION).document(quizTakenId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    score[0] = String.valueOf(documentSnapshot.get(Constant.QuizTakenCollectionFields.SCORE));
                    MaxScore[0] = String.valueOf(documentSnapshot.get(Constant.QuizTakenCollectionFields.TOTAL_SCORE));
                }
                String text = (score[0]) + "/" + MaxScore[0];
                textViewMarks.setText(text);
            } else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBackground() {

        //Array to store the grid text
        String[] number = new String[questionList.size()];

        //Array to store the background of the  grid item
        int[] questionBackground = new int[questionList.size()];

        //Main iteration over all the questions from the quizTakenQuestions Collection
        for (int i = 0; i < questionList.size(); i++) {
            int n = (int) questionList.get(i).getCorrectOption();
            String x = questionList.get(i).getOptionA();
            String ans = "";
            switch (n) {
                case 1:
                    ans = (questionList.get(i).getOption1());
                    break;
                case 2:
                    ans = (questionList.get(i).getOption2());
                    break;
                case 3:
                    ans = (questionList.get(i).getOption3());
                    break;
                case 4:
                    ans = (questionList.get(i).getOption4());
                    break;
            }
            String finalAns = ans;
            int background;
            QuizTakenQuestion takenQuestion = getTakenQuestion(questionList.get(i).getId());
            if (takenQuestion == null) {
                return;
            }
            String attemptedAns = takenQuestion.getAttemptedAnswer();
            if (attemptedAns == null) {
                background = -1;
            } else if (attemptedAns.equals(finalAns)) {
                background = 1;
            } else {
                background = 0;
            }
            questionBackground[i] = background;
        }
        for (int i = 0; i < questionList.size(); i++) {
            number[i] = String.valueOf(i + 1);
        }
        GridAdapter adapter = new GridAdapter(getApplicationContext(), number, questionBackground);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /* Display the Toast */
//                Log.d("QuizReviewActivity","============"+position);
//                Toast.makeText(getApplicationContext(), "hh"+l+" "+position, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(QuizReviewActivity.this, SingleQuestionDetailActivity.class);
                i.putExtra("QuizId", quizId);
                i.putExtra("quickened", quizTakenId);
                i.putExtra("questionNumber", position);
                startActivity(i);
            }
        });

        //OnItemClickListener on the grid item

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
////                Intent i = new Intent(QuizReviewActivity.this, QuestionReview.class);
////                i.putExtra("QuizId", quizId);
////                i.putExtra("quickened", quizTakenId);
////                i.putExtra("", quizTakenId);
////                i.putExtra("quickened", quizTakenId);
////                startActivity(i);
//
//                Log.d("QuizReviewActivity", "Position : " + position);
//                String selectedItem = adapterView.getItemAtPosition(position).toString();
//                Toast.makeText(QuizReviewActivity.this, "Position : " + position + " id : " + id + " " + selectedItem, Toast.LENGTH_SHORT).show();
//            }
//        });

        //following variables to display text in the progressbar and the accuracy
        int totalAttempts = 0;
        int totalCorrect = 0;
        int totalUnAttempts = 0;
        for (int j = 0; j < questionList.size(); j++) {
            if (questionBackground[j] != -1) {
                totalAttempts++;
                if (questionBackground[j] == 1) {
                    totalCorrect++;
                }
            } else
                totalUnAttempts++;
        }
        Log.d("QuizReviewActivity", "The no of 0 is : " + totalAttempts);
        Log.d("QuizReviewActivity", "The no of 1 is : " + totalCorrect);
        Log.d("QuizReviewActivity", "The no of -1 is : " + totalUnAttempts);
        TotalAttempt = findViewById(R.id.TotalAttempt);
        progressBar = findViewById(R.id.accuracyProgressBar);
        String accuracy = totalCorrect + "/" + totalAttempts;
        float accuracyPercentage = ((float) totalCorrect / (float) totalAttempts) * 100;
//        int a=Integer.parseInt(accuracy);
//        a=a*100;
        DecimalFormat df = new DecimalFormat("#.##");
        accuracyPercentage = Float.parseFloat(df.format(accuracyPercentage));
        String text = accuracy + " " + accuracyPercentage + "%";

        Log.d("QuizReviewActivity", "The value is of accuracy percentage : " + accuracyPercentage + " " + totalAttempts + " " + totalCorrect);
        TotalAttempt.setText(text);
        progressBar.setMax(100);
        progressBar.setProgress((int) accuracyPercentage);
    }

    private QuizTakenQuestion getTakenQuestion(String id) {
        for (QuizTakenQuestion takenQuestion : takenQuestionList) {
            if (takenQuestion.getQuestionId().equals(id)) {
                return takenQuestion;
            }
        }
        return null;
    }

    private void getTakenQuestionList() {
        takenQuestionList = new ArrayList<>();
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference yourCollRef = rootRef.collection(Constant.QUIZ_TAKEN_QUESTION_COLLECTION);
        Query query = yourCollRef.whereEqualTo(Constant.QuizTakenQuestionsFields.QUIZ_TAKEN_ID, quizTakenId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    QuizTakenQuestion takenQuestion = documentSnapshot.toObject(QuizTakenQuestion.class);
                    assert takenQuestion != null;
                    takenQuestion.setId(documentSnapshot.getId());
                    takenQuestionList.add(takenQuestion);
                }
                setBackground();
            }
        });
    }
}
