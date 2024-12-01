package ru.yandex.tasktracker;
import org.junit.jupiter.api.Assertions;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.service.InMemoryTaskManager;
import ru.yandex.tasktracker.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

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

//        System.out.println("Проверьте, что статус задачи и подзадачи сохранился, ");
//        System.out.println("а статус эпика рассчитался по статусам подзадач.\n");
//
//        task1.setStatus(TaskStatus.IN_PROGRESS);
//        System.out.println("Статус задачи task1 изменён на IN_PROGRESS: " + task1 + "\n");
//
//        subtask1.setStatus(TaskStatus.IN_PROGRESS);
//        subtask2.setStatus(TaskStatus.DONE);
//        subtask3.setStatus(TaskStatus.DONE);
//
//        System.out.println("Статусы подзадач изменены на IN_PROGRESS, DONE, DONE:" + manager.getSubtasks() + "\n");
//
//        manager.updateEpicStatus(epic1);
//        manager.updateEpicStatus(epic2);
//
//        manager.getTask(10);
//        manager.getTask(9);
//        manager.getTask(8);
//        manager.getTask(7);
//        manager.getTask(6);
//        manager.getTask(5);
//        manager.getTask(5);
//        manager.getTask(4);
//        manager.getTask(3);
//        manager.getTask(2);
//        manager.getTask(2);
//        manager.getTask(11);
//        manager.getTask(12);
//        manager.getTask(13);
//        manager.getTask(14);
//
//        System.out.println("Статусы эпиков пересчитаны:" + manager.getEpics() + "\n");
//
//        System.out.println("\nИстория просмотра: " + manager.getHistory() + "\n");
//
//        System.out.println("И, наконец, попробуйте удалить одну из задач и один из эпиков.\n");
//
//        manager.removeTask(task1);
//        System.out.println("Задача task1 удалена. Список задач: " + manager.getTasks() + "\n");
//
//        manager.removeEpic(19);
//
//        System.out.println("Эпик с ID 3 удалён. Списки оставшихся подзадач и эпиков:\n");
//        System.out.println(manager.getSubtasks());
//        System.out.println(manager.getEpics());
//
//        System.out.println("\nИстория просмотра:\n" + manager.getHistory() + "\n");
//    }
}}