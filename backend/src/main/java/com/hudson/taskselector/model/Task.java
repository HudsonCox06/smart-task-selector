package com.hudson.taskselector.model;

public class Task {

    private Long id;
    private String title;
    private int priority;
    private String category;
    private boolean completed;

    public Task() {
    }

    public Task(Long id, String title, int priority, String category, boolean completed) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}