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
import com.spark.submitbutton.SubmitButton;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    RelativeLayout rellay1, rellay2;
    SubmitButton logInBtn;
    EditText emailR, password;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);
        emailR = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pass);
        logInBtn=(SubmitButton) findViewById(R.id.logInBBtn);
        handler.postDelayed(runnable, 2000);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sEmail=emailR.getText().toString().trim();
                final String sPassword=password.getText().toString().trim();
                if(sEmail.isEmpty()){
                    emailR.setError("Email Required");
                    emailR.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    emailR.setError("Enter a Valid Email");
                    emailR.requestFocus();
                    return;
                }
                if (!sEmail.equals("alpha@gmail.com")){
                    emailR.setError("Enter Correct Email");
                    emailR.requestFocus();
                }
                if (sPassword.isEmpty()){
                    password.setError("Password Required");
                    password.requestFocus();
                    return;
                }
                if (!sPassword.equals("Alpha123")){
                    password.setError("Enter Correct Email");
                    password.requestFocus();
                }
                if (sEmail.equals("alpha@gmail.com")&& sPassword.equals("Alpha123")){
                    Thread thread=new Thread(new Runnable() {
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
                    thread.start();

                }else {
                    Toast.makeText(LoginActivity.this, "Enter The Valid Code", Toast.LENGTH_SHORT).show();
                }

               /* if (sPassword.length()<6){
                    password.setError("password should be at least 6 character");
                    password.requestFocus();
                    return;
                }*/

            }
        });
    }
}


