package ru.yandex.tasktracker.service;

import ru.yandex.tasktracker.model.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void removeFromHistory(Integer id);

    List<Task> getHistory();
}
