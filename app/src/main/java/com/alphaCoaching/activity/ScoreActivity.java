package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.alphaCoaching.R;

public class ScoreActivity extends AppCompatActivity {
    private TextView score;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score=findViewById(R.id.score);
        String score_st=getIntent().getStringExtra("SCORE");
        score.setText(score_st);

        button=findViewById(R.id.button_done);
        button.setOnClickListener(view -> {
            Intent intent=new Intent(ScoreActivity.this,MainActivity.class);
            startActivity(intent);
        });

    }
}
