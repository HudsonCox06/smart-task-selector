package com.hudson.taskselector.controller;

import com.hudson.taskselector.dto.SelectTaskRequest;
import com.hudson.taskselector.dto.SelectedTaskResponse;
import com.hudson.taskselector.mapper.TaskMapper;
import com.hudson.taskselector.model.Task;
import com.hudson.taskselector.service.ScoreCalculator;
import com.hudson.taskselector.service.TaskService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskControllerUnitTest {

    @Test
    void selectTask_returnsSelectedTaskResponseWithScoreReasonAndWeights() {
        TaskService taskService = mock(TaskService.class);
        TaskMapper taskMapper = new TaskMapper();
        ScoreCalculator scoreCalculator = new ScoreCalculator(10, 5);

        TaskController controller = new TaskController(taskService, taskMapper, scoreCalculator);

        Task selectedTask = new Task(1L, "Selectable task", 4, "school", false);

        when(taskService.selectTask("school", 1, false, true, 5, 30))
                .thenReturn(selectedTask);

        SelectTaskRequest request = new SelectTaskRequest();
        request.setCategory("school");
        request.setMinPriority(1);
        request.setRandom(false);
        request.setIncludeCompleted(true);
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