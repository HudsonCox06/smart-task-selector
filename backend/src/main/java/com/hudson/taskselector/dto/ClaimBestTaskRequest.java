package com.hudson.taskselector.dto;

import jakarta.validation.constraints.NotBlank;

public class ClaimBestTaskRequest {

    private String category;
    private Integer minPriority;

    @NotBlank(message = "User ID is required")
    private String userId;

    private Integer priorityWeight;
    private Integer incompleteBonus;

    public ClaimBestTaskRequest() {}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getMinPriority() {
        return minPriority;
    }

    public void setMinPriority(Integer minPriority) {
        this.minPriority = minPriority;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getPriorityWeight() {
        return priorityWeight;
    }

    public void setPriorityWeight(Integer priorityWeight) {
        this.priorityWeight = priorityWeight;
    }

    public Integer getIncompleteBonus() {
        return incompleteBonus;
    }

    public void setIncompleteBonus(Integer incompleteBonus) {
        this.incompleteBonus = incompleteBonus;
    }
}