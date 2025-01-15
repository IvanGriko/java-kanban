package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.TaskStatus;
import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.service.HistoryManager;
import ru.yandex.tasktracker.service.Managers;
import ru.yandex.tasktracker.service.TaskManager;

class ManagersTest {

    @Test
    void getDefaultTaskManagerTest() throws ManagerSaveException {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefaultTaskManager();
        Task task = new Task("Name", "Description", 1, TaskStatus.NEW, 15);
        taskManager.addTask(task);
        Assertions.assertNotNull(manager, "Менеджер не найден.");
        Assertions.assertNotNull(taskManager.getTasks(), "Задача не найдена.");
    }

    @Test
    void getDefaultHistoryTest() throws ManagerSaveException {
        Managers manager = new Managers();
        TaskManager taskManager = manager.getDefaultTaskManager();
        HistoryManager historyManager = manager.getDefaultHistory();
        Task task = new Task("Name", "Description", 1, TaskStatus.NEW, 15);
        taskManager.addTask(task);
        Assertions.assertNotNull(taskManager.getTasks(), "Задача не найдена.");
        taskManager.getTaskById(1);
        Assertions.assertNotNull(manager, "Менеджер не найден.");
        Assertions.assertNotEquals(0, taskManager.getHistory().size(),
                "История не найдена.");
    }

    // проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void variablesAreSameAfterAddingTaskToManager() throws ManagerSaveException {
        Task task = new Task("Name", "Description", 1, TaskStatus.NEW, 15);
        TaskManager taskManager = Managers.getDefaultTaskManager();
        taskManager.addTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()),
                "Задачи не совпадают.");
    }
}