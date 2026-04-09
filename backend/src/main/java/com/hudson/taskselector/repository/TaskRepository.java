package com.hudson.taskselector.repository;

import com.hudson.taskselector.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hudson.taskselector.model.TaskStatus;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByStatusAndCategoryIgnoreCase(TaskStatus status, String category);

    List<Task> findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
            TaskStatus status,
            String category,
            int priority
    );
}