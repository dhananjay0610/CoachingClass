package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Fade;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.widget.Button;
import android.widget.TextView;

import com.alphaCoaching.R;

public class ScoreActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        setupWindowAnimation();


    TextView score = findViewById(R.id.score);
        String score_st=getIntent().getStringExtra("SCORE");
        score.setText(score_st);

        button=findViewById(R.id.button_done);
        button.setOnClickListener(view -> {
            Intent intent=new Intent(ScoreActivity.this,MainActivity.class);
            startActivity(intent);
        });

    }
    private void setupWindowAnimation(){
        Transition slide= TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(slide);

    }

}

//public class ScoreActivity extends AppCompatActivity {
//    Button button;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_score);
//
//        TextView score = findViewById(R.id.score);
//        String score_st=getIntent().getStringExtra("SCORE");
//        score.setText(score_st);
//
//        button=findViewById(R.id.button_done);
//        button.setOnClickListener(view -> {
//            Intent intent=new Intent(ScoreActivity.this,MainActivity.class);
//            startActivity(intent);
//        });
//
//    }
//}
