package com.hudson.taskselector.service;

import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.model.TaskStatus;
import com.hudson.taskselector.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hudson.taskselector.dto.SelectionResult;
import com.hudson.taskselector.exception.NoTaskSelectedException;

import java.util.List;
import java.util.ArrayList;

@Service
public class TaskClaimService {

    private final TaskRepository taskRepository;
    private final ScoreCalculator scoreCalculator;

    public TaskClaimService(TaskRepository taskRepository, 
                            ScoreCalculator scoreCalculator) {
        this.taskRepository = taskRepository;
        this.scoreCalculator = scoreCalculator;
    }

    @Transactional
    public SelectionResult attemptClaimBestTask(
            String category,
            Integer minPriority,
            String userId,
            Integer priorityWeightOverride,
            Integer incompleteBonusOverride) {

        List<Task> candidates = new ArrayList<>();

        if (category != null && !category.isBlank()) {
            if (minPriority != null) {
                candidates = taskRepository
                        .findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
                                TaskStatus.OPEN,
                                category,
                                minPriority
                        );
            } else {
                candidates = taskRepository
                        .findByStatusAndCategoryIgnoreCase(
                                TaskStatus.OPEN,
                                category
                        );
            }
        } else {
            candidates = taskRepository.findByStatus(TaskStatus.OPEN);
        }   


        if (candidates.isEmpty()) {
            throw new NoTaskSelectedException();
        }

        int priorityWeightUsed = scoreCalculator.getEffectivePriorityWeight(priorityWeightOverride);
        int incompleteBonusUsed = scoreCalculator.getEffectiveIncompleteBonus(incompleteBonusOverride);

        Task bestTask = null;
        int bestScore = Integer.MIN_VALUE;

        for (Task task : candidates) {
            int score = scoreCalculator.calculateScore(
                    task,
                    priorityWeightOverride,
                    incompleteBonusOverride
            );

            if (bestTask == null || score > bestScore) {
                bestTask = task;
                bestScore = score;
            }
        }
    

        String reason = scoreCalculator.explainScore(
                bestTask,
                priorityWeightOverride,
                incompleteBonusOverride
        );

        SelectionResult selection = new SelectionResult(
            bestTask,
            bestScore,
            reason,
            priorityWeightUsed,
            incompleteBonusUsed
        );

        Task task = selection.getTask();
        task.claim(userId);
        taskRepository.save(task);

        return new SelectionResult(
                task,
                selection.getScore(),
                selection.getReason(),
                selection.getPriorityWeightUsed(),
                selection.getIncompleteBonusUsed()
        );
    }
}