package com.hudson.taskselector.dto;

import com.hudson.taskselector.model.Task;

public class SelectionResult {

    private final Task task;
    private final int score;
    private final String reason;
    private final int priorityWeightUsed;
    private final int incompleteBonusUsed;

    public SelectionResult(
            Task task,
            int score,
            String reason,
            int priorityWeightUsed,
            int incompleteBonusUsed
    ) {
        this.task = task;
        this.score = score;
        this.reason = reason;
        this.priorityWeightUsed = priorityWeightUsed;
        this.incompleteBonusUsed = incompleteBonusUsed;
    }

    public Task getTask() {
        return task;
    }

    public int getScore() {
        return score;
    }

    public String getReason() {
        return reason;
    }

    public int getPriorityWeightUsed() {
        return priorityWeightUsed;
    }

    public int getIncompleteBonusUsed() {
        return incompleteBonusUsed;
    }
}