package com.hudson.taskselector.dto;

public class SelectedTaskResponse {

    private TaskResponse task;
    private int score;
    private String reason;
    private int priorityWeightUsed;
    private int incompleteBonusUsed;

    public SelectedTaskResponse() {
    }

    public SelectedTaskResponse(TaskResponse task, int score, String reason, int priorityWeightUsed, int incompleteBonusUsed) {
        this.task = task;
        this.score = score;
        this.reason = reason;
        this.priorityWeightUsed = priorityWeightUsed;
        this.incompleteBonusUsed = incompleteBonusUsed;
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

    public int getPriorityWeightUsed() {
        return priorityWeightUsed;
    }

    public void setPriorityWeightUsed(int priorityWeightUsed) {
        this.priorityWeightUsed = priorityWeightUsed;
    }

    public int getIncompleteBonusUsed() {
        return incompleteBonusUsed;
    }

    public void setIncompleteBonusUsed(int incompleteBonusUsed) {
        this.incompleteBonusUsed = incompleteBonusUsed;
    }
}