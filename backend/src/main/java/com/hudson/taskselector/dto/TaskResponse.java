package com.hudson.taskselector.dto;

import com.hudson.taskselector.model.TaskStatus;

import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private String title;
    private int priority;
    private String category;
    private TaskStatus status;
    private String claimedBy;
    private LocalDateTime claimedAt;

    public TaskResponse() {
    }

    public TaskResponse(
            Long id,
            String title,
            int priority,
            String category,
            TaskStatus status,
            String claimedBy,
            LocalDateTime claimedAt
    ) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.status = status;
        this.claimedBy = claimedBy;
        this.claimedAt = claimedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }
}