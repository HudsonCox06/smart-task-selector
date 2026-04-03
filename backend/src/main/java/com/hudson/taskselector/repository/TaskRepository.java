package com.hudson.taskselector.repository;

import com.hudson.taskselector.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}