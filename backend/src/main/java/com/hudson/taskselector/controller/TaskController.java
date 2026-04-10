package com.hudson.taskselector.controller;

import com.hudson.taskselector.dto.CreateTaskRequest;
import com.hudson.taskselector.dto.SelectTaskRequest;
import com.hudson.taskselector.dto.ClaimTaskRequest;
import com.hudson.taskselector.dto.SelectedTaskResponse;
import com.hudson.taskselector.dto.ClaimBestTaskRequest;
import com.hudson.taskselector.dto.TaskResponse;
import com.hudson.taskselector.dto.UpdateTaskRequest;
import com.hudson.taskselector.mapper.TaskMapper;
import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.service.TaskService;
import com.hudson.taskselector.dto.SelectionResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    public SelectedTaskResponse selectTask(@Valid @RequestBody SelectTaskRequest request) {
        SelectionResult result = taskService.selectTask(
                request.getCategory(),
                request.getMinPriority(),
                request.getPriorityWeight(),
                request.getIncompleteBonus()
        );

        return taskMapper.toSelectedTaskResponse(
                result.getTask(),
                result.getScore(),
                result.getReason(),
                result.getPriorityWeightUsed(),
                result.getIncompleteBonusUsed()
        );
    }

    @PostMapping("/claim-best")
    public SelectedTaskResponse claimBestTask(@Valid @RequestBody ClaimBestTaskRequest request) {
        SelectionResult result = taskService.claimBestTask(
                request.getCategory(),
                request.getMinPriority(),
                request.getUserId(),
                request.getPriorityWeight(),
                request.getIncompleteBonus()
        );

        return taskMapper.toSelectedTaskResponse(
                result.getTask(),
                result.getScore(),
                result.getReason(),
                result.getPriorityWeightUsed(),
                result.getIncompleteBonusUsed()
        );
    }


    @PutMapping("/{id}/complete")
    public TaskResponse completeTask(@PathVariable Long id) {
        Task completedTask = taskService.completeTaskById(id);
        return taskMapper.toResponse(completedTask);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        Task updatedTask = taskService.updateTaskById(id, request);
        return taskMapper.toResponse(updatedTask);
    }

    @PutMapping("/{id}/claim")
    public TaskResponse claimTask(@PathVariable Long id, @Valid @RequestBody ClaimTaskRequest request) {
        Task claimedTask = taskService.claimTaskById(id, request.getUserId());
        return taskMapper.toResponse(claimedTask);
    }
}