package com.hudson.taskselector.service;

import com.hudson.taskselector.dto.SelectionResult;
import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.model.TaskStatus;
import com.hudson.taskselector.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskServiceTest {

    @Test
    void selectTask_returnsHighestPriorityTask() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task low = new Task(1L, "Low", 1, "school", TaskStatus.OPEN);
        Task high = new Task(2L, "High", 5, "school", TaskStatus.OPEN);

        when(mockRepo.findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
                TaskStatus.OPEN, "school", 1))
                .thenReturn(List.of(low, high));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        SelectionResult result = service.selectTask("school", 1,null, null);

        assertEquals("High", result.getTask().getTitle());
        assertEquals(55, result.getScore());
        assertEquals(10, result.getPriorityWeightUsed());
        assertEquals(5, result.getIncompleteBonusUsed());
    }

    @Test
    void selectTask_filtersByCategoryAndMinPriority() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task task1 = new Task(1L, "Low school", 1, "school", TaskStatus.OPEN);
        Task task2 = new Task(2L, "High work", 5, "work", TaskStatus.OPEN);
        Task task3 = new Task(3L, "High school", 5, "school", TaskStatus.OPEN);

        when(mockRepo.findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
                TaskStatus.OPEN, "school", 3))
                .thenReturn(List.of(task3));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        SelectionResult result = service.selectTask("school", 3,null, null);

        assertEquals("High school", result.getTask().getTitle());
        assertEquals(55, result.getScore());
    }


    @Test
    void selectTask_returnsFirstTaskWhenScoresAreEqual() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task task1 = new Task(1L, "Task A", 5, "school", TaskStatus.OPEN);
        Task task2 = new Task(2L, "Task B", 5, "school", TaskStatus.OPEN);

        when(mockRepo.findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
                TaskStatus.OPEN, "school", 1))
                .thenReturn(List.of(task1, task2));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        SelectionResult result = service.selectTask("school", 1,null, null);

        assertEquals("Task A", result.getTask().getTitle());
        assertEquals(55, result.getScore());
    }

    @Test
    void selectTask_usesRequestOverridesToChangeSelection() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task higherPriority = new Task(1L, "Higher Priority", 5, "school", TaskStatus.OPEN);
        Task lowerPriority = new Task(2L, "Lower Priority", 4, "school", TaskStatus.OPEN);

        when(mockRepo.findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
                TaskStatus.OPEN, "school", 1))
                .thenReturn(List.of(higherPriority, lowerPriority));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        SelectionResult result = service.selectTask("school", 1, 5, 30);

        assertEquals("Higher Priority", result.getTask().getTitle());
        assertEquals(55, result.getScore());
        assertEquals(5, result.getPriorityWeightUsed());
        assertEquals(30, result.getIncompleteBonusUsed());
    }

    @Test
    void completeTask_throwsWhenTaskAlreadyCompleted() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task completedTask = new Task(1L, "Done", 5, "school", TaskStatus.COMPLETED);

        when(mockRepo.findById(1L)).thenReturn(java.util.Optional.of(completedTask));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> service.completeTaskById(1L)
        );
    }

    @Test
    void completeTask_completesClaimedTask() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task claimedTask = new Task(1L, "Claimed Task", 5, "school", TaskStatus.CLAIMED);
        claimedTask.setClaimedBy("hudson");

        when(mockRepo.findById(1L)).thenReturn(java.util.Optional.of(claimedTask));
        when(mockRepo.save(claimedTask)).thenReturn(claimedTask);

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        Task result = service.completeTaskById(1L);

        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }

    @Test
    void completeTask_throwsWhenTaskIsStillOpen() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task openTask = new Task(1L, "Open Task", 5, "school", TaskStatus.OPEN);

        when(mockRepo.findById(1L)).thenReturn(java.util.Optional.of(openTask));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> service.completeTaskById(1L)
        );
    }

    @Test
    void claimTask_claimsOpenTask() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task openTask = new Task(1L, "Open Task", 5, "school", TaskStatus.OPEN);

        when(mockRepo.findById(1L)).thenReturn(java.util.Optional.of(openTask));
        when(mockRepo.save(openTask)).thenReturn(openTask);

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        Task result = service.claimTaskById(1L, "hudson");

        assertEquals(TaskStatus.CLAIMED, result.getStatus());
        assertEquals("hudson", result.getClaimedBy());
    }

    @Test
    void claimTask_throwsWhenTaskAlreadyClaimed() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task claimedTask = new Task(1L, "Claimed Task", 5, "school", TaskStatus.CLAIMED);
        claimedTask.setClaimedBy("someone");

        when(mockRepo.findById(1L)).thenReturn(java.util.Optional.of(claimedTask));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> service.claimTaskById(1L, "hudson")
        );
    }

    @Test
    void claimBestTask_selectsAndClaimsBestTask() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task low = new Task(1L, "Low", 1, "school", TaskStatus.OPEN);
        Task high = new Task(2L, "High", 5, "school", TaskStatus.OPEN);

        when(mockRepo.findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
                TaskStatus.OPEN, "school", 1))
                .thenReturn(List.of(low, high));

        when(mockRepo.save(high)).thenReturn(high);

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        SelectionResult result = service.claimBestTask("school", 1, "hudson", null, null);

        assertEquals("High", result.getTask().getTitle());
        assertEquals(TaskStatus.CLAIMED, result.getTask().getStatus());
        assertEquals("hudson", result.getTask().getClaimedBy());
    }

    @Test
    void claimBestTask_throwsWhenTaskAlreadyClaimed() {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task claimed = new Task(1L, "Claimed", 5, "school", TaskStatus.CLAIMED);
        claimed.setClaimedBy("someone");

        when(mockRepo.findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
                TaskStatus.OPEN, "school", 1))
                .thenReturn(List.of());

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> service.claimBestTask("school", 1, "hudson", null, null)
        );
    }

    @Test
    void claimBestTask_onlyOneThreadClaimsTask() throws Exception {
        TaskRepository mockRepo = mock(TaskRepository.class);

        Task task = new Task(1L, "Important Task", 5, "school", TaskStatus.OPEN);

        // Always return the same task for selection
        when(mockRepo.findByStatusAndCategoryIgnoreCaseAndPriorityGreaterThanEqual(
                TaskStatus.OPEN, "school", 1))
                .thenReturn(List.of(task));

        // Simulate optimistic locking: first save succeeds, second fails
        when(mockRepo.save(task))
                .thenReturn(task)
                .thenThrow(new org.springframework.orm.ObjectOptimisticLockingFailureException(Task.class, 1L));

        TaskService service = new TaskService(mockRepo, null, new ScoreCalculator(10, 5));

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(2);

        java.util.concurrent.Callable<Boolean> call = () -> {
            try {
                service.claimBestTask("school", 1, "user", null, null);
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        var future1 = executor.submit(call);
        var future2 = executor.submit(call);

        boolean result1 = future1.get();
        boolean result2 = future2.get();

        // Exactly one should succeed
        assertEquals(1, (result1 ? 1 : 0) + (result2 ? 1 : 0));

        executor.shutdown();
    }
}