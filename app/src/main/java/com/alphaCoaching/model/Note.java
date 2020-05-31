package com.alphaCoaching.Model;


public class Note {
    private String id;
    private String quizName;
    private int questionNumber;
    private int quizTime;

    public Note() {

    }

    private Note(String quizName, int questionNumber, int quizTime) {
        this.quizName = quizName;
        this.questionNumber = questionNumber;
        this.quizTime = quizTime;
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
