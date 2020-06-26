package com.alphaCoaching.Model;


import com.google.firebase.Timestamp;

public class Note {
    private String id;
    private String quizName;
    private int questionNumber;
    private int quizTime;
    private Timestamp quizDate;

    public Note() {

    }

    private Note(String quizName, int questionNumber, int quizTime) {
        this.quizName = quizName;
        this.questionNumber = questionNumber;
        this.quizTime = quizTime;
    }

    public Timestamp getQuizDate() {
        return quizDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getQuizName() {
        return quizName;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public int getQuizTime() {
        return quizTime;
    }
}
