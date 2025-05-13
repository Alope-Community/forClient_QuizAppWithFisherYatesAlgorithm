package com.example.quizwithfisheryates._models;

public class Option {
    private int id;
    private int question_id;
    private String value;

    public Option(int id, int question_id, String value) {
        this.id = id;
        this.question_id = question_id;
        this.value = value;
    }

    public int getId() { return id; }
    public int getQuestionId() { return question_id; }
    public String getValue() { return value; }

    @Override
    public String toString() {
        return "Option{id=" + id +
                ", questionId=" + question_id +
                ", value='" + value + "'}";
    }
}
