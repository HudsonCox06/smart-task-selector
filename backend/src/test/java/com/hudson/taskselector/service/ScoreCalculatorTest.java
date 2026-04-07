package com.hudson.taskselector.service;

import com.hudson.taskselector.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreCalculatorTest {

    @Test
    void calculateScore_returnsPriorityTimesWeightPlusIncompleteBonus() {
        ScoreCalculator calculator = new ScoreCalculator(10, 5);

        Task task = new Task(1L, "Test Task", 4, "school", false);

        int score = calculator.calculateScore(task);

        assertEquals(45, score);
    }

    @Test
    void calculateScore_doesNotAddIncompleteBonusForCompletedTask() {
        ScoreCalculator calculator = new ScoreCalculator(10, 5);

        Task task = new Task(1L, "Completed Task", 4, "school", true);

        int score = calculator.calculateScore(task);

        assertEquals(40, score);
    }
}