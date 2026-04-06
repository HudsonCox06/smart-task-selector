package com.hudson.taskselector.service;

import com.hudson.taskselector.exception.TaskNotFoundException;
import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.repository.TaskRepository;
import org.springframework.stereotype.Service;
import com.hudson.taskselector.exception.TaskNotFoundException;
import com.hudson.taskselector.dto.UpdateTaskRequest;
import com.hudson.taskselector.mapper.TaskMapper;
import com.hudson.taskselector.exception.NoTaskSelectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
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
            throw new NoTaskSelectedException();
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

    public Task updateTaskById(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        taskMapper.updateEntity(task, request);
        return taskRepository.save(task);
    }
        

    public Task completeTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setCompleted(true);
        return taskRepository.save(task);
    }

}