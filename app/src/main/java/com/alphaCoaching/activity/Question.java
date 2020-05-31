package com.alphaCoaching.activity;

public class Question {
    private String id;
    private String question;
    private String optionA, optionB, optionC, optionD;
    private long correctOption;
    private String QuestionId;

    public Question(String id, String question, String optionA, String optionB, String optionC, String optionD, long correctOption, String questionId) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.QuestionId = questionId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(String question) {
        this.QuestionId = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public long getCorrectOption() {
        return correctOption;
    }

    public void setCorrectAns(int correctAns) {
        this.correctOption = correctAns;
    }
}
