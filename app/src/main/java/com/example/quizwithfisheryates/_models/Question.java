package com.example.quizwithfisheryates._models;

import java.util.List;

public class Question {
    private int id;
    private String question;
    private String image;
    private String difficulty;
    private String answer;

    //
    int optionId;
    List<String> value;

    public Question(int id, String question, String image, String difficulty, String answer) {
        this.id = id;
        this.question = question;
        this.image = image;
        this.difficulty = difficulty;
        this.answer = answer;
    }

    public Question(int questionId, String question, String difficulty, String image,
                    String answer, int optionId, List<String> value) {
        this.id = questionId;
        this.question = question;
        this.difficulty = difficulty;
        this.image = image;
        this.answer = answer;
        this.optionId = optionId;
        this.value = value;
    }

    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getImage() { return image; }
    public String getDifficulty() { return difficulty; }
    public String getAnswer() { return answer; }
    public List<String> getValue() {
        return value;
    }
}
