package com.alphaCoaching.Model;

public class QuizTaken {
    private String id;
    private long TotalScore;
    private String quizId;
    private long score;
    private String userId;
    private String userName;

    public QuizTaken (long TotalScore, String userId, long score, String quizId, String userName) {
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

    public long getScore() {
        return score;
    }

    public long getTotalScore() {
        return TotalScore;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
