package com.example.c045_khushishah_todolist;

public class Task {
    private String id;
    private String title, description;
    private int priority; // 1=High, 2=Medium, 3=Low

    public Task() { } // required for Firebase

    public Task(String id, String title, String description, int priority) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.priority = priority;
    }
    // getters and setters
    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }

    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }

    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }

    public int getPriority(){ return priority; }
    public void setPriority(int priority){ this.priority = priority; }
}
