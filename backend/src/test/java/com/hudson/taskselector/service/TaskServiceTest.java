package com.hudson.taskselector.service;

import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.repository.TaskRepository;
import com.hudson.taskselector.service.ScoreCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskServiceTest {

    @Test
    void selectTask_returnsHighestPriorityTask() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task low = new Task(1L, "Low", 1, "school", false);
        Task high = new Task(2L, "High", 5, "school", false);

        when(mockRepo.findAll()).thenReturn(List.of(low, high));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        Task result = service.selectTask("school", 1, false, false, null, null);

        assertEquals("High", result.getTitle());
    }

    @Test
    void selectTask_filtersByCategoryAndMinPriority() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task task1 = new Task(1L, "Low school", 1, "school", false);
        Task task2 = new Task(2L, "High work", 5, "work", false);
        Task task3 = new Task(3L, "High school", 5, "school", false);

        when(mockRepo.findAll()).thenReturn(List.of(task1, task2, task3));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        Task result = service.selectTask("school", 3, false, false, null, null);

        assertEquals("High school", result.getTitle());
    }

    @Test
    void selectTask_prefersIncompleteOverCompletedWhenScoresClose() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task completedHigh = new Task(1L, "Completed High", 5, "school", true);
        Task incompleteSlightlyLower = new Task(2L, "Incomplete Slightly Lower", 4, "school", false);

        when(mockRepo.findAll()).thenReturn(List.of(completedHigh, incompleteSlightlyLower));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        Task result = service.selectTask("school", 1, false, true, null, null);

        assertEquals("Completed High", result.getTitle());
    }

    @Test
    void selectTask_returnsFirstTaskWhenScoresAreEqual() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task task1 = new Task(1L, "Task A", 5, "school", false);
        Task task2 = new Task(2L, "Task B", 5, "school", false);

        when(mockRepo.findAll()).thenReturn(List.of(task1, task2));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        Task result = service.selectTask("school", 1, false, false, null, null);

        assertEquals("Task A", result.getTitle());
    }

    @Test
    void selectTask_usesRequestOverridesToChangeSelection() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task highCompleted = new Task(1L, "High Completed", 5, "school", true);
        Task lowerIncomplete = new Task(2L, "Lower Incomplete", 4, "school", false);

        when(mockRepo.findAll()).thenReturn(List.of(highCompleted, lowerIncomplete));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        Task result = service.selectTask("school", 1, false, true, 5, 30);

        assertEquals("Lower Incomplete", result.getTitle());
    }
}