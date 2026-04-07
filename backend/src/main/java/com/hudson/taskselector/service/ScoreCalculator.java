package com.hudson.taskselector.service;

import com.hudson.taskselector.model.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScoreCalculator {

    private final int priorityWeight;
    private final int incompleteBonus;

    public ScoreCalculator(
            @Value("${task.scoring.priority-weight}") int priorityWeight,
            @Value("${task.scoring.incomplete-bonus}") int incompleteBonus) {
        this.priorityWeight = priorityWeight;
        this.incompleteBonus = incompleteBonus;
    }

    public int calculateScore(Task task) {
        int score = 0;

        score += task.getPriority() * priorityWeight;

        if (!task.isCompleted()) {
            score += incompleteBonus;
        }

        return score;
    }

    public String explainScore(Task task) {
        StringBuilder reason = new StringBuilder();

        reason.append("Priority ")
                .append(task.getPriority())
                .append(" × weight ")
                .append(priorityWeight);

        if (!task.isCompleted()) {
            reason.append(", plus incomplete bonus ")
                .append(incompleteBonus);
        }

        return reason.toString();
    }
}