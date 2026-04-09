package com.hudson.taskselector.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import java.time.LocalDateTime;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int priority;
    private String category;
    private String claimedBy;
    private LocalDateTime claimedAt;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.OPEN;

    public Task() {
    }

    public Task(Long id, String title, int priority, String category, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.status = status;
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

    public boolean isOpen() {
        return status == TaskStatus.OPEN;
    }

    public boolean isClaimed() {
        return status == TaskStatus.CLAIMED;
    }

    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public void claim(String userId) {
        if (status != TaskStatus.OPEN) {
            throw new IllegalStateException("Only OPEN tasks can be claimed.");
        }
        this.status = TaskStatus.CLAIMED;
        this.claimedBy = userId;
        this.claimedAt = LocalDateTime.now();
    }

    public void complete() {
        if (status != TaskStatus.CLAIMED) {
            throw new IllegalStateException("Only CLAIMED tasks can be completed.");
        }
        this.status = TaskStatus.COMPLETED;
    }
}