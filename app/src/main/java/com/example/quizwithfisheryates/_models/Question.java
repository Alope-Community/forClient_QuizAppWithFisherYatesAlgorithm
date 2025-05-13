package com.example.quizwithfisheryates._models;

public class Question {
    private int id;
    private String question;
    private String image;
    private String difficulty;

    public Question(int id, String question, String image, String difficulty) {
        this.id = id;
        this.question = question;
        this.image = image;
        this.difficulty = difficulty;
    }

    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getImage() { return image; }
    public String getDifficulty() { return difficulty; }
}
