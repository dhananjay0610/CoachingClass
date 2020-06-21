package com.alphaCoaching.Model;

public class QuizTakenQuestion {

    private String id;
    private String attemptedAnswer;
    private String questionId;
    private String quizTakenId;
    private Long timeTaken;
    private String question;
    private String option1, option2, option3, option4;
    private long correctOption;

    public QuizTakenQuestion() {

    }

    public String getQuestion() {
        return question;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public long getCorrectOption() {
        return correctOption;
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
