package ru.yandex.tasktracker.service;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    public int taskCount = 1;                  // счётчик задач

    public int getTaskCount() {
        return taskCount;
    }
    Map<Integer, Task> tasksMap = new HashMap<>();
    Map<Integer, Subtask> subtasksMap = new HashMap<>();
    Map<Integer, Epic> epicsMap = new HashMap<>();
    HistoryManager historyManager = new InMemoryHistoryManager();

    // получение списка задач
    @Override
    public ArrayList getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>(tasksMap.values());
        return tasksList;
    }

    // удаление всех задач
    @Override
    public void deleteTasks() {
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
    }

    // удаление задачи по ID
    @Override
    public void removeTaskByID(int id) {
        tasksMap.remove(id);
    }

    // получение списка подзадач
    @Override
    public ArrayList getSubtasks() {
        ArrayList<Subtask> subtasksList = new ArrayList<>(subtasksMap.values());
        return subtasksList;
    }

    // удаление всех подзадач
    @Override
    public void deleteSubtasks() {
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
    }

    // получение списка эпиков
    @Override
    public ArrayList getEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>(epicsMap.values());
        return epicsList;
    }

    // удаление всех эпиков
    @Override
    public void deleteEpics() {
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
        ArrayList<Subtask> epicSubtasks = getSubtasksByEpic(getEpic(id));
        for (Subtask subtask : epicSubtasks) {
            subtasksMap.remove(subtask.getId());
        }
        epicsMap.remove(id);
    }

    // обновление статуса эпика
    @Override
    public void updateEpicStatus(Epic epic) {
        int isDoneCount = 0;
        int isNewCount = 0;
        ArrayList<Subtask> list = getSubtasksByEpic(epic);

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
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksList = new ArrayList<>(subtasksMap.values());
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
    public ArrayList getHistory() {
        return historyManager.getHistory();
    }
}
