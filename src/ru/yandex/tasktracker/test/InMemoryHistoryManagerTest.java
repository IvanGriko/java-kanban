package ru.yandex.tasktracker.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.service.TaskManager;
import ru.yandex.tasktracker.service.TaskStatus;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void getHistory() {
        TaskManager testManager = new InMemoryTaskManager();
        Task task = new Task("Задача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(task);
        Epic epic = new Epic("Эпик", "Тестовый",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic);
        testManager.addSubtask(subtask);
        ArrayList<Task> expectedHistoryList = new ArrayList<>
                (asList(epic, epic, task, epic, epic, subtask, subtask, subtask, epic, task));
        testManager.getEpic(2);     // 2
        testManager.getTask(1);     // 3
        testManager.getEpic(2);     // 4
        testManager.getEpic(2);     // 5
        testManager.getEpic(2);     // 6
        testManager.getTask(1);     // 7
        testManager.getEpic(2);     // 8
        testManager.getEpic(2);     // 9
        testManager.getSubtask(3);  // 10
        testManager.getSubtask(3);  // 11
        testManager.getSubtask(3);  // 12
        testManager.getEpic(2);     // 13
        testManager.getTask(1);     // 14
        Assertions.assertEquals(expectedHistoryList, testManager.getHistory(),
                "Списки истории не совпадают.");
    }

    @Test
    void historyManagerSavesPreviousVersionOfTask() {
        TaskManager testManager = new InMemoryTaskManager();
        Task task = new Task("Задача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(task);
        testManager.getTask(task.getId());
        task.setDescription("Изменённая");
        task.setStatus(TaskStatus.IN_PROGRESS);
        testManager.updateTask(task);
        testManager.getTask(task.getId());
        Assertions.assertFalse((testManager.getHistory().get(0).toString()).
                equals(testManager.getHistory().get(1).toString()),
                "Сохранена одна и та же версия задачи.");


    }
}