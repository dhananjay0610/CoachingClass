package com.alphaCoaching.activity;

import android.animation.Animator;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class QuestionDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView question, qCount, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int questionNumber;
    private FirebaseFirestore firestore;
    private int quizScore;
    private String quizId;
    private Long quizTime;
    private Button next, previous;
    private Toolbar toolbar;
    private LinearLayout mProgressBar;
    private HashMap<Integer, Long> timeTalken = new HashMap<>();

    //Array needed to store data in order to add them in the database
    private HashMap<Integer, String> attemptedAnswers = new HashMap<>();
    private Boolean isExamEnd = false;
    private long questionStartTime;
    private long mQuizStartTime, mQuizEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        quizScore = 0;

        toolbar = findViewById(R.id.ToolbarOfQuestionDetailActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        quizId = bundle.getString("docID");
        quizTime = Long.parseLong((String) Objects.requireNonNull(bundle.get("quizTime")));

        question = findViewById(R.id.question);
        qCount = findViewById(R.id.questionNumber);
        timer = findViewById(R.id.countDown);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        mProgressBar = findViewById(R.id.progressbar);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        firestore = FirebaseFirestore.getInstance();

        getQuestionsList();
        setupWindowAnimation();

    }

    @Override
    public void onPause() {
        if (isApplicationSentToBackground(this)) {
            Toast.makeText(getAppContext(), "Your quiz is submitted as you have pressed the home button.", Toast.LENGTH_LONG).show();
            examEndProcress();
        }
        super.onPause();
    }

    private boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        quitQuiz(false);
    }

    private void quitQuiz(boolean isHomeButtonPressed) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Do you want to exit the test. \nYou may lost your scores after exiting the quiz!");
        dialog.
                setPositiveButton("Stay here", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Exit Test", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        examEndProcress();
                        dialog.dismiss();
                    }
                });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

    private void setupWindowAnimation() {
        Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);

    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    //method to get current time
    public long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis() / 1000;
    }

    private void getQuestionsList() {
//        String docId = getIntent().getStringExtra("docID");
        mProgressBar.setVisibility(View.VISIBLE);
        questionList = new ArrayList<>();
        firestore.collection(Constant.QUESTION_COLLECTION)
                .whereEqualTo(Constant.QuestionCollectionFields.QUIZ_ID, quizId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot questions = task.getResult();
                assert questions != null;
                for (QueryDocumentSnapshot doc : questions) {
                    questionList.add(new Question(doc.getId(),
                            doc.getString(Constant.QuestionCollectionFields.QUESTION),
                            doc.getString(Constant.QuestionCollectionFields.OPTION_1),
                            doc.getString(Constant.QuestionCollectionFields.OPTION_2),
                            doc.getString(Constant.QuestionCollectionFields.OPTION_3),
                            doc.getString(Constant.QuestionCollectionFields.OPTION_4),
                            doc.getLong(Constant.QuestionCollectionFields.CORRECT_OPTION),
                            doc.getId()
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
        });

    }

    private void setQuestion() {
        if (questionList.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getAppContext(), "Question list is empty in this quiz.", Toast.LENGTH_LONG).show();
            QuestionDetailActivity.this.finish();
        } else {
            timer.setText("Time: " + (quizTime));
            question.setText(questionList.get(0).getQuestion());
            option1.setText(questionList.get(0).getOptionA());
            option2.setText(questionList.get(0).getOptionB());
            option3.setText(questionList.get(0).getOptionC());
            option4.setText(questionList.get(0).getOptionD());
            qCount.setText((1) + "/" + (questionList.size()));
            startTimer();
            mQuizStartTime = System.currentTimeMillis();
            questionStartTime = getCurrentTime();
            questionNumber = 0;
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void startTimer() {
        // int ans = 0;
//        long quizTime = getIntent().getLongExtra("quizTime", 2000);
        final long[] mTimeleftinmillis = {quizTime * 60000};
        CountDownTimer countDownperquiz = new CountDownTimer(mTimeleftinmillis[0], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeleftinmillis[0] = millisUntilFinished;
                int minutes = (int) (mTimeleftinmillis[0] / 1000) / 60;
                int seconds = (int) (mTimeleftinmillis[0] / 1000) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                timer.setText("Time : " + timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                //to check if exam has end
                if (!isExamEnd) {
                    Intent intent = new Intent(QuestionDetailActivity.this, ScoreActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("SCORE", (quizScore) + "/" + (questionList.size()));
                    startActivity(intent);
                    QuestionDetailActivity.this.finish();
                    isExamEnd = true;
                    // changeQuestion();
                }
            }
        };
        countDownperquiz.start();
    }

    //variable to iterate over array TimeTaken ,AttemptedAnswer
    int selectedOption = 0;

    @Override
    public void onClick(View v) {
        if (v.getId() == (R.id.next)) {
            long questionEndTime = getCurrentTime();
            if (timeTalken.containsKey(questionNumber))
                timeTalken.put(questionNumber, timeTalken.get(questionNumber) + questionEndTime - questionStartTime);
            else
                timeTalken.put(questionNumber, questionEndTime
                        - questionStartTime);
//            Toast.makeText(this, "Time Taken: " + timeTalken.get(questionNumber), Toast.LENGTH_SHORT).show();


            if (selectedOption == 1)
                attemptedAnswers.put(questionNumber, questionList.get(questionNumber).getOptionA());
            else if (selectedOption == 2)
                attemptedAnswers.put(questionNumber, questionList.get(questionNumber).getOptionB());
            else if (selectedOption == 3)
                attemptedAnswers.put(questionNumber, questionList.get(questionNumber).getOptionC());
            else if (selectedOption == 4)
                attemptedAnswers.put(questionNumber, questionList.get(questionNumber).getOptionD());

            selectedOption = 0;
            nextQuestion();
        } else if (v.getId() == R.id.option1) {
            selectedOption = 1;
            option1.setBackgroundColor(Color.GREEN);
            option2.setBackgroundResource(R.drawable.rectangularviiew);
            option3.setBackgroundResource(R.drawable.rectangularviiew);
            option4.setBackgroundResource(R.drawable.rectangularviiew);
        } else if (v.getId() == R.id.option2) {
            selectedOption = 2;
            option2.setBackgroundColor(Color.GREEN);
            option1.setBackgroundResource(R.drawable.rectangularviiew);
            option3.setBackgroundResource(R.drawable.rectangularviiew);
            option4.setBackgroundResource(R.drawable.rectangularviiew);
        } else if (v.getId() == R.id.option3) {
            selectedOption = 3;
            option3.setBackgroundColor(Color.GREEN);
            option1.setBackgroundResource(R.drawable.rectangularviiew);
            option2.setBackgroundResource(R.drawable.rectangularviiew);
            option4.setBackgroundResource(R.drawable.rectangularviiew);
        } else if (v.getId() == R.id.option4) {
            selectedOption = 4;
            option4.setBackgroundColor(Color.GREEN);
            option1.setBackgroundResource(R.drawable.rectangularviiew);
            option3.setBackgroundResource(R.drawable.rectangularviiew);
            option2.setBackgroundResource(R.drawable.rectangularviiew);
        } else if (v.getId() == R.id.previous) {
            questionStartTime = getCurrentTime();
            previousQuestion();
        }
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
            String text = "Time: " + " ";
            timer.setText(text);
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

            if (attemptedAnswers.get(questionNumber).equals(questionList.get(questionNumber).getOption1())) {
                option1.setBackgroundColor(Color.GREEN);
            }
            if (attemptedAnswers.get(questionNumber).equals(questionList.get(questionNumber).getOption2())) {
                option2.setBackgroundColor(Color.GREEN);
            }
            if (attemptedAnswers.get(questionNumber).equals(questionList.get(questionNumber).getOption3())) {
                option3.setBackgroundColor(Color.GREEN);
            }
            if (attemptedAnswers.get(questionNumber).equals(questionList.get(questionNumber).getOption4())) {
                option4.setBackgroundColor(Color.GREEN);
            }
        }
    }

    private void nextQuestion() {

        questionStartTime = getCurrentTime();
        if (questionNumber < questionList.size() - 1) {
            questionNumber++;
            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText((questionNumber + 1) + "/" + (questionList.size()));
            String text = "Time: " + " ";
            timer.setText(text);
            //  startTimer();
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);
        } else {
            examEndProcress();
        }
    }

    /**
     * This function is used to calculate the result and the further process of exam end.
     */
    private void examEndProcress() {
        mProgressBar.setVisibility(View.VISIBLE);
        isExamEnd = true;
        calculateScore();
        mQuizEndTime = System.currentTimeMillis();
        //adding data to the FireStore quizTaken collection
        String FirstName = UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_FIRST_NAME);
        String LastName = UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_LAST_NAME);
        String UserName = FirstName + " " + LastName;
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.QuizTakenCollectionFields.QUIZ_ID, quizId);
        data.put(Constant.QuizTakenCollectionFields.SCORE, quizScore);
        data.put(Constant.QuizTakenCollectionFields.TOTAL_SCORE, questionList.size());
        data.put(Constant.QuizTakenCollectionFields.USER_ID, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID));
        data.put(Constant.QuizTakenCollectionFields.USER_NAME, UserName);
        data.put(Constant.QuizTakenCollectionFields.QUIZ_START_TIME, mQuizStartTime);
        data.put(Constant.QuizTakenCollectionFields.QUIZ_END_TIME, mQuizEndTime);
        firestore.collection(Constant.QUIZ_TAKEN_COLLECTION)
                .add(data)
                .addOnSuccessListener(documentReference1 -> {

                    //adding data to the quizTakenQuestions collection
                    for (int questionnumber = 0; questionnumber < questionList.size(); questionnumber++) {
                        Map<String, Object> data1 = new HashMap<>();
                        data1.put(Constant.QuizTakenQuestionsFields.ATTEMPTED_ANS, attemptedAnswers.get(questionnumber));
                        data1.put(Constant.QuizTakenQuestionsFields.QUESTION_ID, questionList.get(questionnumber).getId());
                        data1.put(Constant.QuizTakenQuestionsFields.QUIZ_TAKEN_ID, documentReference1.getId());
                        data1.put(Constant.QuizTakenQuestionsFields.TIME_TAKEN, timeTalken.get(questionnumber));
                        data1.put(Constant.QuizTakenQuestionsFields.QUESTION, questionList.get(questionnumber).getQuestion());
                        data1.put(Constant.QuizTakenQuestionsFields.OPTION_1, questionList.get(questionnumber).getOption1());
                        data1.put(Constant.QuizTakenQuestionsFields.OPTION_2, questionList.get(questionnumber).getOption2());
                        data1.put(Constant.QuizTakenQuestionsFields.OPTION_3, questionList.get(questionnumber).getOption3());
                        data1.put(Constant.QuizTakenQuestionsFields.OPTION_4, questionList.get(questionnumber).getOption4());
                        data1.put(Constant.QuizTakenQuestionsFields.CORRECT_OPTION, questionList.get(questionnumber).getCorrectOption());

                        firestore.collection(Constant.QUIZ_TAKEN_QUESTION_COLLECTION)
                                .add(data1)
                                .addOnSuccessListener(documentReference11 -> Log.d("QuestionActivity", "DocumentSnapshot written with ID: " + documentReference11.getId()))
                                .addOnFailureListener(e -> Log.w("QuestionActivity", "Error adding document", e));
                    }
                })
                .addOnFailureListener(e -> Log.w("QuestionActivity", "Error adding document", e));

        mProgressBar.setVisibility(View.GONE);
        // Go to Score Activity
        Intent intent = new Intent(QuestionDetailActivity.this, ScoreActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("SCORE", (quizScore) + "/" + (questionList.size()));
        startActivity(intent);
        QuestionDetailActivity.this.finish();
    }

    private void calculateScore() {
        for (int i = 0; i < questionList.size(); i++) {
            long correctAns = questionList.get(i).getCorrectOption();
            String correctAnswer = "";

            if (correctAns == 1) {
                correctAnswer = questionList.get(i).getOptionA();
            } else if (correctAns == 2) {
                correctAnswer = questionList.get(i).getOptionB();
            } else if (correctAns == 3) {
                correctAnswer = questionList.get(i).getOptionC();
            } else if (correctAns == 4) {
                correctAnswer = questionList.get(i).getOptionD();
            }

            if (attemptedAnswers.get(i) != null && correctAnswer.equals(attemptedAnswers.get(i))) {
                quizScore++;
            }
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
                                view.setBackgroundResource(R.drawable.rectangularviiew);
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