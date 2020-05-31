package com.alphaCoaching.Model;

public class QuizTaken {
    private String id;
    private String TotalScore;
    private String quizId;
    private String score;
    private String userId;
    private String userName;

    public QuizTaken (String TotalScore, String userId, String score, String quizId, String userName) {
        this.quizId = quizId;
        this.score = score;
        this.TotalScore = TotalScore;
        this.userId = userId;
        this.userName = userName;
    }

    public QuizTaken() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getScore() {
        return score;
    }

    public String getTotalScore() {
        return TotalScore;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
