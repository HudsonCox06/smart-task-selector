package com.hudson.taskselector.exception;

public class NoTaskSelectedException extends RuntimeException {

    public NoTaskSelectedException() {
        super("No task matched the selection criteria");
    }
}