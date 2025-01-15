package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    Set<Task> getPrioritizedTasks();

    void checkTimeOverlapping(Task task);

    List getTasks();

    void removeTasks();

    Task getTaskById(int id);

    void addTask(Task task);

    void updateTask(Task task);

    void removeTask(Task task);

    void removeTaskByID(int id);

    List getSubtasks();

    void removeSubtasks();

    Subtask getSubtaskById(int id);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtask(Subtask subtask);

    void removeSubtaskByID(int id);

    List getEpics();

    void removeEpics();

    Epic getEpicByIdWithoutMemorize(int id);

    Epic getEpicById(int id);

    void addEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicById(int id);

    void updateEpicStatus(Epic epic);

    List<Subtask> getSubtasksByEpic(Epic epic);

    List getHistory();

    void removeFromHistory(Integer id);
}
