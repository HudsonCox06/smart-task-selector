package com.hudson.taskselector.controller;

import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.service.TaskService;
import org.springframework.web.bind.annotation.*;
import com.hudson.taskselector.dto.SelectTaskRequest;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.addTask(task);
    }

    @PostMapping("/select-task")
    public Task selectTask(@RequestBody SelectTaskRequest request) {
        return taskService.selectTask(
            request.getCategory(),
            request.getMinPriority(),
            request.getRandom(),
            request.getIncludeCompleted()
        );
    }
}
