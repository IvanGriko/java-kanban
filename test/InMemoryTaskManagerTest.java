package ru.yandex.tasktracker.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.TaskStatus;
import ru.yandex.tasktracker.exceptions.ManagerSaveException;

import java.time.LocalDateTime;
import java.util.ArrayList;

class InMemoryTaskManagerTest {

    TaskManager testManager;

    @BeforeEach
    public void initManager() {
        testManager = Managers.getDefaultTaskManager();
    }

    // Тест расчёта статуса эпика
    @Test
    void determinateStatusOfEpicTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Все подзадачи со статусом NEW", 1);
        Subtask subtask1_1 = new Subtask("Подзадача 1_1", "П.1_1", 11, TaskStatus.NEW, 30, epic1);
        Subtask subtask1_2 = new Subtask("Подзадача 1_2", "П.1_2", 12, TaskStatus.NEW, 40, epic1);
        testManager.addEpic(epic1);
        testManager.addSubtask(subtask1_1);
        testManager.addSubtask(subtask1_2);
        Epic epic2 = new Epic("Эпик2", "Все подзадачи со статусом DONE.", 2);
        Subtask subtask2_1 = new Subtask("Подзадача 2_1", "П.2_1", 21, TaskStatus.DONE, 30, epic2);
        Subtask subtask2_2 = new Subtask("Подзадача 2_2", "П.2_2", 22, TaskStatus.DONE, 25, epic2);
        testManager.addEpic(epic2);
        testManager.addSubtask(subtask2_1);
        testManager.addSubtask(subtask2_2);
        Epic epic3 = new Epic("Эпик3", "Подзадачи со статусами NEW и DONE", 3);
        Subtask subtask3_1 = new Subtask("Подзадача 3_1", "П.3_1", 31, TaskStatus.NEW, 15, epic3);
        Subtask subtask3_2 = new Subtask("Подзадача 3_2", "П.3_2", 32, TaskStatus.DONE, 20, epic3);
        testManager.addEpic(epic3);
        testManager.addSubtask(subtask3_1);
        testManager.addSubtask(subtask3_2);
        Epic epic4 = new Epic("Эпик4", "Подзадачи со статусом IN_PROGRESS", 4);
        Subtask subtask4_1 = new Subtask("Подзадача 4_1", "П.4_1", 41, TaskStatus.NEW, 35, epic4);
        Subtask subtask4_2 = new Subtask("Подзадача 4_2", "П.4_2", 42, TaskStatus.IN_PROGRESS, 15, epic4);
        testManager.addEpic(epic4);
        testManager.addSubtask(subtask4_1);
        testManager.addSubtask(subtask4_2);
        Assertions.assertEquals(TaskStatus.NEW, epic1.getStatus(), "Статус не соответствует");
        Assertions.assertEquals(TaskStatus.DONE, epic2.getStatus(), "Статус не соответствует");
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic3.getStatus(), "Статус не соответствует");
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic4.getStatus(), "Статус не соответствует");
    }

    // Проверка наличия эпика
    @Test
    void subtaskHasEpicTest() {
        Epic epic = new Epic("Эпик", "Тестовый эпик", 1);
        Subtask subtask = new Subtask("Подзадача", "Тестовая подзадача", 2, TaskStatus.NEW, 30, epic);
        Assertions.assertNotNull(subtask.getEpic(), "Эпик отсутствует");
    }

    // Тест на корректность расчёта пересечения интервалов и продолжительности
    @Test
    public void timeAndDurationTest() {
        Epic epic = new Epic("Эпик", "Тестовый эпик", 1);
        testManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача1", "Первая подзадача", 2, TaskStatus.NEW,
                LocalDateTime.of(2025,1,1,17,20), 15, epic);
        testManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Вторая подзадача", 3, TaskStatus.NEW,
                LocalDateTime.of(2025,1,1,17,40), 15, epic);
        testManager.addSubtask(subtask2);
        Assertions.assertEquals(subtask1.getDuration().plus(subtask2.getDuration()), epic.getDuration(),
                "Длительность эпика не равна сумме длительностей подзадач");
        Assertions.assertEquals(subtask1.getStartTime(), epic.getStartTime(),
                "Начало эпика не совпало с началом первой подзадачи");
        Assertions.assertEquals(subtask2.getEndTime(), epic.getEndTime(),
                "Завершение эпика не совпало с завершением последней подзадачи");
    }

    @Test
    void tasksIsEqualsIfIDsIsEquals() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача", 1, TaskStatus.NEW, 15);
        Task testTask2 = new Task("Задача2", "Вторая задача", 1, TaskStatus.NEW, 20);
        Assertions.assertEquals(testTask1, testTask2, "Задачи не равны");
    }

    @Test
    void getTasksTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(testTask1);
        Task testTask2 = new Task("Задача2", "Вторая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(testTask2);
        ArrayList<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(testTask1);
        expectedTasks.add(testTask2);
        Assertions.assertEquals(expectedTasks, testManager.getTasks(), "Списки задач не совпадают");
    }

    @Test
    void deleteTasksTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(testTask1);
        Task testTask2 = new Task("Задача2", "Вторая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 20);
        testManager.addTask(testTask2);
        testManager.deleteTasks();
        Assertions.assertEquals(0, testManager.getTasks().size(), "Задачи не удалены");
    }

    @Test
    void getTaskTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(testTask1);
        Assertions.assertEquals(testTask1, testManager.getTask(testTask1.getId()), "Задачи не совпадают.");
    }

    @Test
    void addTaskTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(testTask1);
        Assertions.assertNotNull(testManager.getTask(1), "Задача не найдена.");
    }

    @Test
    void updateTaskTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW,15);
        testManager.addTask(testTask1);
        Task testTask2 = new Task("Задача2", "Вторая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 20);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
        testManager.addTask(testTask1);
        int id = testTask1.getId();
        testManager.removeTask(testTask1);
        Assertions.assertNull(testManager.getTask(id), "Задача не удалена.");
    }

    @Test
    void removeTaskByIDTest() throws ManagerSaveException {
        Task testTask1 = new Task("Задача1", "Первая задача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
        testManager.addSubtask(testSubtask1);
        Subtask testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 20, epic2);
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
                1, TaskStatus.NEW, 15, epic1);
        Task testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                1, TaskStatus.NEW, 20, epic2);
        Assertions.assertEquals(testSubtask1, testSubtask2, "Подзадачи не равны");
    }

    @Test
    void deleteSubtasksTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Эпик1", "Первый эпик",
                ((InMemoryTaskManager) testManager).taskCount);
        testManager.addEpic(epic1);
        Subtask testSubtask1 = new Subtask("Подзадача1", "Первая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
        testManager.addSubtask(testSubtask1);
        Subtask testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                3, TaskStatus.NEW, 15, epic1);
        Task testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                3, TaskStatus.IN_PROGRESS, 20, epic2);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
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
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 15, epic1);
        testManager.addSubtask(testSubtask1);
        Subtask testSubtask2 = new Subtask("Подзадача2", "Вторая подзадача",
                ((InMemoryTaskManager) testManager).taskCount, TaskStatus.NEW, 20, epic1);
        testManager.addSubtask(testSubtask2);
        ArrayList<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(testSubtask1);
        expectedSubtasks.add(testSubtask2);
        Assertions.assertEquals(expectedSubtasks, testManager.getSubtasksByEpic(epic1),
                "Списки эпиков не совпадают.");
    }
}