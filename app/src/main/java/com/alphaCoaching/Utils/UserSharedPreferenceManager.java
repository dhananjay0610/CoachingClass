package com.alphaCoaching.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserSharedPreferenceManager {
    private static final String QUIZ_TAKEN_STATUS = "quizTakenStatus";

    public static void storeQuizTakenStatus(Context context, boolean status, String quizId) {
        if (context != null && quizId != null ) {
            SharedPreferences sharedPreferences = getSharedPreferences(context, QUIZ_TAKEN_STATUS);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(quizId);
            editor.putBoolean(quizId, status);
            editor.apply();
        }
    }

    public static boolean getQuizTakenStatus (Context context, String quizId) {
        if (context != null && quizId != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context, QUIZ_TAKEN_STATUS);
            return sharedPreferences.getBoolean(quizId, false);
        }
        return false;
    }

    private static SharedPreferences getSharedPreferences(Context context, String sharedPreferenceName) {
        return context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
    }
}
