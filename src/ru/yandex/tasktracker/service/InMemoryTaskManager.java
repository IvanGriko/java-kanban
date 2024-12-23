package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    public int taskCount = 1;                  // счётчик задач

    public int getTaskCount() {
        return taskCount;
    }

    final Map<Integer, Task> tasksMap = new HashMap<>();
    final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    // получение списка задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    // удаление всех задач
    @Override
    public void deleteTasks() {
        for (Task task : tasksMap.values())
            historyManager.remove(task.getId());
        tasksMap.clear();
    }

    // получение задачи по ID
    @Override
    public Task getTask(int id) {
        Task task = tasksMap.get(id);
        historyManager.add(task);
        return task;
    }

    // добавление задачи
    @Override
    public void addTask(Task task) {
        tasksMap.put(task.getId(), task);
        taskCount++;
    }

    // обновление задачи
    @Override
    public void updateTask(Task task) {
        tasksMap.replace(task.getId(), task);
    }

    // удаление задачи
    @Override
    public void removeTask(Task task) {
        tasksMap.remove(task.getId());
        historyManager.remove(task.getId());
    }

    // удаление задачи по ID
    @Override
    public void removeTaskByID(int id) {
        tasksMap.remove(id);
        historyManager.remove(id);
    }

    // получение списка подзадач
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    // удаление всех подзадач
    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasksMap.values())
            historyManager.remove(subtask.getId());
        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.clearSubtasks();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    // получение подзадачи по ID
    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasksMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // добавление подзадачи
    @Override
    public void addSubtask(Subtask subtask) {
        subtasksMap.put(subtask.getId(), subtask);
        taskCount++;
        updateEpicStatus(getEpic(subtask.getEpic()));
    }

    // обновление подзадачи
    @Override
    public void updateSubtask(Subtask subtask) {
        subtasksMap.replace(subtask.getId(), subtask);
        updateEpicStatus(getEpic(subtask.getEpic()));
    }

    // удаление подзадачи
    @Override
    public void removeSubtask(Subtask subtask) {
        subtasksMap.remove(subtask.getId());
        updateEpicStatus(getEpic(subtask.getEpic()));
        historyManager.remove(subtask.getId());
    }

    // удаление подзадачи по ID
    @Override
    public void removeSubtaskByID(int id) {
        Subtask subtask = subtasksMap.get(id);
        int epicID = subtask.getEpic();
        subtasksMap.remove(id);
        Epic epic = epicsMap.get(epicID);
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);
        epic.setSubtaskList(subtaskList);
        updateEpicStatus(epic);
        historyManager.remove(id);
    }

    // получение списка эпиков
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    // удаление всех эпиков
    @Override
    public void deleteEpics() {
        for (Epic epic : epicsMap.values())
            historyManager.remove(epic.getId());
        epicsMap.clear();
        subtasksMap.clear();
    }

    // получение эпика по ID
    @Override
    public Epic getEpic(int id) {
        Epic epic = epicsMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    // добавление эпика
    @Override
    public void addEpic(Epic epic) {
        epicsMap.put(epic.getId(), epic);
        taskCount++;
    }

    // обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        epicsMap.replace(epic.getId(), epic);
    }

    // удаление эпика по ID
    @Override
    public void removeEpic(int id) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(getEpic(id));
        for (Subtask subtask : epicSubtasks) {
            subtasksMap.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        epicsMap.remove(id);
        historyManager.remove(id);
    }

    // обновление статуса эпика
    @Override
    public void updateEpicStatus(Epic epic) {
        int isDoneCount = 0;
        int isNewCount = 0;
        List<Subtask> list = getSubtasksByEpic(epic);

        for (Subtask subtask : list) {
            if (subtask.getStatus() == TaskStatus.DONE) {
                isDoneCount++;
            }
            if (subtask.getStatus() == TaskStatus.NEW) {
                isNewCount++;
            }
        }
        if (isDoneCount == list.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (isNewCount == list.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        updateEpic(epic);
    }

    // Получение списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasksList = new ArrayList<>(subtasksMap.values());
        ArrayList<Subtask> subtasksListByEpic = new ArrayList<>();
        for (Subtask subt : subtasksList) {
            if (epic.getId() == subt.getEpic()) {
                subtasksListByEpic.add(subt);
            }
        }
        epic.setSubtaskList(subtasksListByEpic);
        return subtasksListByEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
