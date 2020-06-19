package com.alphaCoaching.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import static android.content.ContentValues.TAG;

public class SplashScreenActivity extends Activity {
    private FirebaseAuth fireAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onCreate: ");
        new Handler().postDelayed(() -> {
            if (fireAuth.getCurrentUser() != null) {
                openMainActivity();
            } else {
                openLoginActivity();
            }
        }, 3000);

    }

    private void openLoginActivity() {
        Intent mainActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    /**
     * To Open the main activity.
     */
    private void openMainActivity() {
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

//    public static class CatGridAdapter {
//    }
}