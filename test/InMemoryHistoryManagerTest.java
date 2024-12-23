package ru.yandex.tasktracker.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.service.Managers;
import ru.yandex.tasktracker.service.TaskManager;
import ru.yandex.tasktracker.model.TaskStatus;
import ru.yandex.tasktracker.exceptions.ManagerSaveException;

import java.util.ArrayList;

import static java.util.Arrays.asList;

class InMemoryHistoryManagerTest {

    TaskManager testManager;

    @BeforeEach
    public void initManager() {
        testManager = Managers.getDefaultTaskManager();
    }

    @Test
    void getHistoryTest() throws ManagerSaveException {
        Task task = new Task("Задача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(task);
        Epic epic = new Epic("Эпик", "Тестовый",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic);
        testManager.addSubtask(subtask);
        ArrayList<Task> expectedHistoryList = new ArrayList<>(asList(subtask, epic, task));

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
}
