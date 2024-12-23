package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;

import java.util.List;

public interface TaskManager {

    // получение списка задач
    List getTasks();

    // удаление всех задач
    void deleteTasks();

    // получение задачи по ID
    Task getTask(int id);

    // добавление задачи
    void addTask(Task task);

    // обновление задачи
    void updateTask(Task task);

    // удаление задачи
    void removeTask(Task task);

    // удаление задачи по ID
    void removeTaskByID(int id);

    // получение списка подзадач
    List getSubtasks();

    // удаление всех подзадач
    void deleteSubtasks();

    // получение подзадачи по ID
    Subtask getSubtask(int id);

    // добавление подзадачи
    void addSubtask(Subtask subtask);

    // обновление подзадачи
    void updateSubtask(Subtask subtask);

    // удаление подзадачи
    void removeSubtask(Subtask subtask);

    // удаление подзадачи по ID
    void removeSubtaskByID(int id);

    // получение списка эпиков
    List getEpics();

    // удаление всех эпиков
    void deleteEpics();

    // получение эпика по ID
    Epic getEpic(int id);

    // добавление эпика
    void addEpic(Epic epic);

    // обновление эпика
    void updateEpic(Epic epic);

    // удаление эпика по ID
    void removeEpic(int id);

    // обновление статуса эпика
    void updateEpicStatus(Epic epic);

    // Получение списка всех подзадач определённого эпика
    List<Subtask> getSubtasksByEpic(Epic epic);

    List getHistory();
}
