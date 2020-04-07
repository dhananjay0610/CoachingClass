package com.alphaCoaching.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.alphaCoaching.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.spark.submitbutton.SubmitButton;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rellayLayout1, rellayLayout2;
    EditText email, password;
    FirebaseAuth fireAuth;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellayLayout1.setVisibility(View.VISIBLE);
            rellayLayout2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rellayLayout1 = (RelativeLayout) findViewById(R.id.rellayLay1);
        rellayLayout2 = (RelativeLayout) findViewById(R.id.rellayLay2);
        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pass);

        handler.postDelayed(runnable, 2000);

        fireAuth = FirebaseAuth.getInstance();

        fireAuth = FirebaseAuth.getInstance();
        findViewById(R.id.Sign_In).setOnClickListener(this);
    }

    private void userLogin() {
        final String sEmail = email.getText().toString().trim();
        final String sPassword = password.getText().toString().trim();
        if (sEmail.isEmpty()) {
            email.setError("Email Required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Enter a Valid Email");
            email.requestFocus();
            return;
        }
        if (sPassword.isEmpty()) {
            password.setError("Password Required");
            password.requestFocus();
            return;
        }
        if (sPassword.length() < 6) {
            password.setError("password should be at least 6 character");
            password.requestFocus();
            return;
        }


        fireAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent1);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3700);
                                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent1);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Sign_In:
                userLogin();
        }
    }
}



