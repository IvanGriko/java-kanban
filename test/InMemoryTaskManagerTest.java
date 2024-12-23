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

class InMemoryTaskManagerTest {

    TaskManager testManager;

    @BeforeEach
    public void initManager() {
        testManager = Managers.getDefaultTaskManager();
    }

    @Test
    void tasksIsEqualsIfIDsIsEquals() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача", 1, TaskStatus.NEW);
        Task testTask2 = new Task("Задача2", "Вторая задача", 1, TaskStatus.NEW);
        Assertions.assertEquals(testTask1, testTask2, "Задачи не равны");
    }

    @Test
    void getTasksTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask1);
        Task testTask2 = new Task("Задача2", "Вторая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask2);
        ArrayList<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(testTask1);
        expectedTasks.add(testTask2);
        Assertions.assertEquals(expectedTasks, testManager.getTasks(), "Списки задач не совпадают");
    }

    @Test
    void deleteTasksTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask1);
        Task testTask2 = new Task("Задача2", "Вторая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask2);
        testManager.deleteTasks();
        Assertions.assertEquals(0, testManager.getTasks().size(), "Задачи не удалены");
    }

    @Test
    void getTaskTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask1);
        Assertions.assertEquals(testTask1, testManager.getTask(testTask1.getId()), "Задачи не совпадают.");
    }

    @Test
    void addTaskTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask1);
        Assertions.assertNotNull(testManager.getTask(1), "Задача не найдена.");
    }

    @Test
    void updateTaskTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask1);
        Task testTask2 = new Task("Задача2", "Вторая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask2);
        Task oldTask = testTask2;
        Task updatingTask = testManager.getTask(2);
        updatingTask.setDescription("Обновлённая задача");
        testManager.updateTask(updatingTask);
        Assertions.assertEquals(oldTask, testManager.getTask(2), "Задача не обновлена.");
    }

    @Test
    void removeTaskTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask1);
        int id = testTask1.getId();
        testManager.removeTask(testTask1);
        Assertions.assertNull(testManager.getTask(id), "Задача не удалена.");
    }

    @Test
    void removeTaskByIDTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW);
        testManager.addTask(testTask1);
        int id = testTask1.getId();
        testManager.removeTaskByID(id);
        Assertions.assertNull(testManager.getTask(id), "Задача не удалена.");
    }

    @Test
    void getSubtasksTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Второй эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic2);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        Subtask testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic2);
        testManager.addSubtask(testSubtask2);
        ArrayList<Task> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(testSubtask1);
        expectedSubtasks.add(testSubtask2);
        Assertions.assertEquals(expectedSubtasks, testManager.getSubtasks(),
                "Списки подзадач не совпадают");
    }

    @Test
    void subtasksIsEqualsIfIDsIsEqualsTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик", 1);
        Epic epic2 = new Epic("Эпик2", "Второй эпик", 1);
        Task testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                1, TaskStatus.NEW, epic1);
        Task testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                1, TaskStatus.NEW, epic2);
        Assertions.assertEquals(testSubtask1, testSubtask2, "Подзадачи не равны");
    }

    @Test
    void deleteSubtasksTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        Subtask testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask2);
        testManager.deleteSubtasks();
        Assertions.assertEquals(0, testManager.getSubtasks().size(),
                "Подзадачи не удалены");
    }

    @Test
    void getSubtaskTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        Assertions.assertEquals(testSubtask1, testManager.getSubtask(testSubtask1.getId()),
                "Подзадачи не совпадают.");
    }

    @Test
    void addSubtaskTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        Assertions.assertNotNull(testManager.getSubtask(testSubtask1.getId()),
                "Подзадача не найдена.");
    }

    @Test
    void updateSubtaskTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        Subtask oldSubtask = testSubtask1;
        Subtask updatingSubtask = testManager.getSubtask(testSubtask1.getId());
        updatingSubtask.setDescription("Обновлённая задача");
        updatingSubtask.setStatus(TaskStatus.DONE);
        testManager.updateSubtask(updatingSubtask);
        Assertions.assertEquals(oldSubtask, testManager.getSubtask(updatingSubtask.getId()),
                "Подзадача не обновлена.");
    }

    @Test
    void removeSubtaskTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        int id = testSubtask1.getId();
        testManager.removeSubtask(testSubtask1);
        Assertions.assertNull(testManager.getSubtask(id), "Подзадача не удалена.");
    }

    @Test
    void removeSubtaskByIDTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        int id = testSubtask1.getId();
        testManager.removeSubtaskByID(id);
        Assertions.assertNull(testManager.getSubtask(id), "Подзадача не удалена.");
    }

    @Test
    void getEpicsTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Второй эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic2);
        ArrayList<Epic> expectedEpics = new ArrayList<>();
        expectedEpics.add(epic1);
        expectedEpics.add(epic2);
        Assertions.assertEquals(expectedEpics, testManager.getEpics(),
                "Списки подзадач не совпадают");
    }

    @Test
    void epicsIsEqualsIfIDsIsEqualsTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик", 1);
        Epic epic2 = new Epic("Эпик2", "Второй эпик", 2);
        Task testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                3, TaskStatus.NEW, epic1);
        Task testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                3, TaskStatus.IN_PROGRESS, epic2);
        Assertions.assertEquals(testSubtask1, testSubtask2, "Подзадачи не равны");
    }

    @Test
    void deleteEpicsTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Второй эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic2);
        testManager.deleteEpics();
        Assertions.assertEquals(0, testManager.getEpics().size(),
                "Эпики не удалены");
    }

    @Test
    void getEpicTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Assertions.assertEquals(epic1, testManager.getEpic(epic1.getId()),
                "Эпики не совпадают.");
    }

    @Test
    void addEpicTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Assertions.assertNotNull(testManager.getEpic(epic1.getId()),
                "Эпик не найден.");
    }

    @Test
    void updateEpicTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask);
        Epic oldEpic = epic1;
        Epic updatingEpic = epic1;
        updatingEpic.setDescription("Обновлённый эпик");
        testSubtask.setStatus(TaskStatus.DONE);
        testManager.updateSubtask(testSubtask);
        testManager.updateEpic(epic1);
        Assertions.assertEquals(oldEpic, testManager.getEpic(updatingEpic.getId()),
                "Эпик не обновлён.");
    }

    @Test
    void removeEpicTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask);
        testManager.removeEpic(epic1.getId());
        Assertions.assertNull(testManager.getEpic(epic1.getId()), "Эпик не удалён.");
        Assertions.assertEquals(0, testManager.getSubtasksByEpic(epic1).size(),
                "Подзадачи эпика не удалены");
    }

    @Test
    void updateEpicStatusTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        Subtask updatingSubtask = testManager.getSubtask(testSubtask1.getId());
        updatingSubtask.setDescription("Обновлённая задача");
        updatingSubtask.setStatus(TaskStatus.DONE);
        testManager.updateSubtask(updatingSubtask);
        Assertions.assertEquals(TaskStatus.DONE, epic1.getStatus(),
                "Статус эпика не обновлён.");
    }

    @Test
    void getSubtasksByEpicTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask1);
        Subtask testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, epic1);
        testManager.addSubtask(testSubtask2);
        ArrayList<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(testSubtask1);
        expectedSubtasks.add(testSubtask2);
        Assertions.assertEquals(expectedSubtasks, testManager.getSubtasksByEpic(epic1),
                "Списки эпиков не совпадают.");
    }
}