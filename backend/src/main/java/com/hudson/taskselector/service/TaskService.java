package com.hudson.taskselector.service;

import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task addTask(Task task) {
        return taskRepository.save(task);
    }
    

    public Task selectTask(String category, Integer minPriority, Boolean random, Boolean includeCompleted) {
        List<Task> tasks = taskRepository.findAll();
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
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(updatedTask.getTitle());
            task.setPriority(updatedTask.getPriority());
            task.setCategory(updatedTask.getCategory());
            task.setCompleted(updatedTask.isCompleted());
            return taskRepository.save(task);
        }

        return null;
    }
        

    public Task completeTaskById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompleted(true);
            return taskRepository.save(task);
        }

        return null;
    }

}