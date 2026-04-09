package com.hudson.taskselector.mapper;

import com.hudson.taskselector.dto.CreateTaskRequest;
import com.hudson.taskselector.dto.SelectedTaskResponse;
import com.hudson.taskselector.dto.TaskResponse;
import com.hudson.taskselector.dto.UpdateTaskRequest;
import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setPriority(request.getPriority());
        task.setCategory(request.getCategory());
        task.setStatus(TaskStatus.OPEN);
        return task;
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getPriority(),
                task.getCategory(),
                task.getStatus(),
                task.getClaimedBy(),
                task.getClaimedAt()
        );
    }

    public void updateEntity(Task task, UpdateTaskRequest request) {
        task.setTitle(request.getTitle());
        task.setPriority(request.getPriority());
        task.setCategory(request.getCategory());
    }

    public SelectedTaskResponse toSelectedTaskResponse(
            Task task,
            int score,
            String reason,
            int priorityWeightUsed,
            int incompleteBonusUsed) {

        return new SelectedTaskResponse(
                toResponse(task),
                score,
                reason,
                priorityWeightUsed,
                incompleteBonusUsed
        );
    }
}

