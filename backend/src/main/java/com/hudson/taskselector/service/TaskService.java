package com.hudson.taskselector.service;

import com.hudson.taskselector.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();
    private Long nextID = 1L;

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task addTask(Task task) {
        task.setId(nextID++);
        tasks.add(task);
        return task;
    }

    public Task selectTask(String category, Integer minPriority, Boolean random, Boolean includeCompleted) {
        List<Task> candidates = new ArrayList<>();

        for (Task task : tasks) {
            if ((includeCompleted == null || !includeCompleted) && task.isCompleted()) {
                continue;
            }

            if (category != null && !category.isBlank() && !task.getCategory().equalsIgnoreCase(category)) {
                continue;
            }

            if (minPriority != null && task.getPriority() < minPriority) {
            continue;
            }

            candidates.add(task);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        if (random != null && random) {
            int index = (int) (Math.random() * candidates.size());
            return candidates.get(index);
        }

        Task bestTask = null;
        int bestScore = Integer.MIN_VALUE;

        for (Task task : candidates) {
            int score = 0;

            // Base score: priority
            score += task.getPriority() * 10;

            // Slight bonus for incomplete tasks
            if(!task.isCompleted()) {
                score += 5;
            }

            if (bestTask == null || score > bestScore) {
                bestTask = task;
                bestScore = score;
            }
        }

        return bestTask;
    }

    public Task updateTaskById(Long id, Task updatedTask) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                task.setTitle(updatedTask.getTitle());
                task.setPriority(updatedTask.getPriority());
                task.setCategory(updatedTask.getCategory());
                task.setCompleted(updatedTask.isCompleted());
                return task;
            }
        }
        return null;
    }

    public Task completeTaskById(Long id) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                task.setCompleted(true);
                return task;
            }
        }
        return null;
    }
}