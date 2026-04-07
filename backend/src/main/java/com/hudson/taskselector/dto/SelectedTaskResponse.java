package com.hudson.taskselector.dto;

public class SelectedTaskResponse {

    private TaskResponse task;
    private int score;
    private String reason;

    public SelectedTaskResponse() {
    }

    public SelectedTaskResponse(TaskResponse task, int score, String reason) {
        this.task = task;
        this.score = score;
        this.reason = reason;
    }

    public TaskResponse getTask() {
        return task;
    }

    public void setTask(TaskResponse task) {
        this.task = task;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}