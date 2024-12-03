package ru.yandex.tasktracker.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.Managers;
import ru.yandex.tasktracker.service.TaskManager;
import ru.yandex.tasktracker.model.TaskStatus;

class ManagersTest {

    @Test
    void getDefaultTaskManagerTest() {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefaultTaskManager();
        Task task = new Task("Name", "Description", 1, TaskStatus.NEW);
        taskManager.addTask(task);
        Assertions.assertNotNull(manager, "Менеджер не найден.");
        Assertions.assertNotNull(taskManager.getTasks(), "Задача не найдена.");
    }

    @Test
    void getDefaultHistoryTest() {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefaultTaskManager();
        HistoryManager historyManager = manager.getDefaultHistory();
        Task task = new Task("Name", "Description", 1, TaskStatus.NEW);
        taskManager.addTask(task);
        Assertions.assertNotNull(taskManager.getTasks(), "Задача не найдена.");
        taskManager.getTask(1);
        Assertions.assertNotNull(manager, "Менеджер не найден.");
        Assertions.assertNotEquals(0, taskManager.getHistory().size(),
                "История не найдена.");
    }

    @Test // проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    void variablesAreSameAfterAddingTaskToManager() {
        Task task = new Task("Name", "Description", 1, TaskStatus.NEW);
        TaskManager taskManager = Managers.getDefaultTaskManager();
        taskManager.addTask(task);
        Assertions.assertEquals(task, taskManager.getTask(task.getId()),
                "Задачи не совпадают.");
    }
}