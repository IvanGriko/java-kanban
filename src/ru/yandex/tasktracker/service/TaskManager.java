package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;

import java.util.List;

public interface TaskManager {


    // получение списка задач
    List getTasks() throws ManagerSaveException;

    // удаление всех задач
    void deleteTasks() throws ManagerSaveException;

    // получение задачи по ID
    Task getTask(int id) throws ManagerSaveException;

    // добавление задачи
    void addTask(Task task) throws ManagerSaveException;

    // обновление задачи
    void updateTask(Task task) throws ManagerSaveException;

    // удаление задачи
    void removeTask(Task task) throws ManagerSaveException;

    // удаление задачи по ID
    void removeTaskByID(int id) throws ManagerSaveException;

    // получение списка подзадач
    List getSubtasks() throws ManagerSaveException;

    // удаление всех подзадач
    void deleteSubtasks() throws ManagerSaveException;

    // получение подзадачи по ID
    Subtask getSubtask(int id) throws ManagerSaveException;

    // добавление подзадачи
    void addSubtask(Subtask subtask) throws ManagerSaveException;

    // обновление подзадачи
    void updateSubtask(Subtask subtask) throws ManagerSaveException;

    // удаление подзадачи
    void removeSubtask(Subtask subtask) throws ManagerSaveException;

    // удаление подзадачи по ID
    void removeSubtaskByID(int id) throws ManagerSaveException;

    // получение списка эпиков
    List getEpics() throws ManagerSaveException;

    // удаление всех эпиков
    void deleteEpics() throws ManagerSaveException;

    // получение эпика по ID
    Epic getEpic(int id) throws ManagerSaveException;

    // добавление эпика
    void addEpic(Epic epic) throws ManagerSaveException;

    // обновление эпика
    void updateEpic(Epic epic) throws ManagerSaveException;

    // удаление эпика по ID
    void removeEpic(int id) throws ManagerSaveException;

    // обновление статуса эпика
    void updateEpicStatus(Epic epic) throws ManagerSaveException;

    // Получение списка всех подзадач определённого эпика
    List<Subtask> getSubtasksByEpic(Epic epic) throws ManagerSaveException;

    List getHistory();
}
