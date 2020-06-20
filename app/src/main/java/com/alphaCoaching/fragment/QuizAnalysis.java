package com.alphaCoaching.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alphaCoaching.R;
import com.alphaCoaching.activity.QuestionReview;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class QuizAnalysis extends Fragment {
    View view;
    String quizId;
    String quizTakenId;
    String TotalMarks;
    String accuracyProgressValue;
    int accuracyProgress;            //max value is 100
    String TotalAttempts;
    String textProgressBar;
    int TwoProgressFirst;
    int TwoProgressSecondary;
    int TwoProgressTotal;
    int CircularProgress;
    String centerText;


    TextView textViewTotalMarks;
    TextView textViewTotalAttempt;
    TextView textViewaccuracyProgressValue;
    ProgressBar accuracyProgressBar;
    TextView textOfProgressBar;
    ProgressBar twoProgress;
    CircularProgressBar circularProgressBar;
    Button seeClassResultButton;
    TextView textViewcenterText;

    public QuizAnalysis() {
    }

    public QuizAnalysis(String quizId, String quizTakenId, String TotalMarks, String accuracyProgressValue, int accuracyProgress, String totalAttempts, String textProgressBar, int TwoProgressFirst, int TwoProgressSecondary, int twoProgressTotal, int CircularProgress, String centerText) {

        this.quizId = quizId;
        this.quizTakenId = quizTakenId;
        this.TotalMarks = TotalMarks;
        this.textProgressBar = textProgressBar;
        this.accuracyProgressValue = accuracyProgressValue;
        this.accuracyProgress = accuracyProgress;
        this.TotalAttempts = totalAttempts;
        this.TwoProgressFirst = TwoProgressFirst;
        this.TwoProgressSecondary = TwoProgressSecondary;
        this.TwoProgressTotal = twoProgressTotal;
        this.CircularProgress = CircularProgress;
        this.centerText = centerText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("fragment", quizId + " " + quizTakenId + " " + TotalMarks + " " + accuracyProgressValue + " " + accuracyProgress + " " + TotalAttempts + " " + textProgressBar + " " + TwoProgressFirst + " " + TwoProgressSecondary + " " + TwoProgressTotal + " " + CircularProgress + " " + centerText);

        view = inflater.inflate(R.layout.fragment_quiz_analysis, container, false);
        seeClassResultButton = view.findViewById(R.id.seeClassResultButton);
        textViewTotalMarks = view.findViewById(R.id.TotalMarks);
        textViewTotalAttempt = view.findViewById(R.id.TotalAttempt);
        textViewaccuracyProgressValue = view.findViewById(R.id.accuracyProgressValue);
        accuracyProgressBar = view.findViewById(R.id.accuracyProgressBar);
        textOfProgressBar = view.findViewById(R.id.textOfProgressBar);
        twoProgress = view.findViewById(R.id.twoProgress);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        textViewcenterText = view.findViewById(R.id.centerText);

        textViewTotalMarks.setText(TotalMarks);
        textViewaccuracyProgressValue.setText(accuracyProgressValue);
        accuracyProgressBar.setMax(100);
        accuracyProgressBar.setProgress(accuracyProgress);
        textViewTotalAttempt.setText(TotalAttempts);

        SpannableString text1 = new SpannableString(textProgressBar);
        text1.setSpan(new ForegroundColorSpan(Color.GREEN), 8, 9, 0);
        text1.setSpan(new ForegroundColorSpan(Color.RED), 10, 11, 0);
        text1.setSpan(new ForegroundColorSpan(Color.LTGRAY), 12, text1.length(), 0);
        textOfProgressBar.setText(text1, TextView.BufferType.SPANNABLE);

        twoProgress.setMax(TwoProgressTotal);
        twoProgress.setProgress(TwoProgressFirst);
        twoProgress.setSecondaryProgressTintList(ColorStateList.valueOf(Color.RED));
        twoProgress.setSecondaryProgress(TwoProgressSecondary);
        circularProgressBar.setProgressMax(TwoProgressTotal);
        circularProgressBar.setProgress(CircularProgress);
        textViewcenterText.setText(centerText);
        seeClassResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("fragment", quizId + " " + quizTakenId + " " + TotalMarks + " " + accuracyProgressValue + " " + accuracyProgress + " " + TotalAttempts + " " + TwoProgressFirst + " " + TwoProgressSecondary + " " + TwoProgressTotal + " " + CircularProgress + " " + centerText);
                Intent i = new Intent(getActivity(), QuestionReview.class);
                i.putExtra("QuizId", quizId);
                startActivity(i);
            }
        });
        return view;
    }
}