package com.alphaCoaching.model;


public class Note {
   private String quizName;
   private int questionNumber;
   private int quizTime;

    public Note(){

    }

    private Note(String quizName, int questionNumber, int quizTime) {
        this.quizName = quizName;
        this.questionNumber = questionNumber;
        this.quizTime = quizTime;
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
