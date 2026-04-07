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
import com.hudson.taskselector.dto.SelectedTaskResponse;
import com.hudson.taskselector.service.ScoreCalculator;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final ScoreCalculator scoreCalculator;

    public TaskController(TaskService taskService, TaskMapper taskMapper, ScoreCalculator scoreCalculator) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.scoreCalculator = scoreCalculator;
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
    public SelectedTaskResponse selectTask(@RequestBody SelectTaskRequest request) {
        Task selectedTask = taskService.selectTask(
            request.getCategory(),
            request.getMinPriority(),
            request.getRandom(),
            request.getIncludeCompleted(),
            request.getPriorityWeight(),
            request.getIncompleteBonus()
        );

        int priorityWeightUsed = scoreCalculator.getEffectivePriorityWeight(request.getPriorityWeight());
        int incompleteBonusUsed = scoreCalculator.getEffectiveIncompleteBonus(request.getIncompleteBonus());

        int score = scoreCalculator.calculateScore(
                selectedTask,
                request.getPriorityWeight(),
                request.getIncompleteBonus()
        );

        String reason = scoreCalculator.explainScore(
                selectedTask,
                request.getPriorityWeight(),
                request.getIncompleteBonus()
        );

        return taskMapper.toSelectedTaskResponse(
                selectedTask,
                score,
                reason,
                priorityWeightUsed,
                incompleteBonusUsed
        );
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