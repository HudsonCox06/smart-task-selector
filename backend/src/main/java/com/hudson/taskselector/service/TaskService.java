package com.hudson.taskselector.service;

import com.hudson.taskselector.dto.UpdateTaskRequest;
import com.hudson.taskselector.exception.NoTaskSelectedException;
import com.hudson.taskselector.exception.TaskNotFoundException;
import com.hudson.taskselector.mapper.TaskMapper;
import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.model.TaskStatus;
import com.hudson.taskselector.repository.TaskRepository;
import org.springframework.stereotype.Service;
import com.hudson.taskselector.dto.SelectionResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ScoreCalculator scoreCalculator;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, ScoreCalculator scoreCalculator) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.scoreCalculator = scoreCalculator;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    public SelectionResult selectTask(
            String category,
            Integer minPriority,
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

        return new SelectionResult(
            bestTask,
            bestScore,
            reason,
            priorityWeightUsed,
            incompleteBonusUsed
        );
    }

    public Task updateTaskById(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskMapper.updateEntity(task, request);
        return taskRepository.save(task);
    }

    public Task completeTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.complete();
        return taskRepository.save(task);
    }

    public Task claimTaskById(Long id, String userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.claim(userId);
        return taskRepository.save(task);
    }

    public SelectionResult claimBestTask(
            String category,
            Integer minPriority,
            String userId,
            Integer priorityWeightOverride,
            Integer incompleteBonusOverride
    ) {
        for (int attempt = 0; attempt < 2; attempt++) {

            SelectionResult selection = selectTask(
                    category,
                    minPriority,
                    priorityWeightOverride,
                    incompleteBonusOverride
            );

            Task task = selection.getTask();

            try {
                task.claim(userId);
                taskRepository.save(task);

                return new SelectionResult(
                        task,
                        selection.getScore(),
                        selection.getReason(),
                        selection.getPriorityWeightUsed(),
                        selection.getIncompleteBonusUsed()
                );

            } catch (org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
                // retry
            }
        }

        throw new IllegalStateException("Failed to claim task due to concurrent updates.");
    }
}
