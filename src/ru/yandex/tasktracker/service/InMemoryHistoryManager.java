package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> historyList = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        Task gotTask = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
        historyList.add(gotTask);
    }
    //        historyList.add(task);
//        если просто добавлять задачу без создания копии текущей версии,
//        то не проходит тест на разные версии объектов в истории:
//        "Сохранена одна и та же версия задачи. ==> expected: <false> but was: <true>".
//        Прошу меня направить на решение, если возможно.

    @Override
    public ArrayList getHistory() {
        return new ArrayList<>(historyList);
    }
}
