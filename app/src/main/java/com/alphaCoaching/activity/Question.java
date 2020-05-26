package com.alphaCoaching.activity;

public class Question {
    private String question;
    private String optionA, optionB, optionC, optionD;
    private int correctOption;
    private String QuestionId;

    public Question(String question, String optionA, String optionB, String optionC, String optionD, int correctOption, String questionId) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.QuestionId = questionId;
    }

    String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    String getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(String question) {
        this.QuestionId = question;
    }

    String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectAns(int correctAns) {
        this.correctOption = correctAns;
    }
}
