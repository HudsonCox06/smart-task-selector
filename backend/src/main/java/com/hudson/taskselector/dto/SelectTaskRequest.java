package com.hudson.taskselector.dto;

public class SelectTaskRequest {

    private String category;
    private Integer minPriority;
    private Boolean random;
    private Boolean includeCompleted;

    public SelectTaskRequest() {
    }

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

    public Boolean getRandom() {
        return random;
    }

    public void setRandom(Boolean random) {
        this.random = random;
    }

    public Boolean getIncludeCompleted() {
        return includeCompleted;
    }

    public void setIncludeCompleted(Boolean includeCompleted) {
        this.includeCompleted = includeCompleted;
    }
}