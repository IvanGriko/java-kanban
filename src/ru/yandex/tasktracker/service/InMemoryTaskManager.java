package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.tasktracker.model.Epic;
import ru.yandex.tasktracker.model.Subtask;
import ru.yandex.tasktracker.model.Task;
import ru.yandex.tasktracker.model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;

public class InMemoryTaskManager implements TaskManager {

    public int taskCount = 1;

    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected Set<Task> sortedTasks = new TreeSet<>(comparing(Task::getStartTime, nullsLast(LocalDateTime::compareTo)));

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Set<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    @Override
    public void checkTimeOverlapping(Task task) {
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
                throw new ManagerSaveException("Пересечение по времени с задачей " + t.getName());
            }
        });
    }

    public int getTaskCount() {
        return taskCount;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    @Override
    public void removeTasks() {
        for (Task task : tasksMap.values()) {
            sortedTasks.remove(task);
            historyManager.removeFromHistory(task.getId());
        }
        tasksMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasksMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void addTask(Task task) {
        checkTimeOverlapping(task);
        tasksMap.put(task.getId(), task);
        sortedTasks.add(task);
        taskCount++;
    }

    @Override
    public void updateTask(Task task) {
        checkTimeOverlapping(task);
        tasksMap.replace(task.getId(), task);
        sortedTasks.remove(task);
        sortedTasks.add(task);
    }

    @Override
    public void removeTask(Task task) {
        tasksMap.remove(task.getId());
        sortedTasks.remove(task);
        historyManager.removeFromHistory(task.getId());
    }

    @Override
    public void removeTaskByID(int id) {
        sortedTasks.remove(tasksMap.get(id));
        tasksMap.remove(id);
        historyManager.removeFromHistory(id);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : subtasksMap.values()) {
            sortedTasks.remove(subtask);
            historyManager.removeFromHistory(subtask.getId());
        }
        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.clearSubtasks();
            epic.setStatus(TaskStatus.NEW);
            epic.calculateTime();
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasksMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        checkTimeOverlapping(subtask);
        subtasksMap.put(subtask.getId(), subtask);
        sortedTasks.add(subtask);
        taskCount++;
        Epic tmpEpic = getEpicByIdWithoutMemorize(subtask.getEpicId());
        tmpEpic.getSubtaskList().add(subtask);
        updateEpicStatus(tmpEpic);
        tmpEpic.calculateTime();
        updateEpic(tmpEpic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        checkTimeOverlapping(subtask);
        subtasksMap.replace(subtask.getId(), subtask);
        sortedTasks.remove(subtask);
        sortedTasks.add(subtask);
        Epic tmpEpic = getEpicByIdWithoutMemorize(subtask.getEpicId());
        tmpEpic.getSubtaskList().remove(subtask);
        tmpEpic.getSubtaskList().add(subtask);
        updateEpicStatus(tmpEpic);
        tmpEpic.calculateTime();
        updateEpic(tmpEpic);
    }

    @Override
    public void removeSubtask(Subtask subtask) {
        sortedTasks.remove(subtask);
        subtasksMap.remove(subtask.getId());
        Epic tmpEpic = getEpicByIdWithoutMemorize(subtask.getEpicId());
        tmpEpic.getSubtaskList().remove(subtask);
        updateEpicStatus(tmpEpic);
        tmpEpic.calculateTime();
        updateEpic(tmpEpic);
        historyManager.removeFromHistory(subtask.getId());
    }

    @Override
    public void removeSubtaskByID(int id) {
        Subtask subtask = subtasksMap.get(id);
        int epicID = subtask.getEpicId();
        sortedTasks.remove(subtasksMap.get(id));
        subtasksMap.remove(id);
        Epic epic = epicsMap.get(epicID);
        ArrayList<Subtask> subtList = epic.getSubtaskList();
        subtList.remove(subtask);
        epic.setSubtaskList(subtList);
        updateEpicStatus(epic);
        epic.calculateTime();
        updateEpic(epic);
        historyManager.removeFromHistory(id);
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public void removeEpics() {
        for (Subtask subtask : subtasksMap.values()) {
            sortedTasks.remove(subtask);
            historyManager.removeFromHistory(subtask.getId());
        }
        for (Epic epic : epicsMap.values())
            historyManager.removeFromHistory(epic.getId());
        epicsMap.clear();
        subtasksMap.clear();
    }

    @Override
    public Epic getEpicByIdWithoutMemorize(int id) {
        return epicsMap.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicsMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void addEpic(Epic epic) {
        epicsMap.put(epic.getId(), epic);
        taskCount++;
    }

    @Override
    public void updateEpic(Epic epic) {
        epicsMap.replace(epic.getId(), epic);
    }

    @Override
    public void removeEpicById(int id) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(getEpicByIdWithoutMemorize(id));
        for (Subtask subtask : epicSubtasks) {
            sortedTasks.remove(subtask);
            subtasksMap.remove(subtask.getId());
            historyManager.removeFromHistory(subtask.getId());
        }
        epicsMap.remove(id);
        historyManager.removeFromHistory(id);
    }

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

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return subtasksMap.values().stream()
                .filter(subtask -> epic.getId() == subtask.getEpicId())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void removeFromHistory(Integer id) {
        historyManager.removeFromHistory(id);
    }
}
