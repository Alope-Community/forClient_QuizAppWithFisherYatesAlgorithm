package com.example.quizwithfisheryates._models;

public class Score {
    private String name;
    private String difficulty;
    private int score;

    public Score(String name, String difficulty, int score) {
        this.name = name;
        this.difficulty = difficulty;
        this.score = score;
    }

    public String getName() { return name; }
    public String getDifficulty() { return difficulty; }
    public int getScore() { return score; }
}
