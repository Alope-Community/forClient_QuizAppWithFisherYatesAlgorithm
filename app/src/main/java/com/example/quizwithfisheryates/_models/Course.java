package com.example.quizwithfisheryates._models;

public class Course {
    private String title;
    private String description;
    private String body;

    private String created_at;
    private int account_id;

    public Course(String title, String description, String body, String created_at, int account_id) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.created_at = created_at;
        this.account_id = account_id;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getBody() { return body; }
    public String getCreatedAt() { return created_at; }
    public int getAccountID() { return account_id; }
}
