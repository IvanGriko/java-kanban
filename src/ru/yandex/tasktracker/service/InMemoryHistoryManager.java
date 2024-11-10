package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> historyList = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        Task gotTask = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
        historyList.add(gotTask);
    }

    @Override
    public ArrayList getHistory() {
        return historyList;
    }
}
