package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.TaskStatus;
import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.service.Managers;
import ru.yandex.tasktracker.service.TaskManager;

import java.time.LocalDateTime;
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(task);
        Epic epic = new Epic("Эпик", "Тестовый",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(40), 15, epic);
        testManager.addSubtask(subtask);
        ArrayList<Task> expectedHistoryList = new ArrayList<>(asList(subtask, epic, task));
        testManager.getEpicById(2);
        testManager.getSubtaskById(3);
        testManager.getSubtaskById(3);
        testManager.getSubtaskById(3);
        testManager.getEpicById(2);
        testManager.getTaskById(1);
        Assertions.assertEquals(expectedHistoryList, testManager.getHistory(), "Списки истории не совпадают.");
    }

    @Test
    void removeFirstInHistoryTest() throws ManagerSaveException {
        Task task = new Task("Задача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(task);
        Epic epic = new Epic("Эпик", "Тестовый",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(40), 15, epic);
        testManager.addSubtask(subtask);
        ArrayList<Task> expectedHistoryList = new ArrayList<>(asList(epic, task));
        testManager.getSubtaskById(3);
        testManager.getEpicById(2);
        testManager.getTaskById(1);
        testManager.removeFromHistory(3);
        Assertions.assertEquals(expectedHistoryList, testManager.getHistory(), "Списки истории не совпадают.");
    }

    @Test
    void removeMidleInHistoryTest() throws ManagerSaveException {
        Task task = new Task("Задача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(task);
        Epic epic = new Epic("Эпик", "Тестовый",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(40), 15, epic);
        testManager.addSubtask(subtask);
        ArrayList<Task> expectedHistoryList = new ArrayList<>(asList(subtask, task));
        testManager.getSubtaskById(3);
        testManager.getEpicById(2);
        testManager.getTaskById(1);
        testManager.removeFromHistory(2);
        Assertions.assertEquals(expectedHistoryList, testManager.getHistory(), "Списки истории не совпадают.");
    }

    @Test
    void removeLastInHistoryTest() throws ManagerSaveException {
        Task task = new Task("Задача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(task);
        Epic epic = new Epic("Эпик", "Тестовый",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Тестовая",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(40), 15, epic);
        testManager.addSubtask(subtask);
        ArrayList<Task> expectedHistoryList = new ArrayList<>(asList(subtask, epic));
        testManager.getSubtaskById(3);
        testManager.getEpicById(2);
        testManager.getTaskById(1);
        testManager.removeFromHistory(1);
        Assertions.assertEquals(expectedHistoryList, testManager.getHistory(), "Списки истории не совпадают.");
    }
}
