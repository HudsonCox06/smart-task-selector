package com.hudson.taskselector.service;

import com.hudson.taskselector.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task addTask(Task task) {
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

        Task bestTask = candidates.get(0);

        for (Task task : candidates) {
            if (task.getPriority() > bestTask.getPriority()) {
                bestTask = task;
            }
        }

        return bestTask;
    }
}