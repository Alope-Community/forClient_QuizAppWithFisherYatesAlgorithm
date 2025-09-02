package com.example.quizwithfisheryates._models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Score {
    private String name;

    private String nisn;
    private String difficulty;
    private String createdAt;
    private int score;

    public Score(String name, String nisn, String difficulty, String createdAt, int score) {
        this.name = name;
        this.nisn = nisn;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getNisn() {
        return nisn;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getScore() {
        return score;
    }
    public String getCreatedAt() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = inputFormat.parse(createdAt);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return createdAt;
        }
    }
}
