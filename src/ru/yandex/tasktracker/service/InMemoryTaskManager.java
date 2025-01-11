package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    public int taskCount = 1;                  // счётчик задач

    private final HistoryManager historyManager = new InMemoryHistoryManager();
    final Map<Integer, Task> tasksMap = new HashMap<>();
    final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected Set<Task> sortedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getId() == (task2.getId())) {
            return 0;
        } else if (task1.getStartTime().isAfter(task2.getStartTime()) ||
                task1.getStartTime().isEqual(task2.getStartTime())) {
            return 1;
        } else {
            return -1;
        }
    });

    @Override
    public Set<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    @Override
    public void taskTimeCheck(Task task) {
        Optional<Task> matchingTask = sortedTasks.stream()
                .filter(t -> t.getId() == task.getId())
                .findFirst();
        if (matchingTask.isPresent()) {
            return;
        }
        sortedTasks.stream().forEach(t -> {
            if ((t.getEndTime().isAfter(task.getStartTime()) && task.getStartTime().isBefore(t.getEndTime())) ||
                    (t.getStartTime().equals(task.getStartTime()) && task.getEndTime().equals(t.getEndTime())) ||
                    (t.getEndTime().equals(task.getStartTime()) && task.getStartTime().equals(t.getEndTime()))) {
                throw new ManagerSaveException("Пересечение по времени с задачей " + task);
            }
        });
    }

    public int getTaskCount() {
        return taskCount;
    }

    // получение списка задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    // удаление всех задач
    @Override
    public void deleteTasks() {
        for (Task task : tasksMap.values()) {
            sortedTasks.remove(task);
            historyManager.remove(task.getId());
        }
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
        taskTimeCheck(task);
        tasksMap.put(task.getId(), task);
        sortedTasks.add(task);
        taskCount++;
    }

    // обновление задачи
    @Override
    public void updateTask(Task task) {
        taskTimeCheck(task);
        tasksMap.replace(task.getId(), task);
        sortedTasks.add(task);
    }

    // удаление задачи
    @Override
    public void removeTask(Task task) {
        tasksMap.remove(task.getId());
        sortedTasks.remove(task);
        historyManager.remove(task.getId());
    }

    // удаление задачи по ID
    @Override
    public void removeTaskByID(int id) {
        sortedTasks.remove(tasksMap.get(id));
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
        for (Subtask subtask : subtasksMap.values()) {
            sortedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
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
        taskTimeCheck(subtask);
        subtasksMap.put(subtask.getId(), subtask);
        sortedTasks.add(subtask);
        taskCount++;
        updateEpicStatus(getEpic(subtask.getEpic()));
        getEpic(subtask.getEpic()).getSubtaskList().add(subtask);
    }

    // обновление подзадачи
    @Override
    public void updateSubtask(Subtask subtask) {
        taskTimeCheck(subtask);
        subtasksMap.replace(subtask.getId(), subtask);
        sortedTasks.add(subtask);
        updateEpicStatus(getEpic(subtask.getEpic()));
        getEpic(subtask.getEpic()).getSubtaskList().add(subtask);
    }

    // удаление подзадачи
    @Override
    public void removeSubtask(Subtask subtask) {
        sortedTasks.remove(subtask);
        subtasksMap.remove(subtask.getId());
        updateEpicStatus(getEpic(subtask.getEpic()));
        historyManager.remove(subtask.getId());
        getEpic(subtask.getEpic()).getSubtaskList().remove(subtask);
    }

    // удаление подзадачи по ID
    @Override
    public void removeSubtaskByID(int id) {
        Subtask subtask = subtasksMap.get(id);
        int epicID = subtask.getEpic();
        sortedTasks.remove(subtask);
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
        return subtasksMap.values().stream()
                .filter(subtask -> epic.getId() == subtask.getEpic())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
