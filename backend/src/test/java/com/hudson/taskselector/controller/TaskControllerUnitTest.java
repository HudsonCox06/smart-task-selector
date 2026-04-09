package com.hudson.taskselector.controller;

import com.hudson.taskselector.dto.SelectTaskRequest;
import com.hudson.taskselector.dto.SelectedTaskResponse;
import com.hudson.taskselector.dto.SelectionResult;
import com.hudson.taskselector.mapper.TaskMapper;
import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.service.ScoreCalculator;
import com.hudson.taskselector.service.TaskService;
import com.hudson.taskselector.model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskControllerUnitTest {

    @Test
    void selectTask_returnsSelectedTaskResponseWithScoreReasonAndWeights() {
        TaskService taskService = mock(TaskService.class);
        TaskMapper taskMapper = new TaskMapper();

        TaskController controller = new TaskController(taskService, taskMapper);

        Task selectedTask = new Task(1L, "Selectable task", 4, "school", TaskStatus.OPEN);

        SelectionResult result = new SelectionResult(
            selectedTask,
            50,
            "Priority 4 × weight 5, plus incomplete bonus 30",
            5,
            30
        );

        when(taskService.selectTask("school", 1, 5, 30))
                .thenReturn(result);

        SelectTaskRequest request = new SelectTaskRequest();
        request.setCategory("school");
        request.setMinPriority(1);
        request.setPriorityWeight(5);
        request.setIncompleteBonus(30);

        SelectedTaskResponse response = controller.selectTask(request);

        assertEquals(1L, response.getTask().getId());
        assertEquals("Selectable task", response.getTask().getTitle());
        assertEquals(50, response.getScore());
        assertEquals("Priority 4 × weight 5, plus incomplete bonus 30", response.getReason());
        assertEquals(5, response.getPriorityWeightUsed());
        assertEquals(30, response.getIncompleteBonusUsed());
    }
}