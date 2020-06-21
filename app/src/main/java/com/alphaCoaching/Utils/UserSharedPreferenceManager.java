package com.alphaCoaching.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alphaCoaching.Constant.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.MODE_PRIVATE;


public class UserSharedPreferenceManager {
    private static final String QUIZ_TAKEN_STATUS = "quizTakenStatus";
    public static final String USER_DETAIL = "UserDetail";
    public static final String USER_SUBJECTS = "UserSubjects";

    public static interface userInfoFields {
        public static final String USER_UUID =  "userUuid";
        public static final String USER_FIRST_NAME =  "UserFirstName";
        public static final String USER_LAST_NAME =  "UserLastName";
        public static final String USER_STANDARD =  "UserStandard";
        public static final String USER_DOB =  "dateOfBirth";
        public static final String USER_EMAIL =  "UserEmail";
    }

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
        return context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE);
    }

    public static void storeUserDetail(Context context,String id, String userFirstName, String userLastName, String standard, String dateOfBirth, String email) {
        if (context != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context, USER_DETAIL);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(userInfoFields.USER_UUID, id);
            editor.putString(userInfoFields.USER_FIRST_NAME, userFirstName);
            editor.putString(userInfoFields.USER_LAST_NAME, userLastName);
            editor.putString(userInfoFields.USER_STANDARD, standard);
            editor.putString(userInfoFields.USER_DOB, dateOfBirth);
            editor.putString(userInfoFields.USER_EMAIL, email);
            editor.apply();
        }
    }

    public static void storeUserField(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context, USER_DETAIL);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public static void removeUserData(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences( context, USER_DETAIL);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static String getUserInfo(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context, USER_DETAIL);
        return sharedPreferences.getString(key, null);
    }

    public static void storeUserSubjects(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context, USER_SUBJECTS);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public static String getUserSubject(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context, USER_SUBJECTS);
        return sharedPreferences.getString(key, null);
    }
}