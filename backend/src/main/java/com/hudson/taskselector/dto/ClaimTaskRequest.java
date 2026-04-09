package com.hudson.taskselector.dto;

import jakarta.validation.constraints.NotBlank;

public class ClaimTaskRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    public ClaimTaskRequest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}