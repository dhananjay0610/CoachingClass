package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.alphaCoaching.R;

public class ScoreActivity extends AppCompatActivity {
    private TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score=findViewById(R.id.score);
        String score_st=getIntent().getStringExtra("SCORE");
        score.setText(score_st);
    }
}
