package com.alphaCoaching.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserSharedPreferenceManager {
    private static final String QUIZ_TAKEN_STATUS = "quizTakenStatus";
    public static final String USER_DETAIL = "UserDetail";

    public static void storeQuizTakenStatus(Context context, boolean status, String quizId) {
        if (context != null && quizId != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context, QUIZ_TAKEN_STATUS);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(quizId);
            editor.putBoolean(quizId, status);
            editor.apply();
        }
    }

    public static boolean getQuizTakenStatus(Context context, String quizId) {
        if (context != null && quizId != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context, QUIZ_TAKEN_STATUS);
            return sharedPreferences.getBoolean(quizId, false);
        }
        return false;
    }

    private static SharedPreferences getSharedPreferences(Context context, String sharedPreferenceName) {
        return context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
    }

    public static void storeUserDetail(Context context, String userFirstName, String userLastName, String standard, String dateOfBirth, String email) {
        if (context != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context, USER_DETAIL);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("UserFirstName", userFirstName);
            editor.putString("UserLastName", userLastName);
            editor.putString("UserStandard", standard);
            editor.putString("UserDateOfBirth", dateOfBirth);
            editor.putString("UserEmail", email);
            editor.apply();
        }
    }

    public static String getUserStandard(Context context) {
        FirebaseAuth fireAuth;
        fireAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fireAuth.getCurrentUser();
        assert currentUser != null;
        String user_Uuid = currentUser.getUid();
        SharedPreferences sharedPreferences = getSharedPreferences(context, USER_DETAIL);
        return sharedPreferences.getString(user_Uuid, null);
    }
}