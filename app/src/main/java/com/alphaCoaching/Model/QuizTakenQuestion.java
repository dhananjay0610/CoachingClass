package com.alphaCoaching.Model;

public class QuizTakenQuestion {

    private String id;
    private String attemptedAnswer;
    private String questionId;
    private String quizTakenId;
    private Long timeTaken;

    public QuizTakenQuestion() {

    }

    public String getId() {
        return id;
    }

    public String getAttemptedAnswer() {
        return attemptedAnswer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public String getQuizTakenId() {
        return quizTakenId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAttemptedAnswer(String attemptedAnswer) {
        this.attemptedAnswer = attemptedAnswer;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setQuizTakenId(String quizTakenId) {
        this.quizTakenId = quizTakenId;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }
}
