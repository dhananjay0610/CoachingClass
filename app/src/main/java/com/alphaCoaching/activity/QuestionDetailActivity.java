package com.alphaCoaching.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoaching.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;
import static com.alphaCoaching.activity.MainActivity.documentId;

public class QuestionDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView question, qCount, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int questionNumber;
    private CountDownTimer countDownperquiz;
    private FirebaseFirestore firestore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fireAuth;
    private Dialog loadingDialog;
    private int score;
    ArrayList<Long> arr = new ArrayList<>();
    HashMap<Integer, Long> mp = new HashMap<>();

    //Array needed to store data in order to add them in the database
    String[] QuestionId = new String[45];
    String[] attemptedanswer = new String[45];
    long[] Timetaken = new long[45];
    int i = 0;

    Boolean examend = false;

    long starttime;
    long endtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        score = 0;
        for (int i = 0; i < 100; i++)
            arr.add((long) 0);

        Toolbar toolbar = findViewById(R.id.toolbarofquestiondetailactivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        question = findViewById(R.id.question);
        qCount = findViewById(R.id.quesNum);
        timer = findViewById(R.id.countDown);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        Button next = findViewById(R.id.next);
        Button previous = findViewById(R.id.previous);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        firestore = FirebaseFirestore.getInstance();

        loadingDialog = new Dialog(QuestionDetailActivity.this);

        getQuestionsList();
        String docId = getIntent().getStringExtra("docID");
        long quizTime = getIntent().getLongExtra("quizTime", 2000);

        //Log.d("QuestionDetailActivity", quizTime+"   "+docId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }


    //method to get current time
    public long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formate = new SimpleDateFormat("HH:mm:ss");
        String date = formate.format(calendar.getTime());
        return calendar.getTimeInMillis() / 1000;
    }

    private void getQuestionsList() {
        String docId = getIntent().getStringExtra("docID");
        questionList = new ArrayList<>();
        firestore.collection("questions").whereEqualTo("quizID", docId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot questions = task.getResult();
                    assert questions != null;
                    int i = 0;
                    for (QueryDocumentSnapshot doc : questions) {
                        Log.d("QuestionActivity", "document id" + doc.getId());
                        QuestionId[i] = doc.getId();
                        i++;
                        questionList.add(new Question(doc.getString("question"),
                                doc.getString("option1"),
                                doc.getString("option2"),
                                doc.getString("option3"),
                                doc.getString("option4"),
                                Integer.parseInt(Objects.requireNonNull(doc.getString("correctOption")))
                                , doc.getId()
                        ));
                    }
                    if (questionList.size() == 0) {
                        Toast.makeText(getAppContext(), "Question list is empty in this quiz.", Toast.LENGTH_LONG).show();
                        QuestionDetailActivity.this.finish();
                    }
                    setQuestion();
                } else {
                    Toast.makeText(QuestionDetailActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setQuestion() {
        long quizTime = getIntent().getLongExtra("quizTime", 2000);
        timer.setText("Time: " + String.valueOf(quizTime));
        question.setText(questionList.get(0).getQuestion());
        option1.setText(questionList.get(0).getOptionA());
        option2.setText(questionList.get(0).getOptionB());
        option3.setText(questionList.get(0).getOptionC());
        option4.setText(questionList.get(0).getOptionD());
        qCount.setText(String.valueOf(1) + "/" + String.valueOf(questionList.size()));
        startTimer();
        starttime = getCurrentTime();
        questionNumber = 0;
    }

    private void startTimer() {
        int ans = 0;
        long quizTime = getIntent().getLongExtra("quizTime", 2000);
        final long[] mTimeleftinmillis = {quizTime * 60000};
        countDownperquiz = new CountDownTimer(mTimeleftinmillis[0], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeleftinmillis[0] = millisUntilFinished;
                int minutes = (int) (mTimeleftinmillis[0] / 1000) / 60;
                int seconds = (int) (mTimeleftinmillis[0] / 1000) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                timer.setText("Time :- " + timeLeftFormatted);
                //  if (millisUntilFinished < 10000)
                // timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                //to check if exam has end
                if (!examend) {
                    Intent intent = new Intent(QuestionDetailActivity.this, ScoreActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("SCORE", (score) + "/" + (questionList.size()));
                    startActivity(intent);
                    QuestionDetailActivity.this.finish();
                    examend = true;
                    // changeQuestion();
                }
            }
        };
        countDownperquiz.start();
    }

    //variable to iterate over array TimeTaken ,AttemptedAnswer
    int k = 0;

    @Override
    public void onClick(View v) {
        int selectedOption = 0;

        long a = 0;
        if (v.getId() == (R.id.next)) {
            endtime = getCurrentTime();
            arr.add(arr.get(questionNumber) + endtime - starttime);
            if (mp.containsKey(questionNumber))
                mp.put(questionNumber, mp.get(questionNumber) + endtime - starttime);
            else
                mp.put(questionNumber, endtime - starttime);
            Toast.makeText(this, "Time Taken: " + mp.get(questionNumber), Toast.LENGTH_SHORT).show();
            Timetaken[k] = mp.get(questionNumber);
            k++;
        }
        if (v.getId() == R.id.previous) {
            starttime = getCurrentTime();
        }
        switch (v.getId()) {
            case R.id.option1:
                selectedOption = 1;
                option4.setEnabled(false);
                option2.setEnabled(false);
                option3.setEnabled(false);
                break;
            case R.id.option2:
                selectedOption = 2;
                option1.setEnabled(false);
                option4.setEnabled(false);
                option3.setEnabled(false);
                break;
            case R.id.option3:
                selectedOption = 3;
                option1.setEnabled(false);
                option2.setEnabled(false);
                option4.setEnabled(false);
                break;
            case R.id.option4:
                selectedOption = 4;
                option1.setEnabled(false);
                option2.setEnabled(false);
                option3.setEnabled(false);
                break;
            case R.id.previous:
                previousQuestion();
                return;
            case R.id.next:
                changeQuestion();
                return;
            default:
        }
        checkAnswer(selectedOption, v);

        String option = "";
        if (selectedOption == 1)
            option = (questionList.get(0).getOptionA());
        else if (selectedOption == 2)
            option = (questionList.get(0).getOptionB());
        else if (selectedOption == 3)
            option = (questionList.get(0).getOptionC());
        else if (selectedOption == 4)
            option = (questionList.get(0).getOptionD());
        attemptedanswer[k] = option;
    }

    private void checkAnswer(int selectedOption, View view) {
        if (selectedOption == questionList.get(questionNumber).getCorrectOption()) {
            //Right Answer
            view.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
            score++;
        } else {
            //Wrong Answer
            (view).setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
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

    private void previousQuestion() {
        if (questionNumber != 0) {
            questionNumber--;
            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText((questionNumber + 1) + "/" + (questionList.size()));

            timer.setText("Time:- " + (" "));
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);
        }
    }

    private void changeQuestion() {

        starttime = getCurrentTime();
        if (questionNumber < questionList.size() - 1) {

            questionNumber++;

            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText((questionNumber + 1) + "/" + (questionList.size()));

            timer.setText("Time:- " + " ");
            //  startTimer();
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

        } else {
            examend = true;
            // Go to Score Activity
            Intent intent = new Intent(QuestionDetailActivity.this, ScoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("SCORE", (score) + "/" + (questionList.size()));
            startActivity(intent);
            QuestionDetailActivity.this.finish();

            //adding data to the FireStore quizTaken collection
            String docId = getIntent().getStringExtra("docID");
            fireAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = fireAuth.getCurrentUser();
            assert currentUser != null;
            String user_Uuid = currentUser.getUid();
            //fetching username from the user collection
            DocumentReference documentReference = db.collection("users").document(user_Uuid);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                            String FirstName = (String) documentSnapshot.get("firstName");
                            String LastName = (String) documentSnapshot.get("lastName");
                            String UserName = FirstName + LastName;
                            Map<String, Object> data = new HashMap<>();
                            data.put("quizId", docId);
                            data.put("score", String.valueOf(score));
                            data.put("TotalScore", String.valueOf(questionList.size()));
                            data.put("userId", user_Uuid);
                            data.put("userName", UserName);
                            firestore.collection("quizTaken")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("QuestionActivity", "DocumentSnapshot written with ID: " + documentReference.getId());

                                            //adding data to the quizTakenQuestions collection
                                            for (int questionnumber = 0; questionnumber < questionList.size(); questionnumber++) {
                                                Map<String, Object> data1 = new HashMap<>();
                                                data1.put("attemptedAnswer", attemptedanswer[questionnumber]);
                                                data1.put("questionId", QuestionId[questionnumber]);
                                                data1.put("quizTakenId", documentReference.getId());
                                                data1.put("timeTaken", mp.get(questionnumber));
                                                firestore.collection("quizTakenQuestions")
                                                        .add(data1)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d("QuestionActivity", "DocumentSnapshot written with ID: " + documentReference.getId());
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("QuestionActivity", "Error adding document", e);
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.w("QuestionActivity", "Error adding document", e));
                        }
                    }
                }
            });

        }
    }

    private void playAnim(final View view, final int value, final int viewNum) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (value == 0) {
                            switch (viewNum) {
                                case 0:
                                    ((TextView) view).setText(questionList.get(questionNumber).getQuestion());
                                    break;
                                case 1:
                                    ((Button) view).setText(questionList.get(questionNumber).getOptionA());
                                    break;
                                case 2:
                                    ((Button) view).setText(questionList.get(questionNumber).getOptionB());
                                    break;
                                case 3:
                                    ((Button) view).setText(questionList.get(questionNumber).getOptionC());
                                    break;
                                case 4:
                                    ((Button) view).setText(questionList.get(questionNumber).getOptionD());
                                    break;
                            }
                            if (viewNum != 0)
                                view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#696880")));
                            playAnim(view, 1, viewNum);
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
