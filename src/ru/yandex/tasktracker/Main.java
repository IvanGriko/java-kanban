package ru.yandex.tasktracker;
import org.junit.jupiter.api.Assertions;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.model.TaskStatus;

public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager testManager = new InMemoryTaskManager();
        Task task = new Task("Задача", "Тестовая",
                testManager.taskCount, TaskStatus.NEW);
        testManager.addTask(task);
        testManager.getTask(task.getId());
        task.setDescription("Изменённая");
        task.setStatus(TaskStatus.IN_PROGRESS);
        testManager.updateTask(task);
        testManager.getTask(task.getId());
        Assertions.assertNotEquals((testManager.getHistory().get(0).toString()), testManager.getHistory().get(1).toString(), "Сохранена одна и та же версия задачи.");

        InMemoryTaskManager manager = new InMemoryTaskManager();

        System.out.println("\nСоздайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.\n");

        Task task1 = new Task("Задача1", "Первая задача",
                manager.taskCount, TaskStatus.NEW);
        manager.addTask(task1);
        Task task2 = new Task("Задача2", "Вторая задача",
                manager.taskCount, TaskStatus.NEW);
        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Первый эпик",
                manager.taskCount);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1 эпика 1",
                manager.taskCount, TaskStatus.NEW, epic1);
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2 эпика 1",
                manager.taskCount, TaskStatus.NEW, epic1);
        manager.addSubtask(subtask2);
        Epic epic2 = new Epic("Эпик 2", "Второй эпик",
                manager.taskCount);
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача 3", "Подзадача 3 эпика 2",
                manager.taskCount, TaskStatus.NEW, epic2);
        manager.addSubtask(subtask3);

        System.out.println("Распечатайте списки эпиков, задач и подзадач через System.out.println(..).\n");

        System.out.println(manager.getTasks());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getEpics());

        System.out.println("\nИзмените статусы созданных объектов, распечатайте их.");
        System.out.println("Проверьте, что статус задачи и подзадачи сохранился, ");
        System.out.println("а статус эпика рассчитался по статусам подзадач.\n");

        task1.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Статус задачи task1 изменён на IN_PROGRESS: " + task1 + "\n");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);

        System.out.println("Статусы подзадач изменены на IN_PROGRESS, DONE, DONE:" + manager.getSubtasks() + "\n");

        manager.updateEpicStatus(epic1);
        manager.updateEpicStatus(epic2);

        System.out.println("Статусы эпиков пересчитаны:" + manager.getEpics() + "\n");

        System.out.println("\nИстория просмотра: " + manager.getHistory() + "\n");

        System.out.println("И, наконец, попробуйте удалить одну из задач и один из эпиков.\n");

        manager.removeTask(task1);
        System.out.println("Задача task1 удалена. Список задач: " + manager.getTasks() + "\n");

        manager.removeEpic(3);

        System.out.println("Эпик с ID 3 удалён. Списки оставшихся подзадач и эпиков:\n");
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getEpics());

        manager.getEpic(6);
        manager.getTask(2);
        manager.getSubtask(7);
        manager.getEpic(6);
        manager.getTask(2);
        manager.getSubtask(7);
        System.out.println("\nИстория просмотра:\n" + manager.getHistory() + "\n");

    }
}