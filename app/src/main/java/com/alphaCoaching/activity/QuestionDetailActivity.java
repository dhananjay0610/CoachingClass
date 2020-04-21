package com.alphaCoaching.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoaching.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.alphaCoaching.activity.MainActivity.documentId;

public class QuestionDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView question,qCount,timer;
    private Button option1,option2,option3,option4;
    private List<Question> questionList;
    private int questionNumber;
    private CountDownTimer countDown;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;
    private Button next;
    private int score;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        score=0;

        question=findViewById(R.id.question);
        qCount=findViewById(R.id.quesNum);
        timer=findViewById(R.id.countDown);

        option1=findViewById(R.id.option1);
        option2=findViewById(R.id.option2);
        option3=findViewById(R.id.option3);
        option4=findViewById(R.id.option4);
        next=findViewById(R.id.next);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
        next.setOnClickListener(this);

        firestore=FirebaseFirestore.getInstance();

        loadingDialog=new Dialog(QuestionDetailActivity.this);

        getQuestionsList();
        String docId=getIntent().getStringExtra("docID");

    }
     private void getQuestionsList(){
        String docId=getIntent().getStringExtra("docID");
        questionList=new ArrayList<>();
        firestore.collection("questions").whereEqualTo("quizID","docID")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot questions=task.getResult();
                    for (QueryDocumentSnapshot doc:questions)
                    {
                       Log.d("Neha",doc.toString());
                       questionList.add(new Question(doc.getString("question"),
                                doc.getString("option1"),
                                doc.getString("option2"),
                                doc.getString("option3"),
                                doc.getString("option4"),
                                Integer.valueOf(doc.getString("correctOption"))
                                ));
                    }
                    setQuestion();
                }else {
                    Toast.makeText(QuestionDetailActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void setQuestion(){
        timer.setText(String.valueOf(10));

        question.setText(questionList.get(0).getQuestion());
        option1.setText(questionList.get(0).getOptionA());
        option2.setText(questionList.get(0).getOptionB());
        option3.setText(questionList.get(0).getOptionC());
        option4.setText(questionList.get(0).getOptionD());

        qCount.setText(String.valueOf(1)+"/"+String.valueOf(questionList.size()));

        startTimer();
        questionNumber=0;
    }

    private void startTimer(){
         countDown =new CountDownTimer(12000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished<10000)
                timer.setText(String.valueOf(millisUntilFinished/1000));

            }

            @Override
            public void onFinish() {
                changeQuestion();
            }
        };
        countDown.start();
    }

    @Override
    public void onClick(View v) {
        int selectedOption=0;
        switch (v.getId())
        {
            case R.id.option1:
                selectedOption=1;
                option2.setEnabled(false);
                option3.setEnabled(false);
                option4.setEnabled(false);
            break;
            case R.id.option2:
                selectedOption=2;
                option1.setEnabled(false);
                option3.setEnabled(false);
                option4.setEnabled(false);
                break;
            case R.id.option3:
                selectedOption=3;
                option1.setEnabled(false);
                option2.setEnabled(false);
                option4.setEnabled(false);
                break;
            case R.id.option4:
                selectedOption=4;
                option1.setEnabled(false);
                option2.setEnabled(false);
                option3.setEnabled(false);
                break;
            case R.id.next:
                changeQuestion();
                return;
            default:
        }
       // countDown.cancel();
        checkAnswer(selectedOption,v);
    }
    private void checkAnswer(int selectedOption,View view){
        if (selectedOption==questionList.get(questionNumber).getCorrectOption()){
            //Right Answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
            score++;
        }
        else {
            //Wrong Answer
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
           /* switch (questionList.get(questionNumber).getCorrectOption())
            {
                case 1:
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }*/
        }
       /*Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        },2000);*/
    }

    private void changeQuestion()
    {
        if (questionNumber<questionList.size()-1)
        {
            questionNumber++;

            playAnim(question,0,0);
            playAnim(option1,0,1);
            playAnim(option2,0,2);
            playAnim(option3,0,3);
            playAnim(option4,0,4);

            qCount.setText(String.valueOf(questionNumber+1)+"/"+String.valueOf(questionList.size()));

            timer.setText(String.valueOf(10));
            //startTimer();
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);
        }
        else
        {
            // Go to Score Activity
            Intent intent=new Intent(QuestionDetailActivity.this,ScoreActivity.class);
            intent.putExtra("SCORE",String.valueOf(score)+"/"+String.valueOf(questionList.size()));
            startActivity(intent);
            QuestionDetailActivity.this.finish();
        }
    }
    private void playAnim(final View view, final int value, final int viewNum)
    {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (value==0)
                        {
                            switch (viewNum)
                            {
                                case 0:
                                    ((TextView)view).setText(questionList.get(questionNumber).getQuestion());
                                    break;
                                case 1:
                                    ((Button)view).setText(questionList.get(questionNumber).getOptionA());
                                    break;
                                case 2:
                                    ((Button)view).setText(questionList.get(questionNumber).getOptionB());
                                    break;
                                case 3:
                                    ((Button)view).setText(questionList.get(questionNumber).getOptionC());
                                    break;
                                case 4:
                                    ((Button)view).setText(questionList.get(questionNumber).getOptionD());
                                    break;
                            }
                            if (viewNum!=0)
                                ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#696880")));


                            playAnim(view,1,viewNum);
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

    }
}
