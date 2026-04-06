package com.hudson.taskselector.controller;

import com.hudson.taskselector.dto.CreateTaskRequest;
import com.hudson.taskselector.dto.SelectTaskRequest;
import com.hudson.taskselector.dto.TaskResponse;
import com.hudson.taskselector.mapper.TaskMapper;
import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.hudson.taskselector.dto.UpdateTaskRequest;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    public List<TaskResponse> getTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = taskMapper.toEntity(request);
        Task savedTask = taskService.addTask(task);
        return taskMapper.toResponse(savedTask);
    }

    @PostMapping("/select-task")
    public TaskResponse selectTask(@RequestBody SelectTaskRequest request) {
        Task selectedTask = taskService.selectTask(
                request.getCategory(),
                request.getMinPriority(),
                request.getRandom(),
                request.getIncludeCompleted()
        );

        return taskMapper.toResponse(selectedTask);
    }

    @PutMapping("/{id}/complete")
    public Task completeTask(@PathVariable Long id) {
        return taskService.completeTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        Task updatedTask = taskService.updateTaskById(id, request);
        return taskMapper.toResponse(updatedTask);
    }
}