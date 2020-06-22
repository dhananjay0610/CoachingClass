package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.QuizTakenQuestion;
import com.alphaCoaching.R;
import com.alphaCoaching.adapter.PagerAdapter;
import com.alphaCoaching.fragment.QuizAnalysis;
import com.alphaCoaching.fragment.QuizQuestions;
import com.google.android.material.tabs.TabLayout;
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
    private String quizId, quizTakenId;
    String TotalMarks;
    int accuracyProgress;
    String TotalAttempts;
    String accuracyProgressValue;
    String textProgressBar;
    int TwoProgressFirst;
    int TwoProgressSecondary;
    int TwoProgressTotal;
    int CircularProgress;
    String centerText;
    ViewPager viewPager;
    private static List<Question> questionList;
    private static List<QuizTakenQuestion> takenQuestionList;
    private FirebaseFirestore FireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_review);
        FireStore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        quizId = intent.getStringExtra("QuizId");
        quizTakenId = intent.getStringExtra("quickened");

        TabLayout tabLayout = findViewById(R.id.tabBox);
        viewPager = findViewById(R.id.ViewPager);
        getQuestionsList();
        tabLayout.setupWithViewPager(viewPager);
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
        DocumentReference documentReference = FireStore.collection(Constant.QUIZ_TAKEN_COLLECTION).document(quizTakenId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    score[0] = String.valueOf(documentSnapshot.get(Constant.QuizTakenCollectionFields.SCORE));
                    MaxScore[0] = String.valueOf(documentSnapshot.get(Constant.QuizTakenCollectionFields.TOTAL_SCORE));
                }
                TotalMarks = (score[0]) + "/" + MaxScore[0];

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
        float accuracyPercentages = ((float) totalCorrect / (float) totalAttempts) * 100;
        DecimalFormat df = new DecimalFormat("##.##");
        accuracyPercentages = Float.parseFloat(df.format(accuracyPercentages));
        float attemptPercentage = ((float) totalAttempts / (float) questionList.size()) * 100;
        String text = totalAttempts + "/" + questionList.size() + " " + attemptPercentage + "%";

        Log.d("QuizReviewActivity", "The value of accuracy percentage  is : " + accuracyPercentages + totalCorrect + " / " + totalAttempts + " ");
        TotalAttempts = text;

        accuracyProgressValue = accuracyPercentages + "%";

        accuracyProgress = (int) accuracyPercentages;
        textProgressBar = ("C/W/U : " + totalCorrect + "/" + (totalAttempts - totalCorrect) + "/" + totalUnAttempts);
        TwoProgressTotal = questionList.size();
        CircularProgress = totalCorrect;
        float k = ((float) totalCorrect / (float) questionList.size()) * 100;
        k = Float.parseFloat(df.format(k));
        centerText = k + "%";
        TwoProgressFirst = totalCorrect;
        TwoProgressSecondary = totalAttempts;

        PagerAdapter pagerAdapter = new com.alphaCoaching.adapter.PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new QuizAnalysis(quizId, quizTakenId, TotalMarks, accuracyProgressValue, accuracyProgress, TotalAttempts, textProgressBar, TwoProgressFirst, TwoProgressSecondary, TwoProgressTotal, CircularProgress, centerText), "Analysis");
        Log.d("fragment", quizId + " " + quizTakenId + " " + TotalMarks + " " + accuracyProgressValue + " " + accuracyProgress + " " + TotalAttempts + " " + textProgressBar + " " + TwoProgressFirst + " " + TwoProgressSecondary + " " + TwoProgressTotal + " " + CircularProgress + " " + centerText);
        pagerAdapter.addFragment(new QuizQuestions(quizId, quizTakenId), "Questions");
        viewPager.setAdapter(pagerAdapter);
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