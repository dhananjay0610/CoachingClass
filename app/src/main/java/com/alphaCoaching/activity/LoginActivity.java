package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alphaCoaching.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    RelativeLayout mLoginLayout;
    RelativeLayout mForgotPass;
    EditText email;
    EditText password;
    FirebaseAuth fireAuth;
    Handler handler = new Handler();
    CircularProgressButton mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginLayout = (RelativeLayout) findViewById(R.id.loginScreen);
        mForgotPass = (RelativeLayout) findViewById(R.id.forgotPass);
        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pass);
        mLoginBtn = (CircularProgressButton) findViewById(R.id.loginButton);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoginLayout.setVisibility(View.VISIBLE);
                mForgotPass.setVisibility(View.VISIBLE);
            }
        }, 3000);

        fireAuth = FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
//        findViewById(R.id.Sign_In).setOnClickListener(this);
        /*findViewById(R.id.Sign_In).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });*/
    }

    /**
     * Function used to authenticate the user input.
     */
    private void userLogin() {
        mLoginBtn.startMorphAnimation();
        final String sEmail = email.getText().toString().trim();
        final String sPassword = password.getText().toString().trim();
        if (sEmail.isEmpty()) {
            email.setError("Email Required");
            email.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Enter a Valid Email");
            email.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (sPassword.isEmpty()) {
            password.setError("Password Required");
            password.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (sPassword.length() < 6) {
            password.setError("password should be at least 6 character");
            password.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
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
                                mLoginBtn.dispose();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    mLoginBtn.startMorphRevertAnimation();
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



