package ru.yandex.tasktracker;
import org.junit.jupiter.api.Assertions;
import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.model.TaskStatus;

import java.util.List;

public class Main {
    public static void main(String[] args) throws ManagerSaveException {

        InMemoryTaskManager manager = new InMemoryTaskManager();

        System.out.println("\nСоздайте две задачи, эпик с тремя подзадачами и эпик без подзадач.\n");

        Task task1 = new Task("Задача1", "Первая задача", manager.taskCount, TaskStatus.NEW);
        manager.addTask(task1);
        Task task2 = new Task("Задача2", "Вторая задача", manager.taskCount, TaskStatus.NEW);
        manager.addTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Эпик с 3 подзадачами", manager.taskCount);
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1 эпика 1",
                manager.taskCount, TaskStatus.NEW, epic1);
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2 эпика 1",
                manager.taskCount, TaskStatus.NEW, epic1);
        manager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача 3", "Подзадача 3 эпика 1",
                manager.taskCount, TaskStatus.NEW, epic1);
        manager.addSubtask(subtask3);
        Epic epic2 = new Epic("Эпик 2", "Второй эпик", manager.taskCount);
        manager.addEpic(epic2);

        System.out.println("Запросите созданные задачи несколько раз в разном порядке. \n" +
                "После каждого запроса выведите историю и убедитесь, что в ней нет повторов.\n");

        manager.getTask(1);
        manager.getSubtask(4);
        manager.getEpic(3);
        List history1 = manager.getHistory();
        System.out.println("История запросов:" + history1 + "\n");

        manager.getEpic(3);
        manager.getTask(1);
        manager.getSubtask(4);
        List history2 = manager.getHistory();
        System.out.println("История запросов:" + history2 + "\n");

        manager.getSubtask(4);
        manager.getTask(1);
        manager.getEpic(3);
        List history3 = manager.getHistory();
        System.out.println("История запросов:" + history3 + "\n");

        Assertions.assertNotEquals(history1, history2, "В истории запросов есть повторы");
        Assertions.assertNotEquals(history1, history3, "В истории запросов есть повторы");

        System.out.println("Удалите задачу, которая есть в истории, " +
                "и проверьте, что при печати она не будет выводиться.\n");

        manager.removeTaskByID(1);
        System.out.println("История запросов:" + manager.getHistory() + "\n");

        System.out.println("Удалите эпик с тремя подзадачами и убедитесь, " +
                        "что из истории удалился как сам эпик, так и все его подзадачи.\n");

        manager.removeEpic(3);
        System.out.println("История запросов:" + manager.getHistory() + "\n");
    }
}