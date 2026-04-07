package com.hudson.taskselector.mapper;

import com.hudson.taskselector.dto.CreateTaskRequest;
import com.hudson.taskselector.dto.TaskResponse;
import com.hudson.taskselector.model.Task;
import org.springframework.stereotype.Component;
import com.hudson.taskselector.dto.UpdateTaskRequest;
import com.hudson.taskselector.dto.SelectedTaskResponse;

@Component
public class TaskMapper {

    public Task toEntity(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setPriority(request.getPriority());
        task.setCategory(request.getCategory());
        task.setCompleted(false);
        return task;
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.getCategory(),
                task.isCompleted()
        );
    }

    public void updateEntity(Task task, UpdateTaskRequest request) {
        task.setTitle(request.getTitle());
        task.setPriority(request.getPriority());
        task.setCategory(request.getCategory());
        task.setCompleted(request.isCompleted());
    }

    public SelectedTaskResponse toSelectedTaskResponse(Task task, int score, String reason) {
        return new SelectedTaskResponse(
                toResponse(task),
                score,
                reason
        );
    }
}