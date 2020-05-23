package com.alphaCoaching.Model;

import com.google.firebase.firestore.DocumentSnapshot;

public class QuestionModel {
    private String quizName;
    private int questionNumber;
    private int quizTime;
    private String documentId;

    public QuestionModel() {
    }

    private QuestionModel(String quizName, int questionNumber, int quizTime) {
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String doc) {
        this.documentId = doc;

    }
}
